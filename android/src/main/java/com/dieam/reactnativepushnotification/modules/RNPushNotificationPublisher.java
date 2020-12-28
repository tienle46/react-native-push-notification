package com.dieam.reactnativepushnotification.modules;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.security.SecureRandom;

import static com.dieam.reactnativepushnotification.modules.RNPushNotification.LOG_TAG;

public class RNPushNotificationPublisher extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "notificationId";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        long currentTime = System.currentTimeMillis();

        Log.i(LOG_TAG, "NotificationPublisher: Prepare To Publish: " + id + ", Now Time: " + currentTime);

        Application applicationContext = (Application) context.getApplicationContext();

        new RNPushNotificationHelper(applicationContext)
                .sendToNotificationCentre(intent.getExtras());
        notifyNotification(context, id);
    }

    private void notifyNotification(Context context, int id) {
        final Bundle bundle = new Bundle();
        bundle.putInt(NOTIFICATION_ID, id);
        Boolean isForeground = isApplicationInForeground(context);
        bundle.putBoolean("foreground", isForeground);
        bundle.putBoolean("userInteraction", false);
        ReactApplication rnApp = (ReactApplication) context.getApplicationContext();
        ReactApplicationContext reactContext =(ReactApplicationContext) rnApp.getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
        RNPushNotificationJsDelivery jsDelivery = new RNPushNotificationJsDelivery(reactContext);
        jsDelivery.notifyNotification(bundle);

    }

    private boolean isApplicationInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String d : processInfo.pkgList) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}