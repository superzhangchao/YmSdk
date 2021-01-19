package com.ym.ysfj;

import android.app.Application;

import com.ym.game.sdk.common.base.cache.ApplicationCache;


public class MainApplication extends Application {

    private static MainApplication sInstance;
    private static String bugAppId = "10b124026a";


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ApplicationCache.init(this);
// 获取当前包名
//        String packageName = this.getPackageName();
//// 获取当前进程名
//        String processName = YmFileUtils.getProcessName(android.os.Process.myPid());
//// 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//// 初始化Bugly
//        CrashReport.initCrashReport(this, bugAppId, true, strategy);
    }



    public static MainApplication getInstance() {
        return sInstance;
    }

}
