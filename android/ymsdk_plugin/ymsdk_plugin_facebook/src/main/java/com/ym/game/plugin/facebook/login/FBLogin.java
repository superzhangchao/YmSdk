package com.ym.game.plugin.facebook.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;
import java.util.Map;

public class FBLogin {
    private volatile static FBLogin INSTANCE;
    private CallbackManager callbackManager;
    private Context mContext;
    private CallBackListener mloginCallbackListener;

    private FBLogin(){}

    public static FBLogin getInstance(){
        if (INSTANCE==null){
            synchronized (FBLogin.class){
                if (INSTANCE==null){
                    INSTANCE = new FBLogin();
                }
            }
        }
        return INSTANCE;
    }

//    public  void fastLogin(Context context){
//        LoginManager.getInstance().retrieveLoginStatus(context, new LoginStatusCallback() {
//            @Override
//            public void onCompleted(AccessToken accessToken) {
//                //User was previously logged in, can log them in directly here.
//                // If this callback is called, a popup notification appears that says  "Logged in as <User Name>"
//                String userId = accessToken.getUserId();
//                mloginCallbackListener.onSuccess(userId);
//            }
//
//            @Override
//            public void onFailure() {
//                //No access token could be retrieved for the user
//            }
//
//            @Override
//            public void onError(Exception exception) {
//                // An error occurred
//
//            }
//        });
//    }

    public void login(Context context, Map<String,Object> loginMap, CallBackListener callBackListener){
        mContext = context;
        mloginCallbackListener = callBackListener;
        //具体实现fb登录

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        String userId = loginResult.getAccessToken().getUserId();
                        mloginCallbackListener.onSuccess(userId);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        mloginCallbackListener.onFailure(ErrorCode.CANCEL,"fb login oncancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        mloginCallbackListener.onFailure(ErrorCode.FAILURE,"fb login onFail");
                    }
                });




        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (!isLoggedIn){
            LoginManager.getInstance().logInWithReadPermissions((Activity) mContext, Arrays.asList("public_profile"));
        }else {
            String userId = accessToken.getUserId();

            mloginCallbackListener.onSuccess(userId);

        }


    }

    public void logout(Context context){
        LoginManager.getInstance().logOut();
    }


    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
