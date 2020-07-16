package com.ym.game.sdk.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.google.gson.Gson;

import com.orhanobut.logger.Logger;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.AccoutBean;


import com.ym.game.sdk.YmConstants;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;
import com.ym.game.utils.YmFileUtils;
import com.ym.game.utils.YmSignUtils;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YmLoginActivity extends AppCompatActivity {
    private static final String TAG = "Ymsdk";


    private static final int WEIXINVERIFYTOKEN = 0;
    private static final int QQVERIFYTOKEN = 1;
    private static final int VERIFYTOKEN = 2;

    private static final int VERIFYTOKENFAIL = 3;
    private static final int VERIFYTOKENNETERROR = 4;
    private static final int GETACCOUNTSUCCESS = 5;
    private static final int GETACCOUNTFAIL = 6;
    private static final int GETACCOUNTNETFAIL = 7;
    private static final int UPDATEQQUSERINFO = 8;
    private static final int GUESTVERIFYTOKEN = 9;
    private static final int SENDTIME = 10;
    private static final int SENDTIMEERROR = 11;
    private static final String LOGINTYPEQQ = "qq";
    private static final String LOGINTYPEWEIXIN = "weixin";
    private static final String LOGINTYPEGUEST = "guest";
    private static final int GONE_RECENT_LOGIN = 0;
    private static final int SHOW_WEIXIN_RECENT_LOGIN = 1;
    private static final int SHOW_QQ_RECENT_LOGIN = 2;
    private static final int SHOW_GUEST_RECENT_LOGIN = 3;

    private static final int PERMISSION_REQUESTCODE = 1001;


    private IWXAPI api;
    private Tencent mTencent;
    private IUiListener loginListener;
    private UserInfo mInfo;
    private TextView weixinrecent;
    private TextView qqRecent;
    private TextView guestRecent;
    private String uuid;
    private String currentLoginType;
    private TextView tvWeixinLogin;
    private LinearLayout llWeixinLogin;
    private String currentTs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourseIdUtils.getLayoutId("ym_activity_login"));
        setLoginScreenSize();
        setFullScreen();
        initView();
        IntentFilter filter = new IntentFilter(YmConstants.WXLOGINACTION);
        this.registerReceiver(broadcastReceiver, filter);

    }

    private void guestLogin() {
        String uuid = getAndroidId(this);
        if (uuid.isEmpty() || "9774d56d682e549c".equals(uuid)) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUESTCODE);
                return;
            } else {
                uuid = YmFileUtils.getUUid(this);
            }
        }
        setguestLogin(uuid);
    }

    private void setguestLogin(String uuid) {
        getToken(LOGINTYPEGUEST, uuid);
    }

    @SuppressLint("HardwareIds")
    private String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    private void initView() {
        weixinrecent = (TextView) findViewById(ResourseIdUtils.getId("tv_weixin_recent_login"));
        qqRecent = (TextView) findViewById(ResourseIdUtils.getId("tv_qq_recent_login"));
        guestRecent = (TextView) findViewById(ResourseIdUtils.getId("tv_guest_recent_login"));
        findViewById(ResourseIdUtils.getId("tv_weixin_login")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLastLoginType(SHOW_WEIXIN_RECENT_LOGIN);
                api = WXAPIFactory.createWXAPI(YmLoginActivity.this, YmConstants.WX_APP_ID, false);
                if (api.isWXAppInstalled()) {
//                    loginByWechat();
                    getTime(LOGINTYPEWEIXIN);
                } else {
                    ToastUtils.showToast(YmLoginActivity.this, getString(ResourseIdUtils.getStringId("ym_no_install_wechat")));
                }

            }
        });

        findViewById(ResourseIdUtils.getId("tv_qq_login")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLastLoginType(SHOW_QQ_RECENT_LOGIN);
//                loginByQQ();
                getTime(LOGINTYPEQQ);
            }
        });
        findViewById(ResourseIdUtils.getId("tv_guest_login")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLastLoginType(SHOW_GUEST_RECENT_LOGIN);
                getTime(LOGINTYPEGUEST);
            }
        });

        int lastLogin = getLastLogin();
        switch (lastLogin) {
            case GONE_RECENT_LOGIN:
                weixinrecent.setVisibility(View.GONE);
                qqRecent.setVisibility(View.GONE);
                guestRecent.setVisibility(View.GONE);
                break;
            case SHOW_WEIXIN_RECENT_LOGIN:
                weixinrecent.setVisibility(View.VISIBLE);
                qqRecent.setVisibility(View.GONE);
                guestRecent.setVisibility(View.GONE);
                break;
            case SHOW_QQ_RECENT_LOGIN:
                weixinrecent.setVisibility(View.GONE);
                qqRecent.setVisibility(View.VISIBLE);
                guestRecent.setVisibility(View.GONE);
                break;
            case SHOW_GUEST_RECENT_LOGIN:
                weixinrecent.setVisibility(View.GONE);
                qqRecent.setVisibility(View.GONE);
                guestRecent.setVisibility(View.VISIBLE);
                break;
            default:
                weixinrecent.setVisibility(View.GONE);
                qqRecent.setVisibility(View.GONE);
                guestRecent.setVisibility(View.GONE);
                break;

        }
    }

    private int getLastLogin() {
        SharedPreferences lastLoginType = getSharedPreferences(YmConstants.SVAE_LOGIN_TYPE, MODE_PRIVATE);
        return lastLoginType.getInt("lastLoginType", GONE_RECENT_LOGIN);
    }

    private void saveLastLoginType(int loginType) {
        SharedPreferences loginTye = getSharedPreferences(YmConstants.SVAE_LOGIN_TYPE, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginTye.edit();
        editor.putInt("lastLoginType", loginType);
        editor.putBoolean("isFastLogin", true);

        editor.apply();

    }

    private void resetFastLogin(boolean isFastLogin) {
        SharedPreferences loginTye = getSharedPreferences(YmConstants.SVAE_LOGIN_TYPE, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginTye.edit();
        editor.putBoolean("isFastLogin", isFastLogin);
        editor.apply();
    }

    private void setLoginScreenSize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screen_wight = dm.widthPixels;
        int screen_height = dm.heightPixels;

        params.width = (int) (screen_wight * 0.6);
        params.height = (int) (screen_height * 0.6);
        params.dimAmount = 0.0f;
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String loginType;
            String nickName;
            switch (msg.what) {
                case WEIXINVERIFYTOKEN:
                    HashMap<String, String> weixinParam = (HashMap<String, String>) msg.obj;
                    String weixinCode = weixinParam.get("weixinCode");
                    String weixinAccessToken = weixinParam.get("accessToken");
                    getWeixinInfo(weixinCode, weixinAccessToken);
                    break;
                case QQVERIFYTOKEN:
                    HashMap<String, String> qqParam = (HashMap<String, String>) msg.obj;
                    nickName = qqParam.get(YmConstants.NICKNAME);
                    String qqOpenId = qqParam.get(YmConstants.OPENID);
                    String qqAccessToken = qqParam.get("accessToken");
                    getQQInfo(qqOpenId, nickName, qqAccessToken);
                    break;
                case GUESTVERIFYTOKEN:
                    HashMap<String, String> guestParam = (HashMap<String, String>) msg.obj;
                    String uuid = guestParam.get(YmConstants.UUID);
                    String guestaccessToken = guestParam.get("accessToken");
                    getGusetInfo(uuid, guestaccessToken);
                    break;
                case VERIFYTOKEN:
                    HashMap<String, String> accountParam = (HashMap<String, String>) msg.obj;
                    String accountUserName = accountParam.get("userName");
                    String accountPwd = accountParam.get("password");
                    String accessToken = accountParam.get("accessToken");
//                    getAccountInfo(accountUserName,accountPwd,accessToken);
                    break;
                case VERIFYTOKENFAIL:
                    loginType = (String) msg.obj;
                    if (TextUtils.equals(loginType, LOGINTYPEQQ)) {
                        mTencent.logout(YmLoginActivity.this);
                    }
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
                    break;
                case VERIFYTOKENNETERROR:
                    loginType = (String) msg.obj;
                    if (TextUtils.equals(loginType, LOGINTYPEQQ)) {
                        mTencent.logout(YmLoginActivity.this);
                    }
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
                    break;
                case GETACCOUNTSUCCESS:
                    JSONObject dataInfo = (JSONObject) msg.obj;
                    String uid = dataInfo.optString("uid");
                    String token = dataInfo.optString("login_token");
                    nickName = "";
                    nickName = dataInfo.optString("nick_name");
                    resultLoginSuc(uid, token, currentLoginType, nickName);
                    break;
                case GETACCOUNTFAIL:
                    loginType = (String) msg.obj;
                    if (TextUtils.equals(loginType, LOGINTYPEQQ)) {
                        mTencent.logout(YmLoginActivity.this);
                    }
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
                    break;
                case GETACCOUNTNETFAIL:
                    loginType = (String) msg.obj;
                    if (TextUtils.equals(loginType, LOGINTYPEQQ)) {
                        mTencent.logout(YmLoginActivity.this);
                    }
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
                    break;
                case UPDATEQQUSERINFO:
                    JSONObject response = (JSONObject) msg.obj;
                    getToken(LOGINTYPEQQ, response.toString());
                    break;
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    String logintype = timeInfo.optString("logintype");
                    currentTs = timeInfo.optString("ts");
                    openLogin(logintype);

                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
                    break;
                default:
                    break;
            }
        }
    };

    private void openLogin(String logintype) {
        switch(logintype){
            case LOGINTYPEWEIXIN:
                loginByWechat();
                break;
            case LOGINTYPEQQ:
                loginByQQ();
                break;
            case LOGINTYPEGUEST:
                guestLogin();
                break;
            default:
                break;
        }
    }


    private void resultLoginSuc(String uid, String token, String currentLoginType, String nickName) {
        saveAccountInfo(uid, token);
        Intent intent = new Intent();//数据是使用Intent返回
        intent.putExtra("userId", uid);
        intent.putExtra("token", token);
        intent.putExtra("nickName", nickName);
        intent.putExtra("currentLoginType", currentLoginType);
        YmLoginActivity.this.setResult(YmConstants.LOGIN_SUCC_CODE, intent);//设置返回数据
        YmLoginActivity.this.finish();
    }

    private void saveAccountInfo(String uid, String token) {
        SharedPreferences loginTye = getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginTye.edit();
        editor.putString("uid", uid);
        editor.putString("token", token);
        editor.apply();
    }

    private void resultLoginNotSuc(int resultCode) {
        YmLoginActivity.this.setResult(resultCode);//设置返回数据
        YmLoginActivity.this.finish();
        resetFastLogin(false);
    }

    private  void getTime(final String logintype){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<String> token = YmApi.getInstance().getTime();
                token.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String body = response.body();
                        JSONObject timeInfo = new JSONObject();
                        Message message = new Message();
                        try {
                            timeInfo.put("logintype",logintype);
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
                        message.what = SENDTIMEERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getToken(final String accessType, final String accessParam) {

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
                            String accessToken = body.getData().getAccessToken();
                            Map<String, String> verifyInfo = new HashMap<>();
                            if (TextUtils.equals(LOGINTYPEWEIXIN, accessType) && !TextUtils.isEmpty(accessParam)) {
                                verifyInfo.put("weixinCode", accessParam);
                                currentLoginType = LOGINTYPEWEIXIN;
                                message.what = WEIXINVERIFYTOKEN;
                            } else if (TextUtils.equals(LOGINTYPEQQ, accessType) && !TextUtils.isEmpty(accessParam)) {
                                try {
                                    JSONObject qqInfo = new JSONObject(accessParam);
                                    verifyInfo.put(YmConstants.OPENID, mTencent.getQQToken().getOpenId());
                                    String nickname = qqInfo.optString("nickname");

                                    if (TextUtils.isEmpty(nickname) || nickname.trim().length()==0){
                                        nickname = "QQ用户";
                                    }
                                    verifyInfo.put(YmConstants.NICKNAME, nickname);
                                    currentLoginType = LOGINTYPEQQ;
                                    message.what = QQVERIFYTOKEN;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    message.what = VERIFYTOKENFAIL;
                                    message.obj = accessType;
                                }
                            } else if (TextUtils.equals(LOGINTYPEGUEST, accessType) && !TextUtils.isEmpty(accessParam)) {
                                verifyInfo.put(YmConstants.UUID, accessParam);
                                currentLoginType = LOGINTYPEGUEST;
                                message.what = GUESTVERIFYTOKEN;
                            } else {
                                //其他方式验证：
                                message.what = VERIFYTOKEN;
                            }
                            verifyInfo.put("accessToken", accessToken);
                            message.obj = verifyInfo;
                        } else {
                            message.what = VERIFYTOKENFAIL;
                            message.obj = accessType;
                        }
                        handler.sendMessage(message);

                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();
                        message.what = VERIFYTOKENNETERROR;
                        message.obj = accessType;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    private void getWeixinInfo(String weixinCode, String accessToken) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.WEIXINCODE, weixinCode);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<AccoutBean> token = YmApi.getInstance().getWeixinAccoutInfo(param);
                token.enqueue(new Callback<AccoutBean>() {
                    @Override
                    public void onResponse(Call<AccoutBean> call, Response<AccoutBean> response) {
                        AccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {
                            Gson gson = new Gson();
                            try {
                                JSONObject data = new JSONObject(gson.toJson(body.getData()));
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = data;
                            } catch (JSONException e) {
                                message.what = GETACCOUNTFAIL;
                                message.obj = LOGINTYPEWEIXIN;
                                e.printStackTrace();
                            }
                        } else {
                            message.what = GETACCOUNTFAIL;
                            message.obj = LOGINTYPEWEIXIN;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<AccoutBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETACCOUNTNETFAIL;
                        message.obj = LOGINTYPEWEIXIN;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getQQInfo(String qqOpenId, String nickName, String accessToken) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.OPENID, qqOpenId);
        param.put(YmConstants.NICKNAME, nickName);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<AccoutBean> token = YmApi.getInstance().getQQAccoutInfo(param);
                token.enqueue(new Callback<AccoutBean>() {
                    @Override
                    public void onResponse(Call<AccoutBean> call, Response<AccoutBean> response) {

                        AccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {
                            Gson gson = new Gson();
                            try {
                                JSONObject data = new JSONObject(gson.toJson(body.getData()));
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = data;
                            } catch (JSONException e) {
                                message.what = GETACCOUNTFAIL;
                                message.obj = LOGINTYPEQQ;
                                e.printStackTrace();
                            }
                        } else {
                            message.what = GETACCOUNTFAIL;
                            message.obj = LOGINTYPEQQ;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<AccoutBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETACCOUNTNETFAIL;
                        message.obj = LOGINTYPEQQ;
                        handler.sendMessage(message);

                    }
                });
            }
        }).start();
    }

    private void getGusetInfo(String uuid, String accessToken) {
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.UUID, uuid);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<AccoutBean> token = YmApi.getInstance().getGuestAccoutInfo(param);
                token.enqueue(new Callback<AccoutBean>() {
                    @Override
                    public void onResponse(Call<AccoutBean> call, Response<AccoutBean> response) {
                        AccoutBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {
                            Gson gson = new Gson();
                            try {
                                JSONObject data = new JSONObject(gson.toJson(body.getData()));
                                message.what = GETACCOUNTSUCCESS;
                                message.obj = data;
                            } catch (JSONException e) {
                                message.what = GETACCOUNTFAIL;
                                e.printStackTrace();
                            }
                        } else {
                            message.what = GETACCOUNTFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<AccoutBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = GETACCOUNTNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    public void loginByWechat() {
        // send oauth request

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    private void loginByQQ() {
        mTencent = Tencent.createInstance(this.getString(ResourseIdUtils.getStringId("qq_appid")),
                this.getApplicationContext(),
                this.getString(ResourseIdUtils.getStringId("qq_authorities")));
        if (!mTencent.isSessionValid()) {
            startQQLogin();
        } else {
            if (ready(YmLoginActivity.this)) {
                mTencent.checkLogin(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jsonResp = (JSONObject) response;
                        if (jsonResp.optInt("ret", -1) == 0) {
                            JSONObject jsonObject = mTencent.loadSession(getString(ResourseIdUtils.getStringId("qq_appid")));
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

    public boolean ready(Context context) {
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
                resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
            }

            @Override
            public void onCancel() {
                resultLoginNotSuc(YmConstants.LOGIN_CANCEL_CODE);
            }
        };
        mTencent.login(this, "all", loginListener);
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
                    mTencent.logout(YmLoginActivity.this);
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
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
                    mTencent.logout(YmLoginActivity.this);
                    resultLoginNotSuc(YmConstants.LOGIN_CANCEL_CODE);
                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int paycode = intent.getIntExtra("ERRORCODE", -1);
            switch (paycode) {
                case YmConstants.LOGIN_SUCC_CODE:
                    String usercode = intent.getStringExtra("USERCODE");

                    getToken(LOGINTYPEWEIXIN, usercode);
                    break;
                case YmConstants.LOGIN_CANCEL_CODE:
                    resultLoginNotSuc(YmConstants.LOGIN_CANCEL_CODE);
                    break;
                case YmConstants.LOGIN_FAIL_CODE:
                    resultLoginNotSuc(YmConstants.LOGIN_FAIL_CODE);
                    break;
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT > 18) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {

                        @SuppressLint("NewApi")
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {

                            getWindow()
                                    .getDecorView()
                                    .setSystemUiVisibility(
                                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }

                    });
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setFullScreen();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != broadcastReceiver) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (IllegalArgumentException e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        resultLoginNotSuc(YmConstants.LOGIN_CANCEL_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUESTCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                uuid = YmFileUtils.getUUid(this);

                setguestLogin(uuid);
            } else {
                // permission denied.
//                    AskForPermission();
                resultLoginNotSuc(YmConstants.LOGIN_CANCEL_CODE);
            }
            return;
        }
    }

    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("生成游客信息需要存储权限");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //拒绝了存储权限，无法保存游客信息
                resultLoginNotSuc(YmConstants.LOGIN_CANCEL_CODE);
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }
}
