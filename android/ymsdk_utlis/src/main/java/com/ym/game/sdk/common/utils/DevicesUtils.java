package com.ym.game.sdk.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import com.ym.game.sdk.common.base.cache.ApplicationCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DevicesUtils {

    @SuppressLint("HardwareIds")
    public static String getExtra(){
        JSONObject para = new JSONObject();
        try {
            para.put("devicetype","android");
            para.put("model",Build.MODEL);
            para.put("androidversion",Build.VERSION.RELEASE);
            para.put("androidcode",Build.VERSION.SDK_INT+"");
            para.put("appVersion",getVersionName());
            para.put("timezone",TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
            para.put("localtime",System.currentTimeMillis());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return para.toString();
    }

    public static String getDeviceLang(){
        Locale locale = Locale.getDefault();
        //>=24 is Android 7.0 or high
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = ApplicationCache.getInstance().getApplication().getResources().getConfiguration().getLocales().get(0);
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String displayCountry = locale.getDisplayCountry();
        if (TextUtils.equals(language,"zh")){
            if (TextUtils.equals(country,"CN")&&TextUtils.equals(displayCountry,"中国")){
                return language+"_CN";
            }else {
                return language+"_TW";
            }

        }{
            return language;
        }
    }


    public static String getVersionCode(){
        Context applicationContext = ApplicationCache.getInstance().getApplicationContext();
        PackageManager packageManager= applicationContext.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo=packageManager.getPackageInfo(applicationContext.getPackageName(),0);
            versionCode=packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    public static String getVersionName(){
        Context applicationContext = ApplicationCache.getInstance().getApplicationContext();

        PackageManager packageManager=applicationContext.getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo=packageManager.getPackageInfo(applicationContext.getPackageName(),0);
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


}
