package com.ym.game.sdk.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.net.bean.TokenBean;

import com.ym.game.sdk.common.frame.logger.Logger;
import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.callback.listener.BindStatusListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.DevicesUtils;
import com.ym.game.sdk.config.Config;
import com.ym.game.sdk.config.YmErrorCode;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.YmFileUtils;
import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.sdk.invoke.plugin.FBPluginApi;
import com.ym.game.sdk.invoke.plugin.GooglePluginApi;
import com.ym.game.utils.AdvertisingIdUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class UserModel implements IUserModel {

    private static UserModel instance;
    private boolean needBind = false;


    private static final int SENDTIME = 1;
    private static final int SENDTIMEERROR = 2;
    private static final int SENDTIMENETERROR = 3;
    private static final int GETACCESSTOKENSUCCESS = 4;
    private static final int GETACCESSTOKENFAIL = 5;
    private static final int GETACCESSTOKENNETFAIL = 6;

    private static final int GETACCOUNTSUCCESS = 10;
    private static final int GETACCOUNTFAIL = 11;
    private static final int GETACCOUNTNETFAIL = 12;

    private static final int AUTOLOGINSUCCESS = 14;
    private static final int AUTOLOGINFAIL = 15;
    private static final int AUTOLOGINNETFAIL = 16;
    private static final int BINDSUCCESS = 17;
    private static final int BINDFAIL = 18;
    private static final int BINDNETFAIL = 19;

    private String pluginType;
    private static final String GOOGLEPLUGIN = "googleplugin";
    private static final String FACEBOOKPLUGIN = "facebookplugin";


    private static final int PERMISSION_REQUESTCODE = 1001;




    private String currentLoginType;
    private String currentBindType;


    private AccountBean mAccountBean;
    private AccountBean loginAccountInfo;
    private String currentTs;
    private GetVerifyDataListener mGetVerifyDataListener;
    private LoginStatusListener mLoginStatusListener;
    private BindStatusListener mBindStatusListener;
    private LoginStatusListener mAutoLoginStatusListener;
    private Context mContext;
    private AccountBean mBindAccountBean;
    private AccountBean saveAccountInfoBean;


    public static UserModel getInstance(){
        if (instance == null){
            instance = new UserModel();
        }
        return instance;
    }

    private UserModel(){

    }


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Map<String, Object> errorData;
            String messageName = "ym_text_neterror";
            int netError = YmErrorCode.NET_ERROR;
            switch (msg.what) {
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    currentTs = timeInfo.optString("ts");
                    getAccessToken();
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    mGetVerifyDataListener.onFail(ErrorCode.NET_DATA_NULL, mContext.getString(ResourseIdUtils.getStringId("ym_text_netdata_null")));
                    break;
                case SENDTIMENETERROR:
                case GETACCESSTOKENNETFAIL:
                    //TODO:请求ts网络失败
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mGetVerifyDataListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case GETACCESSTOKENSUCCESS:
                    //TODO:
                    String accessToken = (String) msg.obj;
                    mGetVerifyDataListener.onSuccess(currentTs, accessToken);
                    break;
                case GETACCESSTOKENFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mGetVerifyDataListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETACCOUNTSUCCESS:
                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo = new AccountBean();
                    loginAccountInfo.setUid(resultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(resultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setLoginType(resultAccoutBean.getData().getLoginType());
                    loginAccountInfo.setNickName(mContext.getString(ResourseIdUtils.getStringId("ym_nickname_"+resultAccoutBean.getData().getLoginType())));

                    saveAccountInfoBean = new AccountBean();
                    saveAccountInfoBean.setUid(resultAccoutBean.getData().getUid());
                    saveAccountInfoBean.setLoginToken(resultAccoutBean.getData().getLoginToken());
                    saveAccountInfoBean.setLoginType(resultAccoutBean.getData().getLoginType());
                    saveAccountInfo(mContext,saveAccountInfoBean);
                    //海外版用登录类型指代昵称
                    resultAccoutBean.getData().setNickName(resultAccoutBean.getData().getLoginType());
                    mLoginStatusListener.onSuccess(resultAccoutBean);

                    break;
                case GETACCOUNTFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mLoginStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETACCOUNTNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }

                    mLoginStatusListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case BINDSUCCESS:
                    ResultAccoutBean bindResultAccoutBean = (ResultAccoutBean) msg.obj;
                    //TODO:绑定完更新登录号的信息
                    loginAccountInfo.setUid(bindResultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(bindResultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setLoginType(bindResultAccoutBean.getData().getLoginType());
                    loginAccountInfo.setNickName(mContext.getString(ResourseIdUtils.getStringId("ym_nickname_"+bindResultAccoutBean.getData().getLoginType())));

                    saveAccountInfoBean = new AccountBean();
                    saveAccountInfoBean.setUid(bindResultAccoutBean.getData().getUid());
                    saveAccountInfoBean.setLoginToken(bindResultAccoutBean.getData().getLoginToken());
                    saveAccountInfoBean.setLoginType(bindResultAccoutBean.getData().getLoginType());
                    saveAccountInfo(mContext,saveAccountInfoBean);
                    //海外版用登录类型指代昵称
                    bindResultAccoutBean.getData().setNickName(bindResultAccoutBean.getData().getLoginType());
                    mBindStatusListener.onSuccess(bindResultAccoutBean);
                    break;
                case BINDFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mBindStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case BINDNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mBindStatusListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case AUTOLOGINSUCCESS:
                    ResultAccoutBean autoResultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo = new AccountBean();
                    loginAccountInfo.setUid(autoResultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(autoResultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setLoginType(autoResultAccoutBean.getData().getLoginType());
                    loginAccountInfo.setNickName(mContext.getString(ResourseIdUtils.getStringId("ym_nickname_"+autoResultAccoutBean.getData().getLoginType())));
                    //海外版用登录类型指代昵称
                    autoResultAccoutBean.getData().setNickName(autoResultAccoutBean.getData().getLoginType());
                    mAutoLoginStatusListener.onSuccess(autoResultAccoutBean);
                    break;
                case AUTOLOGINFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mAutoLoginStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case AUTOLOGINNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mAutoLoginStatusListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public AccountBean getLoginAccountInfo(){
        return loginAccountInfo;
    }



    @Override
    public void saveAccountInfo(Context context, AccountBean saveAccountInfoBean) {
        if (saveAccountInfoBean !=null){
            SharedPreferences loginTye = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);

            SharedPreferences.Editor editor = loginTye.edit();

            editor.putString("uid", saveAccountInfoBean.getUid());
            editor.putString("token", saveAccountInfoBean.getLoginToken());
            editor.putString("loginType", saveAccountInfoBean.getLoginType());
            editor.apply();
        }
    }




    public void resetAccountInfo(Activity activity) {
        SharedPreferences loginInfo = activity.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        if(loginInfo!=null){
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.remove("uid");
            editor.remove("token");
//            loginTye.edit().clear().apply();
            editor.apply();
        }
    }



    @Override
    public String getLastUid(Context context) {
        SharedPreferences fastLogin = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        String uid = fastLogin.getString("uid", "");

        return uid;
    }
    @Override
    public String getLastToken(Context context) {
        SharedPreferences fastLogin = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        String token = fastLogin.getString("token", "");
        return token;
    }

    public boolean isLogin(){
        if (loginAccountInfo == null){
            return false;
        }

        if (TextUtils.isEmpty(loginAccountInfo.getUid())){
            return false;
        }

        if (TextUtils.isEmpty(loginAccountInfo.getLoginToken())){
            return false;
        }

        return true;
    }

    public boolean isGuest(){
        if (loginAccountInfo!=null&&TextUtils.equals(loginAccountInfo.getLoginType(),YmConstants.GUSETTYPE)){
            return true;
        }

        return false;
    }

    @Override
    public void getVerifyData(Context context, GetVerifyDataListener getVerifyDataListener) {
        mGetVerifyDataListener = getVerifyDataListener;
        mContext = context;
        getTime();
    }


    private  void getTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<String> token = YmApi.getInstance().getTime();
                token.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            String body = response.body();
                            JSONObject timeInfo = new JSONObject();
                            try {
                                timeInfo.put("ts",body);
                                message.obj= timeInfo;
                                message.what = SENDTIME;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                message.what = SENDTIMEERROR;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = SENDTIMENETERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = SENDTIMENETERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getAccessToken() {

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.FROMKEY, YmConstants.FROM);
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN,sign);
        param.put(YmConstants.NOTE, DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<TokenBean> token = YmApi.getInstance().getAccessToken(param);
                token.enqueue(new Callback<TokenBean>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenBean> call, @NonNull Response<TokenBean> response) {
                        Message message = new Message();
                        int errorCode;
                        if (response.isSuccessful()){
                            TokenBean body = response.body();
                            errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {
                                message.obj = body.getData().getAccessToken();
                                message.what = GETACCESSTOKENSUCCESS;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCESSTOKENFAIL;
                            }
                        }else{
                             errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCESSTOKENNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCESSTOKENNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }



    @Override
    public void loginByType(Activity activity, AccountBean accountBean, LoginStatusListener loginStatusListener) {
        mContext = activity;
        mAccountBean = accountBean;
        mLoginStatusListener = loginStatusListener;
        currentLoginType = accountBean.getLoginType();

        if (TextUtils.equals(YmConstants.GOOGLETYPE,currentLoginType)){
        //TODO:google登录
            googlelogin(YmTypeConfig.LOGIN);
        }else  if (TextUtils.equals(YmConstants.FBTYPE,currentLoginType)){
        //TODO:facebook登录
            fblogin(YmTypeConfig.LOGIN);
        }else if (TextUtils.equals(YmConstants.GUSETTYPE,currentLoginType)){
            gtLogin();
        }
    }

    @Override
    public void bindByType(Activity activity, AccountBean accountBean, BindStatusListener bindStatusListener) {
        mContext = activity;
        mBindAccountBean = accountBean;
        mBindStatusListener = bindStatusListener;
        currentBindType = accountBean.getBindType();

        if(TextUtils.equals(YmConstants.GOOGLETYPE,currentBindType)){
            //TODO:google绑定
            googleRevokeAccess();
        }else if (TextUtils.equals(YmConstants.FBTYPE,currentBindType)){
            //TODO:fb绑定
            FBPluginApi.getInstance().logout(activity);
            fblogin(YmTypeConfig.BIND);
        }

    }

    public void loginAccess(AccountBean accountBean){
        String loginType = accountBean.getLoginType();
        switch (loginType){
            case YmConstants.FBTYPE:
                getFBInfo(accountBean);
                break;
            case YmConstants.GOOGLETYPE:
                getGoogleInfo(accountBean);
                break;
            case YmConstants.GUSETTYPE:
                getGusetInfo(accountBean);
                break;
        }
    }

    public void bindAccess(AccountBean accountBean){
        String bindType = accountBean.getBindType();
        switch (bindType){
            case YmConstants.FBTYPE:
                fbBind(accountBean);
                break;
            case YmConstants.GOOGLETYPE:
                googleBind(accountBean);
                break;
        }
    }

    private void fblogin(final int eventType) {
        pluginType = FACEBOOKPLUGIN;
        FBPluginApi.getInstance().login(mContext,null,new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                if (eventType == YmTypeConfig.LOGIN){
                    mAccountBean.setOpenId((String)o);
                    loginAccess(mAccountBean);
                }else if(eventType == YmTypeConfig.BIND){
                    mBindAccountBean.setOpenId((String)o);
                    bindAccess(mBindAccountBean);
                }


            }

            @Override
            public void onFailure(int code, String msg) {
                if (code== ErrorCode.CANCEL){
                    if (eventType == YmTypeConfig.LOGIN){
                        mLoginStatusListener.onCancel();
                    }else if(eventType == YmTypeConfig.BIND){
                        mBindStatusListener.onCancel();
                    }

                }else if(code==ErrorCode.FAILURE){
                    if (eventType == YmTypeConfig.LOGIN){
                        mLoginStatusListener.onFail(code,msg);
                    }else if (eventType == YmTypeConfig.BIND){
                        mBindStatusListener.onFail(code,msg);
                    }

                }
            }
        });
    }
    private void googleRevokeAccess(){
        GooglePluginApi.getInstance().revokeAccess(mContext, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                googlelogin(YmTypeConfig.BIND);
            }

            @Override
            public void onFailure(int code, String msg) {
                mBindStatusListener.onFail(code,msg);
            }
        });
    }

    private void googlelogin(final int eventType) {
        pluginType = GOOGLEPLUGIN;

        GooglePluginApi.getInstance().login(mContext,null,new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                if (eventType == YmTypeConfig.LOGIN){
                    mAccountBean.setOpenId((String)o);
                    loginAccess(mAccountBean);
                }else if(eventType == YmTypeConfig.BIND){
                    mBindAccountBean.setOpenId((String)o);
                    bindAccess(mBindAccountBean);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code== ErrorCode.CANCEL){
                    if (eventType == YmTypeConfig.LOGIN){
                        mLoginStatusListener.onCancel();
                    }else if(eventType == YmTypeConfig.BIND){
                        mBindStatusListener.onCancel();
                    }
                }else if (code==ErrorCode.FAILURE){
                    if (eventType == YmTypeConfig.LOGIN){
                        mLoginStatusListener.onFail(code,msg);
                    }else if (eventType == YmTypeConfig.BIND){
                        mBindStatusListener.onFail(code,msg);
                    }
                }
            }
        });
    }



    private void gtLogin() {
        String uuid ="";
        if (GooglePluginApi.getInstance()!=null){
            uuid = AdvertisingIdUtils.getAdvertisingId(mContext);
        }
        if (uuid.isEmpty()){
            uuid = YmFileUtils.getUUid(mContext);
        }
        mAccountBean.setUuid(uuid);
        getGusetInfo(mAccountBean);
    }

    @Override
    public void autoLogin(Context context, AccountBean accountBean, LoginStatusListener loginStatusListener) {
        mContext = context;
        mAccountBean = accountBean;
        mAutoLoginStatusListener = loginStatusListener;

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.UID, getLastUid(context));
        param.put(YmConstants.LOGINTOKEN, getLastToken(context));
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        param.put(YmConstants.NOTE,DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().quickLogin(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {
                                message.what = AUTOLOGINSUCCESS;
                                message.obj = body;
                            } else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = AUTOLOGINFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = AUTOLOGINNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = AUTOLOGINNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }



    @Override
    public void logout(final Activity activity) {
        String loginType = loginAccountInfo.getLoginType();
        if (TextUtils.equals(loginType,YmConstants.FBTYPE)){
            FBPluginApi.getInstance().logout(activity);
            loginAccountInfo = null;
            resetAccountInfo(activity);

        }else if (TextUtils.equals(loginType,YmConstants.GOOGLETYPE)){
            GooglePluginApi.getInstance().logout(activity, new CallBackListener() {
                @Override
                public void onSuccess(Object o) {
                    loginAccountInfo = null;
                    resetAccountInfo(activity);
                }

                @Override
                public void onFailure(int code, String msg) {
                }
            });
        }else {
            loginAccountInfo = null;
            resetAccountInfo(activity);
        }
    }




    private void getFBInfo(AccountBean accountBean) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.FBID, mAccountBean.getOpenId());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        param.put(YmConstants.NOTE, DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getFBAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = body;
                            } else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCOUNTFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    private void getGoogleInfo(AccountBean accountBean) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.GOOGLEID, mAccountBean.getOpenId());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        param.put(YmConstants.NOTE, DevicesUtils.getExtra());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getGoogleAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = body;
                            } else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCOUNTFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    private void getGusetInfo(AccountBean accountBean) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.UUID, accountBean.getUuid());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        param.put(YmConstants.NOTE,DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getGuestAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = body;
                            } else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCOUNTFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    private void fbBind(AccountBean accountBean) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, accountBean.getTimeStamp());
        param.put(YmConstants.UID, accountBean.getUid());
        param.put(YmConstants.LOGINTOKEN, accountBean.getLoginToken());
        param.put(YmConstants.FBID,accountBean.getOpenId());
        param.put(YmConstants.ACCESSTOKEN, accountBean.getAccessToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        param.put(YmConstants.NOTE,DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().bindFbAccount(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                body.getData().setUid(mBindAccountBean.getUid());
                                body.getData().setLoginToken(mBindAccountBean.getLoginToken());
                                body.getData().setLoginType(mBindAccountBean.getBindType());
                                message.what = BINDSUCCESS;
                                message.obj = body;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = BINDFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = BINDNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_DISCONNET;
                        message.obj = errorCode;
                        message.what = BINDNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    private void googleBind(AccountBean accountBean) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, accountBean.getTimeStamp());
        param.put(YmConstants.UID, accountBean.getUid());
        param.put(YmConstants.LOGINTOKEN, accountBean.getLoginToken());
        param.put(YmConstants.GOOGLEID,accountBean.getOpenId());
        param.put(YmConstants.ACCESSTOKEN, accountBean.getAccessToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        param.put(YmConstants.NOTE,DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().bindGoolgeAccount(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                body.getData().setUid(mAccountBean.getUid());
                                body.getData().setLoginToken(mAccountBean.getLoginToken());
                                message.what = BINDSUCCESS;
                                message.obj = body;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = BINDFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = BINDNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_DISCONNET;
                        message.obj = errorCode;
                        message.what = BINDNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (pluginType == GOOGLEPLUGIN){
            GooglePluginApi.getInstance().onActivityResult(context,requestCode,resultCode,data);
        }else if (pluginType == FACEBOOKPLUGIN){
            FBPluginApi.getInstance().onActivityResult(context,requestCode,resultCode,data);
        }

    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTCODE) {
            boolean isNeverAsk = ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                String uuid = YmFileUtils.getUUid(context);
                mAccountBean.setUuid(uuid);
                getGusetInfo(mAccountBean);

            } else {
                // permission denied.
                if (!isNeverAsk) {
                    AskForPermission();

                } else {
                    mLoginStatusListener.onFail(YmErrorCode.FAILURE,mContext.getString(ResourseIdUtils.getStringId("ym_logingt_fail")));
                }
            }
            return;
        }
    }



    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(ResourseIdUtils.getStringId("ym_text_writepermission")));
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //拒绝了存储权限，无法保存游客信息
                mLoginStatusListener.onCancel();
            }
        });
        builder.setPositiveButton(mContext.getString(ResourseIdUtils.getStringId("ym_text_setting")), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName())); // 根据包名打开对应的设置界面
                mContext.startActivity(intent);
            }
        });
        builder.create().show();
    }



}
