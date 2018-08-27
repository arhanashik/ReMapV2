package com.bodytel.remapv2.ui.listdata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.listdata.SensorModel;

import java.util.Objects;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private HandlerThread handlerThread;
    private Handler handler;
    SensorDataAdapter dataAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        handlerThread = new HandlerThread("min_count");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        initView();

        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void initView(){
        recyclerView = findViewById(R.id.activity_list_data_recycler_view);
        dataAdapter = new SensorDataAdapter();
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        calculateMin();
    }



    private void calculateMin(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.e("SensorActivity","Min X ="+totalX/itemCount+" Min Y ="+totalY/itemCount+" Min Z ="+totalZ/itemCount);

                long time = System.currentTimeMillis();
                float X = totalX/itemCount;
                float Y = totalY/itemCount;
                float Z = totalZ/itemCount;

                String value = "("+String.format("%.2f", X)+", "+String.format("%.2f", Y)+", "+String.format("%.2f",Z)+")";
                showData(new SensorModel(time, value));
                totalX = 0.0f;
                totalY = 0.0f;
                totalZ = 0.0f;
                itemCount = 0.0f;
                calculateMin();
            }
        }, 1000);
    }


    private void showData(SensorModel sensorModel){
        runOnUiThread(()->{
           dataAdapter.addItem(sensorModel);
           recyclerView.scrollToPosition(dataAdapter.getItemCount()-1);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    volatile float totalX = 0.0f, totalY = 0.0f, totalZ = 0.0f;
    volatile float itemCount = 0.0f;


    @Override
    public void onSensorChanged(SensorEvent event) {
        float mSensorX = event.values[0];
        float mSensorY = event.values[1];
        float mSensorZ = event.values[2];

        itemCount ++;
        totalX = totalX+mSensorX;
        totalY = totalY + mSensorY;
        totalZ = totalZ + mSensorZ;


        //Log.v("SensorActivity","X ="+mSensorX+" Y ="+mSensorY+" Z ="+mSensorZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
