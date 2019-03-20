package com.nielsmasdorp.speculum.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nielsmasdorp.speculum.activity.MainActivity;

public class MotionDetectorReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "MotionDetector";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Intent received: " + intent.getAction());

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
    }
}
