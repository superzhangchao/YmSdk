package com.ym.game.sdk.presenter;

import android.app.Activity;

import android.content.Intent;


import android.text.TextUtils;

import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.sdk.R;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.listener.ChangeVcodeViewListener;
import com.ym.game.sdk.callback.listener.CheckBindListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.callback.listener.RealNameStatusListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;
import com.ym.game.sdk.model.IUserModel;
import com.ym.game.sdk.model.IUserView;
import com.ym.game.sdk.model.UserModel;
import com.ym.game.sdk.ui.activity.BaseActivity;
import com.ym.game.sdk.ui.activity.YmUserActivity;
import com.ym.game.sdk.base.config.TypeConfig;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;


public class UserPresenter {
    private static final int UNREALNAME = 0;
    private static Activity loginActivity;

    public static void showLoginActiviy(Activity activity){
        loginActivity = activity;
        IUserModel userModel = UserModel.getInstance();
//        User user = userModel.getUserInfo(activity);
        String token = userModel.getToken(activity);
        String uid = userModel.getUid(activity);
        if (TextUtils.isEmpty(token)||TextUtils.isEmpty(uid)){
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type",TypeConfig.LOGIN);
            activity.startActivity(intent);
        }else{
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type",TypeConfig.AUTOLOGIN);
            activity.startActivity(intent);
        }
    }

    public static void showBindActiviy(Activity activity,ResultAccoutBean resultAccoutBean){

        AccountBean accountBean = new AccountBean();
        accountBean.setUid(resultAccoutBean.getData().getUid());
        accountBean.setLoginToken(resultAccoutBean.getData().getLoginToken());
        accountBean.setNickName(resultAccoutBean.getData().getNickName());
        accountBean.setAuthStatus(resultAccoutBean.getData().getAuthStatus());
        Intent intent = new Intent(activity, YmUserActivity.class);
        intent.putExtra("type",TypeConfig.BIND);
        intent.putExtra("accountBean",accountBean);
        activity.startActivity(intent);
    }

    public static void showRealNameActiviy(Activity activity,ResultAccoutBean resultAccoutBean){

        AccountBean accountBean = new AccountBean();
        accountBean.setUid(resultAccoutBean.getData().getUid());
        accountBean.setLoginToken(resultAccoutBean.getData().getLoginToken());

        Intent intent = new Intent(activity, YmUserActivity.class);
        intent.putExtra("type",TypeConfig.REALNAME);
        intent.putExtra("accountBean",accountBean);
        activity.startActivity(intent);
    }
    


    public static void cancelLogin(IUserView purchaseView){
        purchaseView.closeActivity();
        CallbackMananger.getLoginCallBack().onCancel();

    }


    public static void startLogin(final IUserView userView, final AccountBean accountBean) {
        userView.showLoading();

        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                loginByType(userView,accountBean);
            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                ToastUtils.showToast(loginActivity,message);
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });
    }

    /**
     * 自动登录
     * @param activity
     */
    public static void autoLogin(final BaseActivity activity){
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
                        if(needBind(resultAccountBean)){
                            showBindActiviy(loginActivity,resultAccountBean);
                        }else if(needRealName(resultAccountBean)){
                            CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
                            showRealNameActiviy(loginActivity,resultAccountBean);
                        }else {
                            CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);

                        }
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFail(int status,String message) {
                        UserModel.getInstance().resetAccountInfo(activity);
                        activity.dismissLoading();
                        activity.finish();
                        ToastUtils.showToast(loginActivity,message);
                        showLoginActiviy(loginActivity);
                    }
                });
            }

            @Override
            public void onFail(int status,String message) {
                activity.dismissLoading();
                activity.finish();
                ToastUtils.showToast(loginActivity,message);
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });
    }

    private static void loginByType(final IUserView userView, final AccountBean accountBean){
        UserModel.getInstance().loginByType((Activity) userView.getContext(), accountBean,new LoginStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();
                if(needBind(resultAccountBean)){
                    showBindActiviy(loginActivity,resultAccountBean);
                }else if(needRealName(resultAccountBean)){
                    CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
                    showRealNameActiviy(loginActivity,resultAccountBean);
                }else {
                    CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
                }
            }

            @Override
            public void onCancel() {
                userView.dismissLoading();
                userView.cancelLogin();
                ToastUtils.showToast(loginActivity,loginActivity.getString(ResourseIdUtils.getStringId("ym_text_logincancel")));

            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                userView.closeActivity();
                ToastUtils.showToast(loginActivity,message);
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });
    }

    private static boolean needBind(ResultAccoutBean resultAccoutBean){
        String loginType = resultAccoutBean.getData().getLoginType();
        return TextUtils.equals("guest",loginType);
    }
    private static boolean needRealName(ResultAccoutBean resultAccoutBean){
        return resultAccoutBean.getData().getAuthStatus()== UNREALNAME;
    }
    public static void startBind(final IUserView userView, final AccountBean accountBean){
        userView.showLoading();
        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                bindAccount(userView,accountBean);
            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                userView.closeActivity();
                ToastUtils.showToast(loginActivity,message);
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });
    }
    public static void cancelBind(IUserView userView, ResultAccoutBean resultAccoutBean) {
        userView.closeActivity();
        CallbackMananger.getLoginCallBack().onSuccess(resultAccoutBean);
        if (needRealName(resultAccoutBean)){
            showRealNameActiviy(loginActivity,resultAccoutBean);
        }
    }
    private static void bindAccount(final IUserView userView, final AccountBean accountBean){
        UserModel.getInstance().bindAccount((Activity) userView.getContext(), accountBean,new LoginStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();
                CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
                if (needRealName(resultAccountBean)){
                    showRealNameActiviy(loginActivity,resultAccountBean);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                userView.closeActivity();
                ToastUtils.showToast(loginActivity,message);
                CallbackMananger.getLoginCallBack().onFailure(status,message);
            }
        });
    }

    public static void sendVcode(final IUserView userView, final String phone, final ChangeVcodeViewListener changeSendVcodeViewListener) {

        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts,String accessToken) {
                checkBind(userView,phone,ts,accessToken,changeSendVcodeViewListener);
            }

            @Override
            public void onFail(int status,String message) {
                //获取ts和token失败
                ToastUtils.showToast(userView.getContext(),message);
            }
        });


    }

    public static void sendVcode(final IUserView userView, final String phone) {

        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts,String accessToken) {
                startSendVcode(userView,phone,ts,accessToken);
            }

            @Override
            public void onFail(int status,String message) {
                //获取ts和token失败
                ToastUtils.showToast(userView.getContext(),message);
            }
        });


    }

    private static void checkBind(final IUserView userView, final String phone, final String ts, final String accessToken, final ChangeVcodeViewListener changeSendVcodeViewListener){
        UserModel.getInstance().checkBind(userView.getContext(),phone,ts,accessToken,new CheckBindListener(){
            @Override
            public void getBindStatus(int status,String message) {
                if(status==0){
                    changeSendVcodeViewListener.onChangeVcodeView();
                    startSendVcode(userView,phone,ts,accessToken);
                }else {

                    ToastUtils.showToast(userView.getContext(),message);
                }
            }

            @Override
            public void onFail(int status,String message) {
                //发送验证手机号码失败
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }

    private static void startSendVcode(final IUserView userView, String phone, String ts, String accessToken){
        UserModel.getInstance().sendVcode(userView.getContext(),phone,ts,accessToken, new SendVcodeListener() {
            @Override
            public void onSuccess() {
                //发送验证码成功
                ToastUtils.showToast(userView.getContext(),userView.getContext().getString(ResourseIdUtils.getStringId("ym_text_sendsms_suc")));
            }

            @Override
            public void onFail(int status,String message) {
                ToastUtils.showToast(userView.getContext(),message);

            }
        });
    }


    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        UserModel.getInstance().onActivityResult(activity,requestCode,resultCode,data);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        UserModel.getInstance().onRequestPermissionsResult(activity,requestCode,permissions,grantResults);

    }


    public static void logout(Activity activity) {
        UserModel.getInstance().logout(activity);
    }

    public static void startRealName(final IUserView userView, final AccountBean accountBean) {
        userView.showLoading();
        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                realName(userView,accountBean);
            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }

    private static void realName(final IUserView userView, AccountBean accountBean) {
        UserModel.getInstance().realName((Activity) userView.getContext(), accountBean,new RealNameStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();
                ToastUtils.showToast(userView.getContext(),userView.getContext().getString(ResourseIdUtils.getStringId("ym_text_realname_suc")));
            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }

    public static void cancelRealName(final IUserView userView) {
        userView.dismissLoading();
        userView.closeActivity();
    }

    public static AccountBean getLoginAccountInfo(){
        return UserModel.getInstance().getLoginAccountInfo();
    }

    public static boolean isLogin(){
        return UserModel.getInstance().isLogin();
    }

    public static int getRealNameStatus(){
        if(isLogin()){
            return UserModel.getInstance().getRealNameStatus();
        }
        return -1;
    }

    public static void saveXieyiStatud(IUserView userView, boolean status) {
        UserModel.getInstance().saveXieyiStatud(userView.getContext(),status);
    }

    public static boolean getXieyiStatus(IUserView userView) {
        return UserModel.getInstance().getXieyiStatus(userView.getContext());
    }

    public static void checkWxLogin() {
        boolean wxLoginStatus = UserModel.getInstance().getWxLoginStatus();
        if (wxLoginStatus){
            UserModel.getInstance().resetWxlogin();
        }
    }
}
