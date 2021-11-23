package com.ym.game.plugin.adjust.event;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdjustEvent {

    private volatile static AdjustEvent INSTANCE;
    private String adjustToken = "v9k3vlxukqo0";
    public static AdjustEvent getInstance() {
        if (INSTANCE == null) {
            synchronized (AdjustEvent.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdjustEvent();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Application context,boolean debug){
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;

        if (debug){
             environment = AdjustConfig.ENVIRONMENT_SANDBOX;

        }
        AdjustConfig config = new AdjustConfig(context, adjustToken, environment);
//        AdjustConfig config = new AdjustConfig(context, adjustToken, environment,true);//设置第三个参数为true就开启
        // Evaluate the deeplink to be launched.
//        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
//            @Override
//            public boolean launchReceivedDeeplink(Uri deeplink) {
//                // ...
//                if (shouldAdjustSdkLaunchTheDeeplink(deeplink)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        Adjust.onCreate(config);
        if (debug){
            config.setLogLevel(LogLevel.VERBOSE); // enable all logs

        }
//        config.setLogLevel(LogLevel.DEBUG); // disable verbose logs
//        config.setLogLevel(LogLevel.INFO); // disable debug logs (default)
//        config.setLogLevel(LogLevel.WARN); // disable info logs
//        config.setLogLevel(LogLevel.ERROR); // disable warning logs
//        config.setLogLevel(LogLevel.ASSERT); // disable error logs
//        config.setLogLevel(LogLevel.SUPRESS); // disable all logs
        context.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    //通过深度链接的再归因
    public void appwithOpenUrl(Activity context,Intent intent){
        if (intent==null){
            intent = context.getIntent();
        }
        Uri data = intent.getData();
        Adjust.appWillOpenUrl(data, context.getApplicationContext());
    }

    public void trackEvent(String eventToken){

        com.adjust.sdk.AdjustEvent adjustEvent = new com.adjust.sdk.AdjustEvent(eventToken);
        Adjust.trackEvent(adjustEvent);
    }

    //跟踪收入
    public void trackEventWithRevenue(String eventToken,double revenue,String orderId,String currency){
        com.adjust.sdk.AdjustEvent adjustEvent = new com.adjust.sdk.AdjustEvent(eventToken);
        adjustEvent.setRevenue(revenue, currency);
        adjustEvent.setOrderId(orderId);
        Adjust.trackEvent(adjustEvent);
    }

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

        //...
    }
}
