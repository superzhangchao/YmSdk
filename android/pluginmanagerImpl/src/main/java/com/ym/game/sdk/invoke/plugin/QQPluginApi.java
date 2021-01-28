package com.ym.game.sdk.invoke.plugin;

import android.content.Context;
import android.content.Intent;

import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;

import java.util.Map;

public class QQPluginApi extends PluginReflectApi {

    private Plugin qqPlugin;


    private volatile static QQPluginApi INSTANCE;

    private QQPluginApi() {
        qqPlugin = PluginManager.getInstance().getPlugin("plugin_qq");
    }

    public static QQPluginApi getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatPluginApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QQPluginApi();
                }
            }
        }
        return INSTANCE;
    }
    /**
     * 调用qq登录
     */
    public void login(Context context, Map<String,Object> map, CallBackListener callBackListener){

        if (qqPlugin != null){
            invoke(qqPlugin,"qqLogin",new Class<?>[]{Context.class, Map.class, CallBackListener.class},
                    new Object[]{context, map, callBackListener});
        }
    }

    public void logout(Context context){
        if (qqPlugin!=null){
            invoke(qqPlugin,"qqLogout",new Class[]{Context.class},new Object[]{context});
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (qqPlugin != null){
            invoke(qqPlugin,"onActivityResult",new Class[]{Context.class,int.class,int.class,Intent.class},
                    new Object[]{context,requestCode,resultCode,data});
        }

    }
}
