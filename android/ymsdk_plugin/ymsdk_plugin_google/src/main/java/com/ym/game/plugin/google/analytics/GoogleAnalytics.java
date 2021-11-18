package com.ym.game.plugin.google.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ym.game.plugin.google.login.GoogleLogin;

public class GoogleAnalytics {

    private volatile static GoogleAnalytics INSTANCE;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static GoogleAnalytics getInstance(){
        if (INSTANCE==null){
            synchronized (GoogleLogin.class){
                if (INSTANCE==null){
                    INSTANCE = new GoogleAnalytics();
                }
            }
        }
        return INSTANCE;
    }

    public void initAnalytics(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }


    public void report(Context context,String eventName,String roleId,String roleName,String roleLevel) {
        Bundle params = new Bundle();
        params.putString("roleId", roleId);
        params.putString("roleName", roleName);
        params.putString("roleLevel", roleLevel);
        mFirebaseAnalytics.logEvent(eventName,params);
    }

    public void reportWithPurchase(Context context,String roleId,String roleName,String roleLevel,String productName,String productId,double price) {
        Bundle params = new Bundle();
        params.putString("roleId", roleId);
        params.putString("roleName", roleName);
        params.putString("roleLevel", roleLevel);
        params.putString("productName", productName);
        params.putString("productId", productId);
        params.putDouble("price", price);

        mFirebaseAnalytics.logEvent("FinishPurchase",params);
    }

}
