package com.ym.ysfj;

import android.app.Application;

import com.ym.game.sdk.YmSdkApi;
import com.ym.game.sdk.common.base.cache.ApplicationCache;


public class MainApplication extends Application {

    private static MainApplication sInstance;
    private static String bugAppId = "10b124026a";


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ApplicationCache.init(this);
        YmSdkApi.getInstance().initPlatform(sInstance,"5012","abcd");

    }



    public static MainApplication getInstance() {
        return sInstance;
    }

}
