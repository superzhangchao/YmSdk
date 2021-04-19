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

import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.net.bean.ResultVcodeBean;
import com.ym.game.net.bean.TokenBean;

import com.ym.game.sdk.YmConstants;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.HistoryAccountBean;
import com.ym.game.sdk.callback.listener.CheckRegisterListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.callback.listener.RealNameStatusListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;
import com.ym.game.sdk.callback.listener.SetPasswordStatusListener;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.YmFileUtils;
import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.game.sdk.invoke.plugin.QQPluginApi;
import com.ym.game.sdk.invoke.plugin.WechatPluginApi;
import com.ym.game.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class UserModel implements IUserModel {

    private static UserModel instance;
    private boolean needBind = false;
    private GetVerifyDataListener mGetVerifyDataListener;

    private static final int SENDTIME = 1;
    private static final int SENDTIMEERROR = 2;
    private static final int SENDTIMENETERROR = 3;
    private static final int GETTOKENSUCCESS = 4;
    private static final int GETTOKENFAIL = 5;
    private static final int GETTOKENNETFAIL = 6;
    private static final int SENDVCODESUCCESS = 7;
    private static final int SENDVCODEFAIL = 8;
    private static final int SENDVCODENETFAIL = 9;
    private static final int GETACCOUNTSUCCESS = 10;
    private static final int GETACCOUNTFAIL = 11;
    private static final int GETACCOUNTNETFAIL = 12;
    private static final int UPDATEQQUSERINFO = 13;
    private static final int AUTOLOGINSUCCESS = 14;
    private static final int AUTOLOGINFAIL = 15;
    private static final int AUTOLOGINNETFAIL = 16;
    private static final int BINDSUCCESS = 17;
    private static final int BINDFAIL = 18;
    private static final int BINDNETFAIL = 19;
    private static final int REALNAMESUCCESS = 20;
    private static final int REALNAMEFAIL = 21;
    private static final int REALNAMENETFAIL = 22;
    private static final int GETREGISTERSTATUSSUCCESS = 23;
    private static final int GETREGISTERSTATUNETFAIL = 24;
    private static final int SETPWDSUCCESS = 25;
    private static final int SETPWDFAIL = 26;
    private static final int SETPWDNETFAIL = 27;


    private static final int PERMISSION_REQUESTCODE = 1001;

    private String currentTs;
    private SendVcodeListener mSendVcodeListener;
    private LoginStatusListener mLoginStatusListener;
    private SetPasswordStatusListener mSetPasswordStatusListener;
    private LoginStatusListener mAutoLoginStatusListener;
    private LoginStatusListener mBindloginStatusListener;
    private RealNameStatusListener mRealNameStatusListener;
    private CheckRegisterListener mCheckRegisterListener;

    private String currentLoginType;
    private Context mContext;
    private String uuid;
    private Activity mActivity;

    private AccountBean mAccountBean;
    private AccountBean loginAccountInfo;

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
            int netError = ErrorCode.NET_ERROR;
            switch (msg.what) {
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    currentTs = timeInfo.optString("ts");
                    getToken();
                    break;
                case GETTOKENSUCCESS:
                    //TODO:
                    String accessToken = (String) msg.obj;
                    mGetVerifyDataListener.onSuccess(currentTs, accessToken);
                    break;
                case SENDVCODESUCCESS:
                    mSendVcodeListener.onSuccess();
                    break;
                case GETREGISTERSTATUSSUCCESS:
                    errorData = (Map<String, Object>) msg.obj;
                    mCheckRegisterListener.getRegisterStatus((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETACCOUNTSUCCESS:
                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo = new AccountBean();
                    loginAccountInfo.setUid(resultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(resultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setNickName(resultAccoutBean.getData().getNickName());
                    loginAccountInfo.setAuthStatus(resultAccoutBean.getData().getAuthStatus());
                    saveAccountInfoBean = new AccountBean();
                    saveAccountInfoBean.setUid(resultAccoutBean.getData().getUid());
                    saveAccountInfoBean.setLoginToken(resultAccoutBean.getData().getLoginToken());
                    saveAccountInfoBean.setLoginType(resultAccoutBean.getData().getLoginType());

                    if (!TextUtils.isEmpty(resultAccoutBean.getData().getPhoneNumber())){
                        String phoneNumber = resultAccoutBean.getData().getPhoneNumber();
                        saveAccountInfoBean.setNumber(phoneNumber);
                        if (resultAccoutBean.getData().isHasPassword()){
                            //TODO:保存历史账号密码
                            saveAccountInfoBean.setHasPassword(true);
                            String password = resultAccoutBean.getData().getPassword();
                            ArrayList<HistoryAccountBean> historyAccountBeanList = (ArrayList<HistoryAccountBean>) SharedPreferencesUtils.getHistoryAccountBean(mActivity, "histroyAccount", "histroyAccount");
                            HistoryAccountBean historyAccountBean  = new HistoryAccountBean(phoneNumber,password,System.currentTimeMillis(),false);
                            if (historyAccountBeanList==null){
                                historyAccountBeanList = new ArrayList<>();
                            }
                            for (int i = 0; i < historyAccountBeanList.size(); i++) {
                                if (TextUtils.equals(historyAccountBeanList.get(i).getPhone(), historyAccountBean.getPhone())){
                                    historyAccountBeanList.remove(i);
                                }
                            }
                            historyAccountBeanList.add(historyAccountBean);
                            SharedPreferencesUtils.putHistoryAccountBean(mActivity, "histroyAccount",historyAccountBeanList,"histroyAccount" );
                        }
                    }

                    mLoginStatusListener.onSuccess(resultAccoutBean);
                    break;
                case UPDATEQQUSERINFO:
                    JSONObject response = (JSONObject) msg.obj;
                    String nickname = response.optString("nickname");
                    if (TextUtils.isEmpty(nickname) || nickname.trim().length() == 0) {
                        nickname = "QQ用户";
                    }
                    mAccountBean.setNickName(nickname);
                    mAccountBean.setOpenId(response.optString("openId"));
                    getQQInfo();
                    break;
                case AUTOLOGINSUCCESS:
                    ResultAccoutBean autoResultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo = new AccountBean();
                    loginAccountInfo.setUid(autoResultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(autoResultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setNickName(autoResultAccoutBean.getData().getNickName());
                    loginAccountInfo.setAuthStatus(autoResultAccoutBean.getData().getAuthStatus());
                    mAutoLoginStatusListener.onSuccess(autoResultAccoutBean);
                    break;
                case BINDSUCCESS:
                    ResultAccoutBean bindResultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo.setUid(bindResultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(bindResultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setNickName(bindResultAccoutBean.getData().getNickName());
                    mBindloginStatusListener.onSuccess(bindResultAccoutBean);
                    if(saveAccountInfoBean!=null&&TextUtils.equals(saveAccountInfoBean.getLoginType(),YmConstants.GUSETLOGIN)){
                        saveAccountInfoBean.setLoginType(YmConstants.PHONELOGIN);
                        saveAccountInfoBean.setNumber(bindResultAccoutBean.getData().getPhoneNumber());
                    }
                    break;
                case REALNAMESUCCESS:
                    ResultAccoutBean realNameResultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo.setAuthStatus(realNameResultAccoutBean.getData().getAuthStatus());
                    mRealNameStatusListener.onSuccess(realNameResultAccoutBean);
                    break;
                case REALNAMEFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mRealNameStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case REALNAMENETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mRealNameStatusListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case BINDFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mBindloginStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case BINDNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mBindloginStatusListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
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
                    mAutoLoginStatusListener.onFail(netError, mActivity.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    mGetVerifyDataListener.onFail(ErrorCode.NET_DATA_NULL, mContext.getString(ResourseIdUtils.getStringId("ym_text_netdata_null")));
                    break;
                case SENDTIMENETERROR:
                    //TODO:请求ts网络失败
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mGetVerifyDataListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case GETTOKENFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mGetVerifyDataListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETTOKENNETFAIL:
                    //TODO:网络请求token失败
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mGetVerifyDataListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case GETREGISTERSTATUNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mCheckRegisterListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case SENDVCODEFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mSendVcodeListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case SENDVCODENETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mSendVcodeListener.onFail(netError, mContext.getString(ResourseIdUtils.getStringId(messageName)));
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

                    mLoginStatusListener.onFail(netError, mActivity.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case SETPWDSUCCESS:
                    mSetPasswordStatusListener.onSuccess();
                    break;
                case SETPWDFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mSetPasswordStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case SETPWDNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mSetPasswordStatusListener.onFail(netError, mActivity.getString(ResourseIdUtils.getStringId(messageName)));
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
    public void saveAccountInfo(Context context) {
        if (saveAccountInfoBean !=null){
            SharedPreferences loginTye = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);

            SharedPreferences.Editor editor = loginTye.edit();

            editor.putString("uid", saveAccountInfoBean.getUid());
            editor.putString("token", saveAccountInfoBean.getLoginToken());
            editor.putString("loginType", saveAccountInfoBean.getLoginType());
            if (!TextUtils.isEmpty(saveAccountInfoBean.getNumber())){
                editor.putString("phoneNumber", saveAccountInfoBean.getNumber());
            }
            if (saveAccountInfoBean.isHasPassword()){
                editor.putBoolean("hasPassword",saveAccountInfoBean.isHasPassword());
            }else {
                editor.putBoolean("hasPassword",false);
            }
            editor.apply();
            }
    }

    @Override
    public void saveXieyiStatud(Context context, boolean xieyiStatus){
        SharedPreferences userInfo = context.getSharedPreferences(YmConstants.SVAE_XIEYI_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putBoolean("xieyiStatus",xieyiStatus);
        editor.apply();
    }
    @Override
    public boolean getXieyiStatus(Context context){
        SharedPreferences userInfo = context.getSharedPreferences(YmConstants.SVAE_XIEYI_INFO, MODE_PRIVATE);
        return userInfo.getBoolean("xieyiStatus",false);

    }

    @Override
    public int getRealNameStatus() {
        return loginAccountInfo.getAuthStatus();
    }

    public void resetAccountInfo(Activity activity) {
        SharedPreferences loginInfo = activity.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        if(loginInfo!=null){
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.remove("uid");
            editor.remove("token");
//            loginTye.edit().clear().apply();
            editor.commit();
        }
    }

    public AccountBean getLastNormalLoginInfo(Activity activity){
        AccountBean accountBean = new AccountBean();
        SharedPreferences loginInfo = activity.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        String loginType = loginInfo.getString("loginType", "");
        accountBean.setLoginType(loginType);
        if (TextUtils.equals(loginType,YmConstants.PHONELOGIN)){
            String phoneNumber = loginInfo.getString("phoneNumber", "");
            boolean hasPassword = loginInfo.getBoolean("hasPassword",false);
            accountBean.setNumber(phoneNumber);
            accountBean.setHasPassword(hasPassword);
        }
        return accountBean;
    }

    @Override
    public String getUid(Context context) {
        SharedPreferences fastLogin = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        String uid = fastLogin.getString("uid", "");
//        try {
//            return YmFileUtils.decryptDES(uid,YmConstants.APPID);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
        return uid;
    }
    @Override
    public String getToken(Context context) {
        SharedPreferences fastLogin = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        String token = fastLogin.getString("token", "");
        return token;
    }

    @Override
    public void getVerifyData(Context context,GetVerifyDataListener getVerifyDataListener) {
        mGetVerifyDataListener = getVerifyDataListener;
        mContext = context;
        getTime();

    }

    @Override
    public void sendVcode(Context context, String phone,String ts,String accessToken, SendVcodeListener sendVcodeListener) {
        mSendVcodeListener = sendVcodeListener;
        mContext = context;
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, ts);
        param.put(YmConstants.NUMBER, phone);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultVcodeBean> token = YmApi.getInstance().getVcode(param);
                token.enqueue(new Callback<ResultVcodeBean>() {
                    @Override
                    public void onResponse(Call<ResultVcodeBean> call, Response<ResultVcodeBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultVcodeBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = SENDVCODESUCCESS;
                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = SENDVCODEFAIL;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = SENDVCODENETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultVcodeBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = SENDVCODENETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }


    @Override
    public void checkRegister(Context context, String phone, String ts, String accessToken, CheckRegisterListener checkRegisterListener) {
        mCheckRegisterListener = checkRegisterListener;
        mContext = context;
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, ts);
        param.put(YmConstants.NUMBER, phone);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultVcodeBean> token = YmApi.getInstance().checkRegister(param);
                token.enqueue(new Callback<ResultVcodeBean>() {
                    @Override
                    public void onResponse(Call<ResultVcodeBean> call, Response<ResultVcodeBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultVcodeBean body = response.body();
                            Map<String,Object> errorData = new HashMap<>();
                            errorData.put("code",body.getCode());
                            errorData.put("message",body.getMessage());
                            message.obj = errorData;
                            message.what = GETREGISTERSTATUSSUCCESS;
                            handler.sendMessage(message);
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETREGISTERSTATUNETFAIL;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultVcodeBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETREGISTERSTATUNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }


    private  void getTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localTs = System.currentTimeMillis()+"";
                Call<String> token = YmApi.getInstance().getTime(localTs);
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
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = SENDTIMENETERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = SENDTIMENETERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getToken() {

        Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.FROMKEY, YmConstants.FROM);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<TokenBean> token = YmApi.getInstance().getTokenInfo(YmConstants.APPID, YmConstants.FROM, currentTs, sign);
                token.enqueue(new Callback<TokenBean>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenBean> call, @NonNull Response<TokenBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            TokenBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {
                                message.obj = body.getData().getAccessToken();
                                message.what = GETTOKENSUCCESS;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETTOKENFAIL;
                            }
                        }else{
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETTOKENNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETTOKENNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    public void startRegister(Activity activity, AccountBean accountBean, LoginStatusListener loginStatusListener) {
        mActivity = activity;
        mAccountBean = accountBean;
        mLoginStatusListener = loginStatusListener;
        //TODO:账号密码注册
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.NUMBER, mAccountBean.getNumber());
        param.put(YmConstants.VCODE, mAccountBean.getVcode());
        param.put(YmConstants.PASSWORD, mAccountBean.getPassword());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().register(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if(response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = GETACCOUNTSUCCESS;
                                body.getData().setPhoneNumber(mAccountBean.getNumber());
                                body.getData().setPassword(mAccountBean.getPassword());
                                message.obj = body;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCOUNTFAIL;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    public void startSetPwd(Activity activity, AccountBean accountBean, SetPasswordStatusListener setPasswordStatusListener) {
        mActivity = activity;
        mAccountBean = accountBean;
        mSetPasswordStatusListener = setPasswordStatusListener;
        //TODO:账号密码设置
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.NUMBER, mAccountBean.getNumber());
        param.put(YmConstants.PASSWORD, mAccountBean.getPassword());
        param.put(YmConstants.VCODE,mAccountBean.getVcode());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().resetPassword(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if(response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = SETPWDSUCCESS;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = SETPWDFAIL;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = SETPWDNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = SETPWDNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    @Override
    public void loginByType(Activity activity, AccountBean accountBean, LoginStatusListener loginStatusListener) {
        mActivity = activity;
        mAccountBean = accountBean;
        mLoginStatusListener = loginStatusListener;
        currentLoginType = accountBean.getLoginType();

        if (TextUtils.equals(YmConstants.PHONELOGIN,currentLoginType)) {
            if (accountBean.isHasPassword()){
                //TODO:密码登录
                pwdLogin();
            }else {
                phoneVerify();
            }
        }else if (TextUtils.equals(YmConstants.QQLOGIN,currentLoginType)){
            qqLogin();
        }else  if (TextUtils.equals(YmConstants.WXLOGIN,currentLoginType)){
//            IntentFilter filter = new IntentFilter(YmConstants.WXLOGINACTION);
//            mActivity.registerReceiver(broadcastReceiver, filter);
//            api = WXAPIFactory.createWXAPI(mActivity, YmConstants.WX_APP_ID, false);
//            if (api.isWXAppInstalled()) {
//                setWxLoginStatus(true);
                wxLogin();
//            } else {
//                mLoginStatusListener.onCancel();
//                ToastUtils.showToast(mActivity, mActivity.getString(ResourseIdUtils.getStringId("ym_no_install_wechat")));
//            }
        }else if (TextUtils.equals(YmConstants.GUSETLOGIN,currentLoginType)){
            gtLogin();
        }
    }

//    private void setWxLoginStatus(boolean isLoginwx) {
//        mIsLoginwx = isLoginwx;
//    }
//    public boolean getWxLoginStatus(){
//        return mIsLoginwx;
//    }

//    public void resetWxlogin(){
//        mLoginStatusListener.onCancel();
//    }

    private void phoneVerify() {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.NUMBER, mAccountBean.getNumber());
        param.put(YmConstants.VCODE, mAccountBean.getVcode());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getPhoneAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if(response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = GETACCOUNTSUCCESS;
                                body.getData().setPhoneNumber(mAccountBean.getNumber());
                                message.obj = body;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCOUNTFAIL;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void pwdLogin(){
        //TODO：密码登录
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.NUMBER, mAccountBean.getNumber());
        param.put(YmConstants.PASSWORD, mAccountBean.getPassword());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getPasswordAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if(response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = GETACCOUNTSUCCESS;
                                body.getData().setPhoneNumber(mAccountBean.getNumber());
                                body.getData().setPassword(mAccountBean.getPassword());
                                message.obj = body;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = GETACCOUNTFAIL;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }


    private void qqLogin() {
        CallBackListener qqLoginCallbackListener = new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                JSONObject response = new JSONObject();
                try {
                    response.put("nickName","QQ用户");
                    response.put("openId",o);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.obj = response;
                msg.what = UPDATEQQUSERINFO;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        };
        QQPluginApi.getInstance().login(mActivity,null,qqLoginCallbackListener);
    }

    private void wxLogin() {
//        SendAuth.Req req = new SendAuth.Req();
//        req.scope = "snsapi_userinfo";
//        req.state = "wechat_sdk_demo_test";
//        api.sendReq(req);
        CallBackListener wechatCallback = new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                String wechatCode = (String) o;
                getWeixinInfo(wechatCode);
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        };
        WechatPluginApi.getInstance().login(mActivity,null,wechatCallback);
    }

    private void gtLogin() {
        String uuid = getAndroidId();
        if (uuid.isEmpty() || "9774d56d682e549c".equals(uuid)) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUESTCODE);
                return;
            } else {
                uuid = YmFileUtils.getUUid(mActivity);
            }
        }
        mAccountBean.setUuid(uuid);
        getGusetInfo();
    }

    @SuppressLint("HardwareIds")
    private String getAndroidId() {
        return Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    @Override
    public void autoLogin(Context context, AccountBean accountBean, LoginStatusListener loginStatusListener) {
        mActivity = (Activity) context;
        mAccountBean = accountBean;
        mAutoLoginStatusListener = loginStatusListener;

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.UID, getUid(context));
        param.put(YmConstants.LOGINTOKEN, getToken(context));
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
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
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = AUTOLOGINNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = AUTOLOGINNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    @Override
    public void bindAccount(Activity context, AccountBean accountBean, LoginStatusListener loginStatusListener) {
        mBindloginStatusListener = loginStatusListener;
        mAccountBean=accountBean;
        mContext = context;
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, accountBean.getTimeStamp());
        param.put(YmConstants.UID, accountBean.getUid());
        param.put(YmConstants.LOGINTOKEN, accountBean.getLoginToken());
        param.put(YmConstants.NUMBER, accountBean.getNumber());
        param.put(YmConstants.VCODE, accountBean.getVcode());
        param.put(YmConstants.ACCESSTOKEN, accountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().bindAccoutInfo(param);
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
                                body.getData().setAuthStatus(mAccountBean.getAuthStatus());
                                body.getData().setPhoneNumber(mAccountBean.getNumber());
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
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = BINDNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_DISCONNET;
                        message.obj = errorCode;
                        message.what = BINDNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    @Override
    public void logout(Activity activity) {
        loginAccountInfo = null;
        clearQQInfo(activity);
        resetAccountInfo(activity);
    }

    private void clearQQInfo(Activity activity) {
       QQPluginApi.getInstance().logout(activity);
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

    @Override
    public void realName(Activity context, AccountBean accountBean, RealNameStatusListener realNameStatusListener) {
        mRealNameStatusListener = realNameStatusListener;
        mContext = context;
        mAccountBean = accountBean;
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, accountBean.getTimeStamp());
        param.put(YmConstants.UID, accountBean.getUid());
        param.put(YmConstants.LOGINTOKEN, accountBean.getLoginToken());
        param.put(YmConstants.NAME, accountBean.getName());
        param.put(YmConstants.IDCARD, accountBean.getIdCard());
        param.put(YmConstants.ACCESSTOKEN, accountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().realName(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()){
                            ResultAccoutBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = REALNAMESUCCESS;
                                message.obj = body;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",body.getCode());
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = REALNAMEFAIL;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = REALNAMENETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = REALNAMENETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }


    private void getQQInfo() {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.OPENID, mAccountBean.getOpenId());
        param.put(YmConstants.NICKNAME, mAccountBean.getNickName());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getQQAccoutInfo(param);
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
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);

                    }
                });
            }
        }).start();
    }

    private void getWeixinInfo(String wxCode) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.WEIXINCODE, wxCode);
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getWeixinAccoutInfo(param);
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
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getGusetInfo() {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.UUID, mAccountBean.getUuid());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN, sign);
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
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETACCOUNTNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        QQPluginApi.getInstance().onActivityResult(context,requestCode,resultCode,data);

    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTCODE) {
            boolean isNeverAsk = ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                String uuid = YmFileUtils.getUUid(context);
                mAccountBean.setUuid(uuid);
                getGusetInfo();

            } else {
                // permission denied.
                if (!isNeverAsk) {
                    AskForPermission();

                } else {
                    mLoginStatusListener.onFail(ErrorCode.FAILURE,mActivity.getString(ResourseIdUtils.getStringId("ym_logingt_fail")));
                }
            }
            return;
        }
    }



    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(ResourseIdUtils.getStringId("ym_text_writepermission")));
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //拒绝了存储权限，无法保存游客信息
                mLoginStatusListener.onCancel();
            }
        });
        builder.setPositiveButton(mActivity.getString(ResourseIdUtils.getStringId("ym_text_setting")), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mActivity.getPackageName())); // 根据包名打开对应的设置界面
                mActivity.startActivity(intent);
            }
        });
        builder.create().show();
    }


}
