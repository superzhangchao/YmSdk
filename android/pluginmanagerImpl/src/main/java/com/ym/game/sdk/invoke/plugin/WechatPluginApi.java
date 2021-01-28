package com.ym.game.sdk.invoke.plugin;

import android.app.Activity;
import android.content.Context;

import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;

import java.util.Map;

public class WechatPluginApi extends PluginReflectApi {

    private Plugin wechatPlugin;

    private volatile static WechatPluginApi INSTANCE;

    private WechatPluginApi() {
        wechatPlugin = PluginManager.getInstance().getPlugin("plugin_wechat");
    }

    public static WechatPluginApi getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatPluginApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WechatPluginApi();
                }
            }
        }
        return INSTANCE;
    }

    public void login(Context context,Map<String,Object>map,CallBackListener callBackListener){
        if (wechatPlugin!=null){
            invoke(wechatPlugin,"wechatLogin",new Class[]{Context.class,Map.class,CallBackListener.class},new Object[]{
                    context,map,callBackListener});
        }
    }

    /**
     * 调用微信app支付
     */
    public void pay(Context context, Map<String,Object> map, CallBackListener callBackListener){

        if (wechatPlugin != null){
            invoke(wechatPlugin,"wechatPay",new Class<?>[]{Context.class, Map.class, CallBackListener.class},
                    new Object[]{context, map, callBackListener});
        }
    }

    /**
     * 调用微信app支付
     */
    public void onResume(Context context){

        if (wechatPlugin != null){
            invoke(wechatPlugin,"onResume",new Class<?>[]{Context.class},
                    new Object[]{context});
        }
    }
}
