package com.ym.game.sdk.invoke.plugin;

import android.content.Context;

import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;

public class AdjustDataPluginApi extends PluginReflectApi {

    private String TAG = "Adjust2PluginApi";

    private Plugin adjustPlugin;

    private volatile static AdjustDataPluginApi INSTANCE;

    private AdjustDataPluginApi() {
        adjustPlugin = PluginManager.getInstance().getPlugin("plugin_adjust");
    }

    public static AdjustDataPluginApi getInstance() {
        if (INSTANCE == null) {
            synchronized (AdjustDataPluginApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdjustDataPluginApi();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 调用调用adjust初始化
     */
    public void init(Context context,boolean debug) {

        if (adjustPlugin != null) {
            invoke(adjustPlugin, "initAdjust", new Class<?>[]{Context.class,boolean.class},
                    new Object[]{context,debug});
        }

    }

    public void trackEvent(String eventToken){
        if (adjustPlugin!=null){
            invoke(adjustPlugin,"trackEvent",new Class[]{String.class},new Object[]{eventToken});
        }
    }

    public void trackEventWithRevenue(String eventToken,double revenue,String orderId,String currency){
        if (adjustPlugin!=null){
            invoke(adjustPlugin,"trackEventWithRevenue",new Class[]{String.class,double.class,String.class,String.class},new Object[]{eventToken,revenue,orderId,currency});
        }
    }

}
