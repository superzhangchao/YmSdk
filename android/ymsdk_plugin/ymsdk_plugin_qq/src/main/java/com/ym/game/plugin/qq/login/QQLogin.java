package com.ym.game.plugin.qq.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;

import org.json.JSONObject;


import java.util.Map;

import androidx.annotation.NonNull;

public class QQLogin {
    private static final int UPDATEQQUSERINFO = 1;
    public String TAG = "QQlogin";
    private volatile static QQLogin INSTANCE;
    private Tencent mTencent;
    private Context mContext;
    private IUiListener loginListener;
    private UserInfo mInfo;

    private QQLogin() {
    }

    public static QQLogin getInstance() {
        if (INSTANCE == null){
            synchronized (QQLogin.class){
                if (INSTANCE ==null){
                    INSTANCE = new QQLogin();
                }
            }
        }
        return INSTANCE;
    }
    private CallBackListener loginCallback;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Map<String, Object> errorData;
            String messageName = "ym_text_neterror";
            int netError = ErrorCode.NET_ERROR;
            switch (msg.what) {
                case UPDATEQQUSERINFO:
                    JSONObject response = (JSONObject) msg.obj;
                    String nickname = response.optString("nickname");
                    if (TextUtils.isEmpty(nickname) || nickname.trim().length() == 0) {
                        nickname = "QQ用户";
                    }
                    String openId = mTencent.getQQToken().getOpenId();

                    loginCallback.onSuccess(openId);
//                    mAccountBean.setNickName(nickname);
//                    mAccountBean.setOpenId(mTencent.getQQToken().getOpenId());
//                    getQQInfo();
                    break;
                default:
                    break;
            }
        }
    };


    public void login(final Context context, Map<String,Object> loginMap, CallBackListener callBackListener){
        loginCallback = callBackListener;
        mContext = context;
        mTencent = Tencent.createInstance(context.getString(ResourseIdUtils.getStringId("qq_appid")),
                context.getApplicationContext(),
                context.getString(ResourseIdUtils.getStringId("qq_authorities")));
        if (!mTencent.isSessionValid()) {
            startQQLogin();
        } else {
            if (ready(context)) {
                mTencent.checkLogin(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jsonResp = (JSONObject) response;
                        if (jsonResp.optInt("ret", -1) == 0) {
                            JSONObject jsonObject = mTencent.loadSession(context.getString(ResourseIdUtils.getStringId("qq_appid")));
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
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
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
                loginCallback.onFailure(2,"onFail");
            }

            @Override
            public void onCancel() {
//
                loginCallback.onFailure(3,"onCancel");
            }
        };
        mTencent.login((Activity) mContext, "all", loginListener);
    }

    private boolean ready(Context context) {
        if (mTencent == null) {
            return false;
        }
        boolean ready = mTencent.isSessionValid()
                && mTencent.getQQToken().getOpenId() != null;
        return ready;
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
                    mTencent.logout(mContext);
//                    mLoginStatusListener.onFail(ErrorCode.LOGIN_FAIL,mActivity.getString(ResourseIdUtils.getStringId("ym_loginqq_fail")));
                    loginCallback.onFailure(2,"onFail");
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
                    mTencent.logout(mContext);

                }
            };
            mInfo = new UserInfo(mContext, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        }
    }


}
