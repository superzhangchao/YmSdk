package com.ym.game.ymsdk.plugin.adjust.analytics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnDeeplinkResponseListener;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdjustAnalytics {

    private volatile static AdjustAnalytics INSTANCE;

    public static AdjustAnalytics getInstance() {
        if (INSTANCE == null) {
            synchronized (AdjustAnalytics.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdjustAnalytics();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Application context, Map<String,String> initMap){
        String adjustToken = initMap.get("adjustToken");
        String environment = initMap.get("environment");
//        String appToken = "{YourAppToken}";
//        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
//        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
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
//        config.setLogLevel(LogLevel.VERBOSE); // enable all logs
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
        AdjustEvent adjustEvent = new AdjustEvent("abc123");
        Adjust.trackEvent(adjustEvent);
    }

    //跟踪收入
    public void TrackEventWithRevenue(String eventToken,double revenue,String currency){
        AdjustEvent adjustEvent = new AdjustEvent(eventToken);
        adjustEvent.setRevenue(revenue, currency);
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
