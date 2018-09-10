package com.bodytel.remapv2.ui.welcome;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.base.ServiceConnectionActivity;
import com.bodytel.remapv2.ui.debug.DebugActivity;
import com.bodytel.util.lib.network.firebase.FirebaseUtil;
import com.bodytel.util.lib.worker.FitDataCollectorWorker;
import com.bodytel.util.lib.worker.StoreDistanceDataWorker;
import com.bodytel.util.lib.worker.StoreSensorDataWorker;
import com.bodytel.util.lib.worker.StoreStepsDataWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class WelcomeActivity extends ServiceConnectionActivity {
    private WorkManager mWorkManager;

    private TextView txtSubjectId;

    private PrefGlobal prefGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mWorkManager = WorkManager.getInstance();

        txtSubjectId = findViewById(R.id.txt_subject_id);

        prefGlobal = PrefHelper.providePrefGlobal();

        String subjectId = prefGlobal.getSubjectId();
        if(TextUtils.isEmpty(subjectId)) inputSubjectId();
        else {
            txtSubjectId.setText(subjectId);

            FirebaseUtil.on().init();
            startFirestoreWorks();
        }
    }

    public void onClickDebug(View view){
        startActivity(new Intent(this, DebugActivity.class));
    }

    private void inputSubjectId(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.prompt_input_subject_id, null);

        final EditText etSubjectId = view.findViewById(R.id.prompt_input_subject_id_et_subject_id);
        TextView txtOk = view.findViewById(R.id.prompt_input_subject_id_txt_ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        txtOk.setOnClickListener(v -> {
            String subjectId = etSubjectId.getText().toString();

            if(TextUtils.isEmpty(subjectId))
                Toast.makeText(WelcomeActivity.this, "Please, insert a valid subject ID", Toast.LENGTH_SHORT).show();
            else {
                prefGlobal.setSubjectId(subjectId);
                prefGlobal.setBdiVersion("1.0");
                txtSubjectId.setText(subjectId);
                dialog.dismiss();

                FirebaseUtil.on().init();
                startFirestoreWorks();
            }
        });

        dialog.show();
    }

    void startFirestoreWorks() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWork1 = new PeriodicWorkRequest
                .Builder(FitDataCollectorWorker.class, 12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(AppConst.JOB_TAG_COLLECT_FIT_DATA)
                .build();

        PeriodicWorkRequest periodicWork2 = new PeriodicWorkRequest
                .Builder(StoreStepsDataWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .addTag(AppConst.JOB_TAG_SEND_STEPS_DATA_TO_REMOTE)
                .build();

        PeriodicWorkRequest periodicWork3 = new PeriodicWorkRequest
                .Builder(StoreDistanceDataWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .addTag(AppConst.JOB_TAG_SEND_DISTANCE_DATA_TO_REMOTE)
                .build();

        PeriodicWorkRequest periodicWork4 = new PeriodicWorkRequest
                .Builder(StoreSensorDataWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE)
                .build();

        mWorkManager.enqueue(periodicWork1, periodicWork2, periodicWork3, periodicWork4);
    }
}
