package com.bodytel.util.helper;

import android.content.Context;
import android.util.Log;

import com.bodytel.ReMapApp;
import com.bodytel.remapv2.data.local.AppConst;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class ScheduledJobHelper {

    private static ScheduledJobHelper scheduledJobHelper = null;

    public static ScheduledJobHelper on() {
        if(scheduledJobHelper == null) scheduledJobHelper = new ScheduledJobHelper();

        return scheduledJobHelper;
    }

    public void init(){
        scheduleJob(ReMapApp.getContext());
    }

    public static void scheduleJob(Context context) {
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //creating new job and adding it with dispatcher
        Job job = createJob(dispatcher);
        dispatcher.mustSchedule(job);
        Log.d(ScheduledJobHelper.class.getSimpleName(), "scheduleJob: " + "jobScheduled");
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){
        final int periodicity = (int) TimeUnit.HOURS.toSeconds(6); // Every 1 hour periodicity expressed as seconds
        final int toleranceInterval = (int) TimeUnit.MINUTES.toSeconds(15); // a small(ish) window of time when triggering is OK

        return dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(ScheduledJobService.class)
                //unique id of the task
                .setTag(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE)
                //overwrite/don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 0 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING)
                .build();
    }

    public static Job updateJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                //update if any task with the given tag exists.
                .setReplaceCurrent(true)
                //Integrate the job you want to start.
                .setService(ScheduledJobService.class)
                .setTag(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(30, 60))
                .build();
    }

    public void cancelJob(Context context){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //Cancel all the jobs for this package
        dispatcher.cancelAll();
        // Cancel the job for this tag
        dispatcher.cancel(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE);

    }
}
