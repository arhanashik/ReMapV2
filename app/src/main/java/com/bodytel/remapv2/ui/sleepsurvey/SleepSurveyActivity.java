package com.bodytel.remapv2.ui.sleepsurvey;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyEndFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedHashMap;
import java.util.Map;

public class SleepSurveyActivity extends AppCompatActivity implements OnSleepSurveyEvent{

    private boolean isSurveyDone;
    private MenuItem menuItem;

    private FirebaseFirestore db;
    private PrefGlobal prefGlobal;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Step 1 of 2");

        db = FirebaseFirestore.getInstance();
        prefGlobal = PrefHelper.providePrefGlobal();

        SleepSurveyFragment fragment = new SleepSurveyFragment();
        fragment.setSleepSurveyListener(this);
        replaceFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        menuItem = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_cancel){
            if(!isSurveyDone) {
                Toast.makeText(this, "Survey canceled", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_sleep_survey_container, fragment)
                .commit();
    }

    @Override
    public void onSleepSurveyAnswer(int hourOfSleep) {
        storeSleepSurveyData(hourOfSleep);
    }

    private void storeSleepSurveyData(int hourOfSleep){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while storing data...");
        progressDialog.show();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(AppConst.SUBJECT_ID, prefGlobal.getSubjectId());
        data.put(AppConst.CREATED_AT, System.currentTimeMillis());
        final int newSleepSurveyVersion = prefGlobal.getLastSleepSurveyVersion() + 1;
        data.put(AppConst.SURVEY_VERSION, newSleepSurveyVersion);
        data.put(AppConst.ANSWER, hourOfSleep);

        db.collection(AppConst.COLLECTION_SLEEP_SURVEY_DATA)
                .add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressDialog.dismiss();

                        setTitle("Step 2 of 2");
                        menuItem.setTitle(getString(R.string.label_done));
                        isSurveyDone = true;
                        replaceFragment(new BdiSurveyEndFragment());
                        prefGlobal.setLastSleepSurveyVersion(newSleepSurveyVersion);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(SleepSurveyActivity.this, e.getMessage() + ". Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
