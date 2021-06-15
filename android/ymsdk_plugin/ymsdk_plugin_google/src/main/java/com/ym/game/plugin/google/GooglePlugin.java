package com.ym.game.plugin.google;

import android.content.Context;
import android.content.Intent;


import com.ym.game.plugin.google.login.GoogleLogin;
import com.ym.game.plugin.google.pay.GooglePay;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;

import java.util.Map;

public class GooglePlugin  extends Plugin {

    private static final String TAG = "GooglePlugin";
    private int eventType = 0;
    private static final int noEvent = 0;
    private static final int loginEvent = 1;
    private static final int logoutEvent = 2;
    private static final int payEvent = 3;
    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();

        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    public void googleLogin(Context context, Map<String,Object> loginMap, CallBackListener callBackListener){
        eventType= loginEvent;
        GoogleLogin.getInstance().login(context,loginMap,callBackListener);
    }
    public void googleLogout(Context context,CallBackListener callBackListener){
        eventType= logoutEvent;
        GoogleLogin.getInstance().logout(context,callBackListener);
    }
    public void googleRevokeAccess(Context context,CallBackListener callBackListener){
        GoogleLogin.getInstance().revokeAccess(context,callBackListener);
    }

    public void googlepay(Context context,Map<String,Object> payMap,CallBackListener callBackListener,CallBackListener getGPVerifyParamListener){
        eventType = payEvent;
        GooglePay.getInstance().initPay(context, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                GooglePay.getInstance().pay(context,payMap,callBackListener,getGPVerifyParamListener);
            }

            @Override
            public void onFailure(int code, String msg) {
                callBackListener.onFailure(code,msg);
            }
        });
    }



    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (eventType == loginEvent){
            GoogleLogin.getInstance().onActivityResult(context,requestCode,resultCode,data);
        }
    }
}
