package com.ym.game.plugin.alipay;

import android.content.Context;

import com.ym.game.plugin.alipay.pay.AlipayPay;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;

import java.util.Map;

public class AlipayPlugin extends Plugin {
    private String TAG = "AlipayPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    /**
     * 调用支付宝支付app接口
     */
    public void alipay(Context context, Map<String,Object> payMap, CallBackListener callBackListener){
       AlipayPay.getInstance().pay(context,payMap,callBackListener);
    }
}
