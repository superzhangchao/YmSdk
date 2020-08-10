package com.ym.game.sdk.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.net.bean.ResultVcodeBean;
import com.ym.game.net.bean.TokenBean;

import com.ym.game.sdk.YmConstants;
import com.ym.game.sdk.YmSdkApi;
import com.ym.game.sdk.base.config.ErrorCode;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.CheckBindListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.callback.listener.RealNameStatusListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;
import com.ym.game.utils.YmFileUtils;
import com.ym.game.utils.YmSignUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final int GETBINDSTATUSSUCCESS = 23;
    private static final int GETBINDSTATUNETFAIL = 24;

    private static final int PERMISSION_REQUESTCODE = 1001;

    private String currentTs;
    private SendVcodeListener mSendVcodeListener;
    private LoginStatusListener mLoginStatusListener;
    private LoginStatusListener mAutoLoginStatusListener;
    private LoginStatusListener mBindloginStatusListener;
    private RealNameStatusListener mRealNameStatusListener;
    private CheckBindListener mCheckBindListener;
    private String currentLoginType;
    private Context mContext;
    private String uuid;
    private Activity mActivity;
    private Tencent mTencent;
    private IUiListener loginListener;
    private UserInfo mInfo;
    private AccountBean mAccountBean;
    private AccountBean loginAccountInfo;
    private IWXAPI api;
    private boolean mIsLoginwx = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int paycode = intent.getIntExtra("ERRORCODE", -1);
            setWxLoginStatus(false);
            switch (paycode) {
                case YmConstants.LOGIN_SUCC_CODE:
                    String usercode = intent.getStringExtra("USERCODE");
                    mAccountBean.setWxCode(usercode);
                    getWeixinInfo();

                    break;
                case YmConstants.LOGIN_CANCEL_CODE:
                    mLoginStatusListener.onCancel();
                    break;
                case YmConstants.LOGIN_FAIL_CODE:
                    mLoginStatusListener.onFail(ErrorCode.LOGIN_FAIL,context.getString(ResourseIdUtils.getStringId("ym_loginwx_fail")));
                    break;
            }
        }
    };



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
                case GETBINDSTATUSSUCCESS:
                    errorData = (Map<String, Object>) msg.obj;
                    mCheckBindListener.getBindStatus((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETACCOUNTSUCCESS:
                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) msg.obj;
                    loginAccountInfo = new AccountBean();
                    loginAccountInfo.setUid(resultAccoutBean.getData().getUid());
                    loginAccountInfo.setLoginToken(resultAccoutBean.getData().getLoginToken());
                    loginAccountInfo.setNickName(resultAccoutBean.getData().getNickName());
                    loginAccountInfo.setAuthStatus(resultAccoutBean.getData().getAuthStatus());
                    saveAccountInfo(mActivity, resultAccoutBean.getData().getUid(), resultAccoutBean.getData().getLoginToken());
                    mLoginStatusListener.onSuccess(resultAccoutBean);
                    break;
                case UPDATEQQUSERINFO:
                    JSONObject response = (JSONObject) msg.obj;
                    String nickname = response.optString("nickname");
                    if (TextUtils.isEmpty(nickname) || nickname.trim().length() == 0) {
                        nickname = "QQ用户";
                    }
                    mAccountBean.setNickName(nickname);
                    mAccountBean.setOpenId(mTencent.getQQToken().getOpenId());
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
                    mRealNameStatusListener.onFail(ErrorCode.NET_ERROR, mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case BINDFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mBindloginStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case BINDNETFAIL:
                    mBindloginStatusListener.onFail(ErrorCode.NET_ERROR, mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case AUTOLOGINFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mAutoLoginStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case AUTOLOGINNETFAIL:
                    mAutoLoginStatusListener.onFail(ErrorCode.NET_ERROR, mActivity.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    mGetVerifyDataListener.onFail(ErrorCode.NET_DATA_NULL, mContext.getString(ResourseIdUtils.getStringId("ym_text_netdata_null")));
                    break;
                case SENDTIMENETERROR:
                    //TODO:请求ts网络失败
                    mGetVerifyDataListener.onFail(ErrorCode.NET_ERROR, mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case GETTOKENFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mGetVerifyDataListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETTOKENNETFAIL:
                    //TODO:网络请求token失败
                    mGetVerifyDataListener.onFail(ErrorCode.NET_ERROR, mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case GETBINDSTATUNETFAIL:
                    mCheckBindListener.onFail(ErrorCode.NET_ERROR, mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case SENDVCODEFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mSendVcodeListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case SENDVCODENETFAIL:
                    mSendVcodeListener.onFail(ErrorCode.NET_ERROR, mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case GETACCOUNTFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mLoginStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETACCOUNTNETFAIL:
                    mLoginStatusListener.onFail(ErrorCode.NET_ERROR, mActivity.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
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
    public void saveAccountInfo(Context context, String uid, String token) {
        SharedPreferences loginTye = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);

        SharedPreferences.Editor editor = loginTye.edit();
//        try {
//            uid = YmFileUtils.encryptDES(uid, YmConstants.APPID);
        editor.putString("uid", uid);
        editor.putString("token", token);
        editor.apply();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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
        SharedPreferences loginTye = activity.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        if(loginTye!=null){
            loginTye.edit().clear().apply();
        }
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
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultVcodeBean> token = YmApi.getInstance().getVcode(param);
                token.enqueue(new Callback<ResultVcodeBean>() {
                    @Override
                    public void onResponse(Call<ResultVcodeBean> call, Response<ResultVcodeBean> response) {
                        ResultVcodeBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE){
                            message.what = SENDVCODESUCCESS;
                        }else {
                            Map<String,Object> errorData = new HashMap<>();
                            errorData.put("code",body.getCode());
                            errorData.put("message",body.getMessage());
                            message.obj = errorData;
                            message.what = SENDVCODEFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultVcodeBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = SENDVCODENETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    @Override
    public void checkBind(Context context, String phone, String ts, String accessToken, CheckBindListener checkBindListener) {
        mCheckBindListener = checkBindListener;
        mContext = context;
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, ts);
        param.put(YmConstants.NUMBER, phone);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultVcodeBean> token = YmApi.getInstance().checkBind(param);
                token.enqueue(new Callback<ResultVcodeBean>() {
                    @Override
                    public void onResponse(Call<ResultVcodeBean> call, Response<ResultVcodeBean> response) {
                        ResultVcodeBean body = response.body();
                        Message message = new Message();
                        Map<String,Object> errorData = new HashMap<>();
                        errorData.put("code",body.getCode());
                        errorData.put("message",body.getMessage());
                        message.obj = errorData;
                        message.what = GETBINDSTATUSSUCCESS;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultVcodeBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETBINDSTATUNETFAIL;
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
                        String body = response.body();
                        JSONObject timeInfo = new JSONObject();
                        Message message = new Message();
                        try {
                            timeInfo.put("ts",body);
                            message.obj= timeInfo;
                            message.what = SENDTIME;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            message.what = SENDTIMEERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
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
                        TokenBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
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
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();

                        message.what = GETTOKENNETFAIL;
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

        if (TextUtils.equals("phone",currentLoginType)){
            phoneVerify();
        }else if (TextUtils.equals("qq",currentLoginType)){
            qqLogin();
        }else  if (TextUtils.equals("wx",currentLoginType)){
            IntentFilter filter = new IntentFilter(YmConstants.WXLOGINACTION);
            mActivity.registerReceiver(broadcastReceiver, filter);
            api = WXAPIFactory.createWXAPI(mActivity, YmConstants.WX_APP_ID, false);
            if (api.isWXAppInstalled()) {
                setWxLoginStatus(true);
                wxLogin();
            } else {
                mLoginStatusListener.onCancel();
                ToastUtils.showToast(mActivity, mActivity.getString(ResourseIdUtils.getStringId("ym_no_install_wechat")));
            }
        }else if (TextUtils.equals("guest",currentLoginType)){
            gtLogin();
        }
    }

    private void setWxLoginStatus(boolean isLoginwx) {
        mIsLoginwx = isLoginwx;
    }
    public boolean getWxLoginStatus(){
        return mIsLoginwx;
    }

    public void resetWxlogin(){
        mLoginStatusListener.onCancel();
    }

    private void phoneVerify() {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, mAccountBean.getTimeStamp());
        param.put(YmConstants.NUMBER, mAccountBean.getNumber());
        param.put(YmConstants.VCODE, mAccountBean.getVcode());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getPhoneAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE){
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = body;

                        }else {
                            Map<String,Object> errorData = new HashMap<>();
                            errorData.put("code",body.getCode());
                            errorData.put("message",body.getMessage());
                            message.obj = errorData;
                            message.what = GETACCOUNTFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }
//


    private void qqLogin() {
        mTencent = Tencent.createInstance(mActivity.getString(ResourseIdUtils.getStringId("qq_appid")),
                mActivity.getApplicationContext(),
                mActivity.getString(ResourseIdUtils.getStringId("qq_authorities")));
        if (!mTencent.isSessionValid()) {
            startQQLogin();
        } else {
            if (ready(mActivity)) {
                mTencent.checkLogin(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jsonResp = (JSONObject) response;
                        if (jsonResp.optInt("ret", -1) == 0) {
                            JSONObject jsonObject = mTencent.loadSession(mActivity.getString(ResourseIdUtils.getStringId("qq_appid")));
                            mTencent.initSessionCache(jsonObject);
                            updateUserInfo();
                        } else {
                            startQQLogin();
                        }
                    }

                    @Override
                    public void onError(UiError e) {
                        startQQLogin();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }

        }
    }

    private void wxLogin() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
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
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().quickLogin(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
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
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
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
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().bindAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
//                        ResultAccoutBean bindAccoutBean = new ResultAccoutBean();
//                        bindAccoutBean.setData();
                        body.getData().setUid(mAccountBean.getUid());
                        body.getData().setLoginToken(mAccountBean.getLoginToken());
                        body.getData().setAuthStatus(mAccountBean.getAuthStatus());
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE){
                            message.what = BINDSUCCESS;
                            message.obj = body;

                        }else {
                            Map<String,Object> errorData = new HashMap<>();
                            errorData.put("code",body.getCode());
                            errorData.put("message",body.getMessage());
                            message.obj = errorData;
                            message.what = BINDFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
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
        mTencent = Tencent.createInstance(activity.getString(ResourseIdUtils.getStringId("qq_appid")),
                activity.getApplicationContext(),
                activity.getString(ResourseIdUtils.getStringId("qq_authorities")));
        mTencent.logout(activity);
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
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().realName(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
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
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
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
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getQQAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {

                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
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
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);

                    }
                });
            }
        }).start();
    }

    private void getWeixinInfo() {
        try {
            if (null != broadcastReceiver) {
                mActivity.unregisterReceiver(broadcastReceiver);
            }
        } catch (IllegalArgumentException e) {
        }

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.WEIXINCODE, mAccountBean.getWxCode());
        param.put(YmConstants.ACCESSTOKEN, mAccountBean.getAccessToken());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getWeixinAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
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
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
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
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultAccoutBean> token = YmApi.getInstance().getGuestAccoutInfo(param);
                token.enqueue(new Callback<ResultAccoutBean>() {
                    @Override
                    public void onResponse(Call<ResultAccoutBean> call, Response<ResultAccoutBean> response) {
                        ResultAccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
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
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultAccoutBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }


    private boolean ready(Context context) {
        if (mTencent == null) {
            return false;
        }
        boolean ready = mTencent.isSessionValid()
                && mTencent.getQQToken().getOpenId() != null;
        return ready;
    }

    private void startQQLogin() {
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;
                initOpenidAndToken(response);
                updateUserInfo();

            }

            @Override
            public void onError(UiError uiError) {
                mLoginStatusListener.onFail(ErrorCode.LOGIN_FAIL,mActivity.getString(ResourseIdUtils.getStringId("ym_loginqq_fail")));
            }

            @Override
            public void onCancel() {
                mLoginStatusListener.onCancel();
            }
        };
        mTencent.login(mActivity, "all", loginListener);
    }

    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    mTencent.logout(mActivity);
                    mLoginStatusListener.onFail(ErrorCode.LOGIN_FAIL,mActivity.getString(ResourseIdUtils.getStringId("ym_loginqq_fail")));
                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = UPDATEQQUSERINFO;
                    handler.sendMessage(msg);

                }

                @Override
                public void onCancel() {
                    mTencent.logout(mActivity);

                }
            };
            mInfo = new UserInfo(mActivity, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        }
    }


    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);

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
