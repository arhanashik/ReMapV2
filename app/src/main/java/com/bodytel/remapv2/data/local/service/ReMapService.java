package com.bodytel.remapv2.data.local.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataService;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.util.helper.NumberUtil;

public class ReMapService extends Service implements SensorEventListener {
    /**
     * Command to the service to display a message
     */
    public static final int MSG_SAY_HELLO = 1;
    public static final int RUN_FOREGROUND = 2;
    public static final int RUN_BACKGROUND = 3;

    public static final String START_FOREGROUND_ACTION = "com.bodytel.remapv2.action.startforeground";
    public static final String STOP_FOREGROUND_ACTION = "com.bodytel.remapv2.action.stopforeground";
    public static final int FOREGROUND_SERVICE_ID = 101;

    public static final String CHANNEL_NAME = "remap";
    public static final String CHANNEL_ID = "remap_notification_channel";

    private Notification mServiceNotification;
    private boolean mIsForeground = false;
    private int mVisibleActivities = 0;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private volatile float totalX = 0.0f, totalY = 0.0f, totalZ = 0.0f;
    private volatile float itemCount = 0.0f;
    private float lastX, lastY, lastZ;

    private Handler handler;
    private HandlerThread handlerThread;

    private AccelerometerDataService accelerometerDataService;

    @Override
    public void onCreate() {
        super.onCreate();

        accelerometerDataService = DatabaseHelper.provideAccelerometerDataService();

        handlerThread = new HandlerThread("min_count");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());

        Intent stopForegroundIntent = new Intent(this, ReMapService.class);
        //stopForegroundIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingIntent
                = PendingIntent.getService(this, 0, stopForegroundIntent, 0);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            //noinspection deprecation
            builder = new NotificationCompat.Builder(this);
        }
        Resources resources = getResources();
        mServiceNotification = builder.setAutoCancel(false)
                .setTicker(resources.getString(R.string.app_name))
                .setContentTitle("ReMap")
                .setContentText("Needs to be running for data collection")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setNumber(100)
                .build();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        calculateMin();
    }


    private void calculateMin() {
        handler.postDelayed(() -> {
            //Log.e("SensorActivity","Min X ="+totalX/itemCount+" Min Y ="+totalY/itemCount+" Min Z ="+totalZ/itemCount);

            long time = System.currentTimeMillis();
            float X = NumberUtil.floorToOneDecimalPoint(totalX / itemCount);
            float Y = NumberUtil.floorToOneDecimalPoint(totalY / itemCount);
            float Z = NumberUtil.floorToOneDecimalPoint(totalZ / itemCount);

            if (lastX != X || lastY != Y || lastZ != Z) {
                AccelerometerDataModel model = new AccelerometerDataModel(
                        time,
                        X,
                        Y,
                        Z,
                        false
                );
                accelerometerDataService.insert(model);
                lastX = X;
                lastY = Y;
                lastZ = Z;

                String value = "(" + model.getXMean() + ", " + model.getYMean() + ", " + model.getZMean() + ")";
                Log.e("ReMapService", "Time =" + time + " value =" + value);
            }

            totalX = 0.0f;
            totalY = 0.0f;
            totalZ = 0.0f;
            itemCount = 0.0f;
            calculateMin();
        }, 1000);
    }


    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                case RUN_BACKGROUND:
                    startInForeground();
                    break;
                case RUN_FOREGROUND:
                    stopForeground(true);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (action != null && action.equals(STOP_FOREGROUND_ACTION)) {
            if (mIsForeground) {
                stopForeground(true);
                mIsForeground = false;
            }
            stopSelf();

        } else if (action != null && action.equals(START_FOREGROUND_ACTION)) {
            startInForeground();
        }
        return START_STICKY;
    }

    private void startInForeground() {
        startForeground(FOREGROUND_SERVICE_ID, mServiceNotification);
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float mSensorX = event.values[0];
        float mSensorY = event.values[1];
        float mSensorZ = event.values[2];

        itemCount++;
        totalX = totalX + mSensorX;
        totalY = totalY + mSensorY;
        totalZ = totalZ + mSensorZ;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
