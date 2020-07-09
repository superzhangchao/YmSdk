package com.ym.game.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.tauth.Tencent;
import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.AccoutBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.config.ApplicationCache;
import com.ym.game.sdk.config.Config;

import com.ym.game.sdk.ui.activity.UserActivity;
import com.ym.game.sdk.ui.activity.YmLoginActivity;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.YmSignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.ym.game.sdk.base.config.TypeConfig.LOGIN;

public class YmSdkApi {


    private static final String TAG = "Ymsdk";
    private static final int GONE_RECENT_LOGIN = 0;

    private static final int FASTLOGINVERIFYTOKE = 0;
    private static final int VERIFYTOKENFAIL = 1;
    private static final int VERIFYTOKENNETERROR = 2;
    private static final int GETACCOUNTSUCCESS = 3;
    private static final int GETACCOUNTFAIL = 4;
    private static final int GETACCOUNTNETFAIL = 5;

    private volatile static YmSdkApi instance;
    private Context context;
    private LoginCallBack mLoginCallBack;
    private String currentLoginType;

    public static YmSdkApi getInstance() {

        if (instance == null) {
            synchronized (YmSdkApi.class) {
                if (instance == null) {
                    instance = new YmSdkApi();
                }
            }
        }
        return instance;
    }

    private YmSdkApi() {

    }

    /**
     * 初始化SDK
     *
     * @param gameId
     */
    public YmSdkApi initPlatform(Activity activity, String gameId) {
        this.context = activity;
        Config.setGameId(gameId);
        YmApi.setBaseUrl(YmConstants.BASEURL);
        return this;
    }


    public void setDebugMode(boolean isDebug) {
        if (isDebug) {
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
    }

    public void login(Activity activity, LoginCallBack loginCallBack) {
        mLoginCallBack = loginCallBack;
        if (isFastLogin()) {
            getFastLoginInfo(activity);
        } else {
            openYmLoginPage(activity);
        }
    }

    private void openYmLoginPage(Activity activity) {
        Intent intent = new Intent(activity, YmLoginActivity.class);
        intent.putExtra("type", LOGIN);

        activity.startActivityForResult(intent, YmConstants.LOGINPAGE);
    }

    private void getFastLoginInfo(Activity activity) {
        SharedPreferences fastLogin = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        String uid = fastLogin.getString("uid", "");
        String token = fastLogin.getString("token", "");
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            getfastLoginToken(uid,token);
        }else {
            openYmLoginPage(activity);
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FASTLOGINVERIFYTOKE:
                    HashMap<String, String> param = (HashMap<String, String>) msg.obj;
                    String uid = param.get(YmConstants.UID);
                    String loginToken = param.get(YmConstants.LOGINTOKEN);
                    String accessToken = param.get("accessToken");
                    verifyUserInfo(uid, loginToken, accessToken);
                    break;
                case GETACCOUNTSUCCESS:
                    JSONObject dataInfo = (JSONObject) msg.obj;
                    AccountBean accountBean = new AccountBean();
                    accountBean.setUid(dataInfo.optString("uid"));
                    accountBean.setToken(dataInfo.optString("login_token"));
                    accountBean.setNickName(dataInfo.optString("nick_name"));
                    saveAccountInfo(dataInfo.optString("uid"),dataInfo.optString("login_token"));
                    mLoginCallBack.onSuccess(accountBean);
                    break;
                case VERIFYTOKENFAIL:
                case VERIFYTOKENNETERROR:
                case GETACCOUNTFAIL:
                case GETACCOUNTNETFAIL:
                    resetFastLogin(false);
                    mLoginCallBack.onFailure(9012, "fail");
                    break;

            }
        }
    };

    private void verifyUserInfo(String uid, String loginToken, String accessToken) {
        int ts = (int) (System.currentTimeMillis() / 1000);
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, ts + "");
        param.put(YmConstants.UID, uid);
        param.put(YmConstants.LOGINTOKEN, loginToken);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<AccoutBean> token = YmApi.getInstance().quickLogin(param);
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

    private void getfastLoginToken(final String uid, final String loginToken) {
        final String ts = (int) (System.currentTimeMillis() / 1000)+"";

        Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, ts);
        param.put(YmConstants.FROMKEY, YmConstants.FROM);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<TokenBean> token = YmApi.getInstance().getTokenInfo(YmConstants.APPID, YmConstants.FROM,ts, sign);
                token.enqueue(new Callback<TokenBean>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenBean> call, @NonNull Response<TokenBean> response) {
                        TokenBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {
                            String accessToken = body.getData().getAccessToken();
                            Map<String, String> verifyInfo = new HashMap<>();

                            verifyInfo.put(YmConstants.UID, uid);
                            verifyInfo.put(YmConstants.LOGINTOKEN, loginToken);
                            verifyInfo.put("accessToken", accessToken);
                            message.what = FASTLOGINVERIFYTOKE;
                            message.obj = verifyInfo;
                        } else {
                            message.what = VERIFYTOKENFAIL;
                        }
                        handler.sendMessage(message);

                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();
                        message.what = VERIFYTOKENNETERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }


    private boolean isFastLogin() {
        SharedPreferences lastLoginType = context.getSharedPreferences(YmConstants.SVAE_LOGIN_TYPE, MODE_PRIVATE);
        return lastLoginType.getBoolean("isFastLogin", false);
    }

    private void saveAccountInfo(String uid, String token) {
        SharedPreferences loginTye = context.getSharedPreferences(YmConstants.SVAE_LOGIN_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginTye.edit();
        editor.putString("uid", uid);
        editor.putString("token", token);
        editor.apply();
    }
    public void resetFastLogin(boolean isFastLogin) {
        SharedPreferences loginTye = context.getSharedPreferences(YmConstants.SVAE_LOGIN_TYPE, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginTye.edit();
        editor.putBoolean("isFastLogin", isFastLogin);

        editor.apply();
    }

    public void logout(Activity activity) {
        resetFastLogin(false);
        if(!TextUtils.isEmpty(currentLoginType)&&TextUtils.equals(currentLoginType,"qq")){
            Tencent mTencent = Tencent.createInstance(activity.getString(ResourseIdUtils.getStringId("qq_appid")),
                    activity.getApplicationContext(),
                    activity.getString(ResourseIdUtils.getStringId("qq_authorities")));
            mTencent.logout(activity);
        }

    }

    public void onResume(Activity activity) {

    }

    public void onPuase(Activity activity) {

    }

    public void onDestroy(Activity activity) {
    }

    public void onConfigurationChanged(Activity activity) {

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == YmConstants.LOGINPAGE) {
            if (resultCode == YmConstants.LOGIN_SUCC_CODE) {
                AccountBean accountBean = new AccountBean();
                accountBean.setUid(data.getExtras().getString("userId"));
                accountBean.setToken(data.getExtras().getString("token"));
                accountBean.setNickName(data.getExtras().getString("nickName"));
                currentLoginType = data.getExtras().getString("currentLoginType");
                mLoginCallBack.onSuccess(accountBean);
            } else if (resultCode == YmConstants.LOGIN_CANCEL_CODE) {
                mLoginCallBack.onCancle();
            } else {
                mLoginCallBack.onFailure(9012, "fail");
            }

        }
    }

}
