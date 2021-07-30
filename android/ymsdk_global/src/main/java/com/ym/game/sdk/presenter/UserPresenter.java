package com.ym.game.sdk.presenter;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;


import android.text.TextUtils;
import android.util.Log;

import com.ym.game.net.bean.ResultAccoutBean;

import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.listener.BindStatusListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.invoke.plugin.GooglePluginApi;
import com.ym.game.sdk.model.IUserModel;
import com.ym.game.sdk.model.IUserView;
import com.ym.game.sdk.model.UserModel;
import com.ym.game.sdk.ui.activity.BaseActivity;
import com.ym.game.sdk.ui.activity.YmUserActivity;
import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.utils.AdvertisingIdUtils;


public class UserPresenter {
    private static Activity loginActivity;
    private static Activity bindActivity;
    private static boolean isLogin = false;

    public static void showLoginActiviy(Activity activity){
        loginActivity = activity;
        if (GooglePluginApi.getInstance()!=null){
            AdvertisingIdUtils.initialAdvertisingId(activity);
        }
        UserModel userModel = UserModel.getInstance();
        String uid = userModel.getLastUid(activity);
        String token = userModel.getLastToken(activity);
        if (TextUtils.isEmpty(token)||TextUtils.isEmpty(uid)){
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type", YmTypeConfig.LOGIN);
            activity.startActivity(intent);
        }else{
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type", YmTypeConfig.AUTOLOGIN);
            activity.startActivity(intent);
        }
    }

    public static void showBindActiviy(Activity activity){
        if (UserModel.getInstance().isLogin()&&UserModel.getInstance().isGuest()){
            bindActivity = activity;
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type", YmTypeConfig.BIND);
            activity.startActivity(intent);
        }else if(UserModel.getInstance().isLogin()&&!UserModel.getInstance().isGuest()){
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_noguest")));
            CallbackMananger.getBindCallBack().onFailure(ErrorCode.FAILURE,activity.getString(ResourseIdUtils.getStringId("ym_text_noguest")));
        }else if(!UserModel.getInstance().isLogin()) {
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_please_login")));
            CallbackMananger.getBindCallBack().onFailure(ErrorCode.FAILURE,activity.getString(ResourseIdUtils.getStringId("ym_text_nologin")));
        }
    }

    public static void loginbyType(final IUserView userView, String loginType){
        final AccountBean accountBean = new AccountBean();
        accountBean.setLoginType(loginType);
        userView.showLoading();
        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                startlogin(userView,accountBean);
            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });

    }

    public static void startlogin(final IUserView userView, AccountBean accountBean){
        UserModel.getInstance().loginByType((Activity) userView.getContext(), accountBean, new LoginStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();
                loginSuccess(userView.getContext(),resultAccountBean);
            }

            @Override
            public void onCancel() {
                userView.dismissLoading();
                CallbackMananger.getLoginCallBack().onCancel();
            }

            @Override
            public void onFail(int status, String message) {
                userView.dismissLoading();
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });
    }


    public static void autoLogin(final BaseActivity activity) {
        final AccountBean accountBean = new AccountBean();
        final IUserModel userModel = UserModel.getInstance();
        activity.showLoading();
        UserModel.getInstance().getVerifyData(activity,new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                userModel.autoLogin(activity,accountBean, new LoginStatusListener() {
                    public void onSuccess(final ResultAccoutBean resultAccountBean) {
                        activity.dismissLoading();
                        activity.finish();
                        loginSuccess(activity,resultAccountBean);
                    }

                    @Override
                    public void onCancel() {
                        activity.dismissLoading();
                    }

                    @Override
                    public void onFail(int status,String message) {
                        activity.dismissLoading();
                        UserModel.getInstance().resetAccountInfo(activity);

                    }
                });
            }

            @Override
            public void onFail(int status,String message) {
                activity.dismissLoading();
                UserModel.getInstance().resetAccountInfo(activity);
            }
        });
    }

    private static void loginSuccess(Context context,ResultAccoutBean resultAccountBean) {
        if (TextUtils.equals(resultAccountBean.getData().getLoginType(),YmConstants.GUSETTYPE)){
            ToastUtils.showToast(context,context.getString(ResourseIdUtils.getStringId("ym_text_gusetlogin_tip")));

            //TODO:需要主动弹出实名认证界面暂缓发送登录结果
        }
        CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean.getData());
        //PurchasePresenter.checkPurchaseState(context);

    }

    public static void bindByType(final IUserView userView, String bindType) {

        final AccountBean accountBean = UserModel.getInstance().getLoginAccountInfo();
        String loginType = accountBean.getLoginType();
        if (TextUtils.equals(loginType, YmConstants.GUSETTYPE)){
            accountBean.setBindType(bindType);
            UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
                @Override
                public void onSuccess(String ts, String accessToken) {
                    accountBean.setTimeStamp(ts);
                    accountBean.setAccessToken(accessToken);

                    startBind(userView,accountBean);
                }

                @Override
                public void onFail(int status,String message) {
                    ToastUtils.showToast(userView.getContext(),message);
//                    CallbackMananger.getBindCallBack().onFailure(status,message);
                }
            });
        }else {
            //TODO:不是游客不能绑定
        }
    }

    public static void startBind(final IUserView userView, AccountBean accountBean){
        UserModel.getInstance().bindByType((Activity)userView.getContext(),accountBean, new BindStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();

                CallbackMananger.getBindCallBack().onSuccess(resultAccountBean.getData());
            }

            @Override
            public void onCancel() {
                CallbackMananger.getBindCallBack().onCancel();
            }

            @Override
            public void onFail(int status, String message) {
                ToastUtils.showToast(userView.getContext(),message);
//                CallbackMananger.getBindCallBack().onFailure(status,message);
            }
        });
    }

    public static AccountBean getLoginAccountInfo(){
        return UserModel.getInstance().getLoginAccountInfo();
    }

    public static boolean isLogin(){
        return UserModel.getInstance().isLogin();
    }

    public static void logout(Activity activity) {
        if (UserModel.getInstance().isLogin()){
            UserModel.getInstance().logout(activity);
        }
    }

    public static void switchByBind(Activity activity) {
        if (UserModel.getInstance().isLogin()){
            UserModel.getInstance().logout(activity);
            CallbackMananger.getBindCallBack().onSwitch();
        }
    }

    public static void cancelBind(){
        CallbackMananger.getBindCallBack().onCancel();
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        UserModel.getInstance().onActivityResult(activity,requestCode,resultCode,data);
    }

    public static void onRequestPermissionsResult(YmUserActivity ymUserActivity, int requestCode, String[] permissions, int[] grantResults) {

    }



}
