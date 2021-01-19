package com.ym.game.sdk.invoke.plugin;

import android.content.Context;

import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;

import java.util.Map;

public class AlipayPluginApi extends PluginReflectApi {

    private String TAG = "AlipayPluginApi";

    private Plugin alipayPlugin;

    private volatile static AlipayPluginApi INSTANCE;

    private AlipayPluginApi() {
        alipayPlugin = PluginManager.getInstance().getPlugin("plugin_alipay");
    }

    public static AlipayPluginApi getInstance() {
        if (INSTANCE == null) {
            synchronized (AlipayPluginApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AlipayPluginApi();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 调用支付宝app支付
     */
    public void pay(Context context, Map<String, Object> map, CallBackListener callBackListener) {

        if (alipayPlugin != null) {
            invoke(alipayPlugin, "alipay", new Class<?>[]{Context.class, Map.class, CallBackListener.class},
                    new Object[]{context, map, callBackListener});
        }

    }

}

