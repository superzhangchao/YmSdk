package com.ym.game.plugin.wechat.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ym.game.plugin.wechat.WechatConstants;
import com.ym.game.plugin.wechat.pay.WechatPay;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.frame.logger.Logger;
import com.ym.game.sdk.common.utils.LogUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;

import java.util.Map;

public class WechatLogin {

    private volatile static WechatLogin INSTANCE;
    private IWXAPI msgApi;
    private Context mContext;
    private CallBackListener mLoginCallback;
    private boolean loginStatus;

    private WechatLogin() {
    }

    public static WechatLogin getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatLogin.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WechatLogin();
                }
            }
        }
        return INSTANCE;
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int logincode = intent.getIntExtra("ERRORCODE", -1);
            loginStatus = false;
            switch (logincode) {
                case WechatConstants.LOGIN_RESULT_SUCC_CODE:
                    String usercode = intent.getStringExtra("USERCODE");
                    mLoginCallback.onSuccess(usercode);
                    try {
                        if (null != broadcastReceiver) {
                            mContext.unregisterReceiver(broadcastReceiver);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }

                    break;
                case WechatConstants.LOGIN_RESULT_CANCEL_CODE:
//                    mLoginStatusListener.onCancel();
                    mLoginCallback.onFailure(1,"wechat onCancel");
                    break;
                case WechatConstants.LOGIN_RESULT_FAIL_CODE:
//                    mLoginStatusListener.onFail(ErrorCode.LOGIN_FAIL,context.getString(ResourseIdUtils.getStringId("ym_loginwx_fail")));
                    mLoginCallback.onFailure(2,"wechat onFail");
                    break;
            }
        }
    };
    public void login(Context context, Map<String,Object> loginMap, CallBackListener loginCallback){
        mContext = context;
        mLoginCallback = loginCallback;
        loginStatus = true;
        IntentFilter filter = new IntentFilter(WechatConstants.WXLOGINACTION);
        context.registerReceiver(broadcastReceiver, filter);
        msgApi = WXAPIFactory.createWXAPI(mContext, WechatConstants.WX_APP_ID, false);
        if (msgApi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            msgApi.sendReq(req);
        } else {
            mLoginCallback.onFailure(2,"wechat onFail");
            ToastUtils.showToast(mContext, mContext.getString(ResourseIdUtils.getStringId("ym_no_install_wechat")));
        }

    }

    public void onResume(Context context){
        Logger.d("onResume");
        if (loginStatus){
            mLoginCallback.onFailure(ErrorCode.CANCEL,"pay cancel");
            loginStatus = false;
        }
    }
}
