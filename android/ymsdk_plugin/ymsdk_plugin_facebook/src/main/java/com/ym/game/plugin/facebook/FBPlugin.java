package com.ym.game.plugin.facebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.ym.game.plugin.facebook.login.FBLogin;
import com.ym.game.plugin.facebook.share.FBShare;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;

import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FBPlugin extends Plugin {
    private String TAG = "FBPlugin";
    private int eventType = 0;
    private static final int noEvent = 0;
    private static final int loginEvent = 1;
    private static final int logoutEvent = 2;
    private static final int shareEvent = 3;
    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }


    public void fbLogin(Context context, Map<String,Object> loginMap, CallBackListener callBackListener){
        eventType = loginEvent;
        FBLogin.getInstance().login(context,loginMap,callBackListener);
    }

    public void fbLogout(Context context){
    FBLogin.getInstance().logout(context);
    }
    public void share(Context context,Map<String,Object> shareMap,CallBackListener callBackListener){
        eventType = shareEvent;
        FBShare.getInstance().share(context,shareMap,callBackListener);
    }
    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (eventType==loginEvent){
            FBLogin.getInstance().onActivityResult(context, requestCode, resultCode, data);
            eventType = noEvent;
        }else if (eventType==shareEvent){
            FBShare.getInstance().onActivityResult(context, requestCode, resultCode, data);
            eventType = noEvent;
        }
    }
}
