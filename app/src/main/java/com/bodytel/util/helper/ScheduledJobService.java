package com.bodytel.util.helper;

import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class ScheduledJobService extends JobService {

    private static final String TAG = ScheduledJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "onStartJob: " + "jobStarted");
        new Thread(() -> codeYouWantToRun(params)).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public void codeYouWantToRun(final JobParameters parameters) {
        try {
            FileUtil.addNewLogOnSD();
            Log.d(TAG, "onStartJob: " + "jobFinished");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Tell the framework that the job has completed and needs to be reschedule
            jobFinished(parameters, true);
        }
    }
}
