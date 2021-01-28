package com.ym.game.plugin.wechat;


import android.content.Context;

import com.ym.game.plugin.wechat.login.WechatLogin;
import com.ym.game.plugin.wechat.pay.WechatPay;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;

import java.util.Map;

public class WechatPlugin extends Plugin {
    private String TAG = "WeChatPlugin";
    private int eventType = 0;
    private int loginEvent = 1;
    private int payEvent = 2;
    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }
    /**
     * 调用weChat支付app接口
     */

    public void wechatLogin(Context context,Map<String,Object>loginMap,CallBackListener callBackListener){
        eventType = loginEvent;
        WechatLogin.getInstance().login(context,loginMap,callBackListener);

    }

    /**
     * 调用weChat支付app接口
     */
    public void wechatPay(Context context, Map<String,Object> payMap, CallBackListener callBackListener){
        eventType = payEvent;
        WechatPay.getInstance().pay(context,payMap,callBackListener);

    }



    /**
     * 根据当前的生命周期
     * @param context
     */
    @Override
    public void onResume(Context context) {
        if (eventType ==loginEvent){
            WechatLogin.getInstance().onResume(context);
        }else if (eventType ==payEvent){
            WechatPay.getInstance().onResume(context);
        }
    }
}
