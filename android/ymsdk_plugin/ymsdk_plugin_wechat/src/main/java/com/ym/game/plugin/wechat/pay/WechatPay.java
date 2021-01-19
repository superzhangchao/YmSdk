package com.ym.game.plugin.wechat.pay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ym.game.plugin.wechat.WechatConstants;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.LogUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;

import java.util.Map;

public class WechatPay {


    public String TAG = "WechatPay";

    private volatile static WechatPay INSTANCE;
    private IWXAPI msgApi;

    private WechatPay() {
    }

    public static WechatPay getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatPay.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WechatPay();
                }
            }
        }
        return INSTANCE;
    }

    private CallBackListener paycallback;
    private boolean payStatus = false;
    private BroadcastReceiver wxPayBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            payStatus = false;
            int paycode = intent.getIntExtra("PAYCODE", -1);
            switch (paycode) {
                case ErrorCode.SUCCESS:
                    paycallback.onSuccess(null);
                    break;
                case ErrorCode.CANCEL:
                    paycallback.onFailure(ErrorCode.CANCEL,"pay cancel");
                    break;
                case ErrorCode.FAILURE:
                    paycallback.onFailure(ErrorCode.FAILURE,"pay failure");
                    break;
                default:
                    break;
            }
            try {
                if (null != wxPayBroadcastReceiver) {
                    context.unregisterReceiver(wxPayBroadcastReceiver);
                }
            } catch (IllegalArgumentException e) {
//            Log.e("ysqy",  e.toString());
            }
        }
    };

    public void pay(Context context, Map<String, Object> payMap, CallBackListener callBackListener) {
        paycallback =callBackListener;
        payStatus = true;
        IntentFilter filter = new IntentFilter(WechatConstants.WXPAYACTION);
        context.registerReceiver(wxPayBroadcastReceiver, filter);
        msgApi = WXAPIFactory.createWXAPI(context, WechatConstants.WX_APP_ID, false);
        msgApi.registerApp(WechatConstants.WX_APP_ID);
        PayReq request = new PayReq();
        request.appId = (String) payMap.get("appId");
        request.partnerId = (String) payMap.get("partnerId");
        request.prepayId = (String) payMap.get("prepayId");
        request.packageValue = (String) payMap.get("packageValue");
        request.nonceStr = (String) payMap.get("nonceStr");
        request.timeStamp = (String) payMap.get("timeStamp");
        request.sign = (String) payMap.get("sign");
        int wxSdkVersion = msgApi.getWXAppSupportAPI();
        if (wxSdkVersion >= Build.OFFLINE_PAY_SDK_INT) {
//            setWxPayStatus(true);
            msgApi.sendReq(request);
        } else {
            paycallback.onFailure(ErrorCode.CANCEL,"pay cancel");
            ToastUtils.showToast(context, context.getString(ResourseIdUtils.getStringId("ym_no_install_wechat")));

        }
    }

    /**
     * 处理微信没有回调的问题
     * @param context
     */
    public void onResume(Context context) {
        LogUtils.debug_d(TAG,"onResume");
        if (payStatus){
            paycallback.onFailure(ErrorCode.CANCEL,"pay cancel");
            payStatus = false;
        }
    }
}
