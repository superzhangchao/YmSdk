package com.ym.game.sdk.presenter;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;


import android.text.TextUtils;

import com.ym.game.net.bean.ResultAccoutBean;

import com.ym.game.sdk.YmConstants;
import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.listener.ChangeVcodeViewListener;
import com.ym.game.sdk.callback.listener.CheckRegisterListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.callback.listener.RealNameStatusListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;
import com.ym.game.sdk.callback.listener.SetPasswordStatusListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.event.UserEvent;
import com.ym.game.sdk.model.IUserModel;
import com.ym.game.sdk.model.IUserView;
import com.ym.game.sdk.model.UserModel;
import com.ym.game.sdk.ui.activity.BaseActivity;
import com.ym.game.sdk.ui.activity.YmUserActivity;


import org.greenrobot.eventbus.EventBus;


public class UserPresenter {
    private static final int UNREALNAME = 0;
    private static Activity loginActivity;
    private static boolean isLogin = false;
    private static boolean isRelogin = false;
    private static ResultAccoutBean currentResultAccountBean;
    private static Activity realNameActivity;


    public static void showLoginActiviy(Activity activity){
        loginActivity = activity;
        UserModel userModel = UserModel.getInstance();
//        User user = userModel.getUserInfo(activity);
        String token = userModel.getToken(activity);
        String uid = userModel.getUid(activity);
        AccountBean lastNormalLoginInfo = getLastNormalLoginInfo(loginActivity);
        if (TextUtils.equals(YmConstants.PHONELOGIN,lastNormalLoginInfo.getLoginType())&&lastNormalLoginInfo.isHasPassword()&&isRelogin){
            //TODO:跳转快速密码页
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type", YmTypeConfig.QUICKPWDPAGE);
            activity.startActivity(intent);
            isRelogin =false;
        }else if (TextUtils.isEmpty(token)||TextUtils.isEmpty(uid)){
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type",YmTypeConfig.LOGIN);
            activity.startActivity(intent);
        }else{
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type",YmTypeConfig.AUTOLOGIN);
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
        intent.putExtra("type",YmTypeConfig.BIND);
        intent.putExtra("accountBean",accountBean);
        activity.startActivity(intent);
    }

    public static void showRealNameActiviy(Activity activity,AccountBean accountBean,int realNameType){
        realNameActivity = activity;
        Intent intent = new Intent(activity, YmUserActivity.class);
        intent.putExtra("type",YmTypeConfig.REALNAME);
        intent.putExtra("accountBean",accountBean);
        intent.putExtra("realNameType",realNameType);
        activity.startActivity(intent);
    }

    private static void showRealNamePage(ResultAccoutBean resultAccountBean,int realNameType) {
        AccountBean accountBean = new AccountBean();
        accountBean.setUid(resultAccountBean.getData().getUid());
        accountBean.setLoginToken(resultAccountBean.getData().getLoginToken());
        accountBean.setLoginType(resultAccountBean.getData().getLoginType());
        accountBean.setNickName(resultAccountBean.getData().getNickName());
        showRealNameActiviy(loginActivity, accountBean, realNameType);
    }

    public static void cancelLogin(IUserView purchaseView){
        purchaseView.closeActivity();
        CallbackMananger.getLoginCallBack().onCancel();

    }

    public static void registerAccount(final IUserView userView, final AccountBean accountBean) {
        userView.showLoading();
        UserModel.getInstance().getVerifyData(userView.getContext(), new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                startRegister(userView,accountBean);
            }

            @Override
            public void onFail(int status, String message) {
                userView.dismissLoading();
                userView.cancelLogin();
                ToastUtils.showToast(loginActivity,message);
            }
        });
    }

    public static void setPwd(final IUserView userView, final AccountBean accountBean) {
        userView.showLoading();
        UserModel.getInstance().getVerifyData(userView.getContext(), new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                accountBean.setTimeStamp(ts);
                accountBean.setAccessToken(accessToken);
                startSetPwd(userView,accountBean);
            }

            @Override
            public void onFail(int status, String message) {
                userView.dismissLoading();
                userView.cancelLogin();
                ToastUtils.showToast(loginActivity,message);
            }
        });
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
                userView.cancelLogin();
                ToastUtils.showToast(loginActivity,message);
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
                        if(isGuestLogin(resultAccountBean)){
                            if(needRealName(resultAccountBean)){
                                //CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
                                isLogin = true;
                                currentResultAccountBean = resultAccountBean;
                                showRealNamePage(resultAccountBean,YmConstants.GUESTLOGINREALNAMETYPE);
                            }else {
                                showBindActiviy(loginActivity,resultAccountBean);
                            }
                        }else {
                            if(needRealName(resultAccountBean)){
                                //CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
                                isLogin = true;
                                currentResultAccountBean = resultAccountBean;
                                showRealNamePage(resultAccountBean,YmConstants.COMMONLOGINREALNAMETYPE);
                            }else {
                                sendLoginSuccess(activity,resultAccountBean);

                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFail(int status,String message) {
                        isRelogin = true;
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
                UserModel.getInstance().resetAccountInfo(activity);
                activity.dismissLoading();
                activity.finish();
                ToastUtils.showToast(loginActivity,message);
                showLoginActiviy(loginActivity);
            }
        });
    }


    private static void startRegister(final IUserView userView, AccountBean accountBean) {
        UserModel.getInstance().startRegister((Activity) userView.getContext(),accountBean, new LoginStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();
                if(needRealName(resultAccountBean)){
                    isLogin = true;
                    currentResultAccountBean = resultAccountBean;
                    showRealNamePage(resultAccountBean,YmConstants.COMMONLOGINREALNAMETYPE);
                }else {
                    sendLoginSuccess(userView.getContext(),resultAccountBean);
                }
            }

            @Override
            public void onCancel() {
                userView.dismissLoading();

            }

            @Override
            public void onFail(int status, String message) {
                userView.dismissLoading();
                ToastUtils.showToast(loginActivity,message);
            }
        });
    }

    private static void startSetPwd(final IUserView userView, final AccountBean accountBean) {
        UserModel.getInstance().startSetPwd((Activity) userView.getContext(),accountBean, new SetPasswordStatusListener() {
            @Override
            public void onSuccess() {
                UserEvent userEvent = new UserEvent();
                userEvent.setPhone(accountBean.getNumber());
                userEvent.setPwd(accountBean.getPassword());
                EventBus.getDefault().post(userEvent);
                userView.dismissLoading();
                userView.closeCurrnetFragment();
                ToastUtils.showToast(userView.getContext(),userView.getContext().getString(ResourseIdUtils.getStringId("ym_text_setsuccess")));

            }


            @Override
            public void onFail(int status, String message) {
                userView.dismissLoading();
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }
    private static void loginByType(final IUserView userView, final AccountBean accountBean){
        UserModel.getInstance().loginByType((Activity) userView.getContext(), accountBean,new LoginStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();


                if (isGuestLogin(resultAccountBean)){
                    if(needRealName(resultAccountBean)) {
                        isLogin = true;
                        currentResultAccountBean = resultAccountBean;
                        showRealNamePage(resultAccountBean, YmConstants.GUESTLOGINREALNAMETYPE);
                    }else {
                        showBindActiviy(loginActivity,resultAccountBean);
                    }
                }else {
                    if(needRealName(resultAccountBean)){
                    isLogin = true;
                    currentResultAccountBean = resultAccountBean;
                    showRealNamePage(resultAccountBean,YmConstants.COMMONLOGINREALNAMETYPE);
                    }else {
                        sendLoginSuccess(userView.getContext(),resultAccountBean);
                    }
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
                ToastUtils.showToast(loginActivity,message);
//                if (TextUtils.equals(accountBean.getLoginType(),YmConstants.PHONELOGIN)&&accountBean.isHasPassword()){
                    userView.cancelLogin();
//                }else {
//                    userView.closeActivity();
//                    CallbackMananger.getLoginCallBack().onFailure(status,message);
//                }
            }
        });
    }

    private static void sendLoginSuccess(Context context, ResultAccoutBean resultAccountBean) {
        UserModel.getInstance().saveAccountInfo(context);
        if (!TextUtils.isEmpty(resultAccountBean.getData().getPassword())){
            resultAccountBean.getData().setPassword("");
        }
        CallbackMananger.getLoginCallBack().onSuccess(resultAccountBean);
    }

    private static boolean isGuestLogin(ResultAccoutBean resultAccoutBean){
        String loginType = resultAccoutBean.getData().getLoginType();
        return TextUtils.equals(YmConstants.GUSETLOGIN,loginType);
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
                ToastUtils.showToast(loginActivity,message);
            }
        });
    }
    public static void cancelBind(IUserView userView, ResultAccoutBean resultAccountBean) {
        userView.closeActivity();
        sendLoginSuccess(userView.getContext(),resultAccountBean);

    }
    private static void bindAccount(final IUserView userView, final AccountBean accountBean){
        UserModel.getInstance().bindAccount((Activity) userView.getContext(), accountBean,new LoginStatusListener() {
            @Override
            public void onSuccess(ResultAccoutBean resultAccountBean) {
                userView.dismissLoading();
                userView.closeActivity();
                sendLoginSuccess(userView.getContext(),resultAccountBean);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                ToastUtils.showToast(loginActivity,message);
            }
        });
    }

    public static void sendVcode(final IUserView userView, final int type, final String phone, final ChangeVcodeViewListener changeSendVcodeViewListener) {

        UserModel.getInstance().getVerifyData(userView.getContext(),new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts,String accessToken) {
                if (YmTypeConfig.LOGIN == type){
                    changeSendVcodeViewListener.onChangeVcodeView();
                    startSendVcode(userView,phone,ts,accessToken);
                }else if (YmTypeConfig.BIND == type){
                    checkBind(userView,phone,ts,accessToken,changeSendVcodeViewListener);
                }else if (YmTypeConfig.REGISTER == type){
                    checkRegister(userView,phone,ts,accessToken,changeSendVcodeViewListener);
                }else if (YmTypeConfig.SETPASSWORD ==type){
                    checkSetPwd(userView,phone,ts,accessToken,changeSendVcodeViewListener);
                }
            }

            @Override
            public void onFail(int status,String message) {
                ToastUtils.showToast(userView.getContext(),message);
            }
        });


    }

    private static void checkRegister(final IUserView userView, final String phone, final String ts, final String accessToken, final ChangeVcodeViewListener changeSendVcodeViewListener) {
        UserModel.getInstance().checkRegister(userView.getContext(),phone,ts,accessToken, new CheckRegisterListener() {
            @Override
            public void getRegisterStatus(int status, String message) {
                if (status == 0){
                    changeSendVcodeViewListener.onChangeVcodeView();
                    startSendVcode(userView,phone,ts,accessToken);
                }else if (status == 3001){
                    ToastUtils.showToast(userView.getContext(),userView.getContext().getString(ResourseIdUtils.getStringId("ym_text_register")));
                }else {
                    ToastUtils.showToast(userView.getContext(),message);
                }
            }

            @Override
            public void onFail(int status, String message) {
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }

    private static void checkSetPwd(final IUserView userView, final String phone, final String ts, final String accessToken, final ChangeVcodeViewListener changeSendVcodeViewListener) {
        UserModel.getInstance().checkRegister(userView.getContext(),phone,ts,accessToken, new CheckRegisterListener() {
            @Override
            public void getRegisterStatus(int status, String message) {
                if (status == 3001){
                    changeSendVcodeViewListener.onChangeVcodeView();
                    startSendVcode(userView,phone,ts,accessToken);
                }else if (status == 0){
                    ToastUtils.showToast(userView.getContext(),userView.getContext().getString(ResourseIdUtils.getStringId("ym_text_unregister")));
                }else {
                    ToastUtils.showToast(userView.getContext(),message);
                }
            }

            @Override
            public void onFail(int status, String message) {
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }

    private static void checkBind(final IUserView userView, final String phone, final String ts, final String accessToken, final ChangeVcodeViewListener changeSendVcodeViewListener){
        UserModel.getInstance().checkRegister(userView.getContext(),phone,ts,accessToken,new CheckRegisterListener(){
            @Override
            public void getRegisterStatus(int status,String message) {
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
        isRelogin = true;
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
                if (isLogin) {
                    currentResultAccountBean.getData().setAuthStatus(resultAccountBean.getData().getAuthStatus());
                    sendLoginSuccess(userView.getContext(),currentResultAccountBean);
                    isLogin = false;
                    currentResultAccountBean = null;
                }else {
                    CallbackMananger.getRealNameCallBack().onSuccess(resultAccountBean);
                }
            }

            @Override
            public void onFail(int status,String message) {
                userView.dismissLoading();
                ToastUtils.showToast(userView.getContext(),message);
            }
        });
    }

    public static void cancelRealName(final IUserView userView,int cancelRealNameType,AccountBean accountBean) {
        isLogin =false;
        currentResultAccountBean = null;
        userView.dismissLoading();
        userView.closeActivity();
        switch (cancelRealNameType){
            case YmConstants.REALNAMERELOGINSTATE:
                UserModel.getInstance().saveAccountInfo(userView.getContext());
                logout(realNameActivity);
                showLoginActiviy(realNameActivity);
                break;
            case YmConstants.REALNAMESKIPSTATE:
                ResultAccoutBean resultAccoutBean = new ResultAccoutBean();
                resultAccoutBean.setData(new ResultAccoutBean.DataBean());
                resultAccoutBean.getData().setUid(accountBean.getUid());
                resultAccoutBean.getData().setLoginToken(accountBean.getLoginToken());
                resultAccoutBean.getData().setNickName(accountBean.getNickName());
                resultAccoutBean.getData().setAuthStatus(accountBean.getAuthStatus());
                sendLoginSuccess(userView.getContext(),resultAccoutBean);
                break;
            case YmConstants.REALNAMECALLBACKSTATE:
                CallbackMananger.getRealNameCallBack().onCancel();
                break;
            default:
                break;
        }

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

    public static AccountBean getLastNormalLoginInfo(Activity activity){
        return UserModel.getInstance().getLastNormalLoginInfo(activity);
    }

}
