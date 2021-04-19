package com.ym.game.sdk.invoke.plugin;

import android.app.Application;
import android.content.Context;

import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;

import java.util.Map;

public class AdjustPluginApi extends PluginReflectApi {

    private String TAG = "AdjustPluginApi";

    private Plugin adjustPlugin;

    private volatile static AdjustPluginApi INSTANCE;

    private AdjustPluginApi() {
        adjustPlugin = PluginManager.getInstance().getPlugin("plugin_alipay");
    }

    public static AdjustPluginApi getInstance() {
        if (INSTANCE == null) {
            synchronized (AdjustPluginApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdjustPluginApi();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 调用调用adjust初始化
     */
    public void init(Application context, Map<String, Object> map) {

        if (adjustPlugin != null) {
            invoke(adjustPlugin, "init", new Class<?>[]{Application.class, Map.class},
                    new Object[]{context, map});
        }

    }

}

