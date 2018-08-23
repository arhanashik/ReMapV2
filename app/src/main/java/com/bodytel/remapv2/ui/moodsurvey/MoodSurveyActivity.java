package com.bodytel.remapv2.ui.moodsurvey;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyEndFragment;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.StoreMoodSurveyCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MoodSurveyActivity extends AppCompatActivity implements OnMoodSurveyEvent, StoreMoodSurveyCallback{

    private MenuItem menuItem;

    private PrefGlobal prefGlobal;
    private int newMoodSurveyVersion;
    private boolean isSurveyDone;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("Step 1 of 2");

        prefGlobal = PrefHelper.providePrefGlobal();

        MoodSurveyFragment fragment = new MoodSurveyFragment();
        fragment.setMoodSurveyListener(this);
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
                .replace(R.id.activity_mood_survey_container, fragment)
                .commit();
    }

    @Override
    public void onMoodSurveyAnswer(int mood) {
        storeMoodSurveyData(mood);
    }

    @Override
    public void onStoreMoodSurveySuccessfully(@NotNull MoodSurveyResultModel resultModel) {
        showProgress(false);

        setTitle("Step 2 of 2");
        menuItem.setTitle(getString(R.string.label_done));
        isSurveyDone = true;
        replaceFragment(new BdiSurveyEndFragment());
        prefGlobal.setLastMoodSurveyVersion(newMoodSurveyVersion);
    }

    @Override
    public void onStoreMoodSurveyFailure(@NotNull String error) {
        showProgress(false);

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void storeMoodSurveyData(int mood){
        showProgress(true);

        newMoodSurveyVersion = prefGlobal.getLastMoodSurveyVersion() + 1;

        MoodSurveyResultModel resultModel = new MoodSurveyResultModel(
                0,
                "",
                prefGlobal.getSubjectId(),
                System.currentTimeMillis(),
                newMoodSurveyVersion,
                mood,
                false
        );

        NetworkApi.on().storeMoodSurveyData(resultModel, this);
    }

    private void showProgress(boolean show){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Storing data...");
        }

        if(show){
            if(!progressDialog.isShowing()) progressDialog.show();
        } else{
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}
