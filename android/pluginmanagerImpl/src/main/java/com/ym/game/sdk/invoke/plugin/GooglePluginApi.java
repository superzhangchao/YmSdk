package com.ym.game.sdk.invoke.plugin;

import android.content.Context;
import android.content.Intent;

import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;

import java.util.Map;

public class GooglePluginApi extends PluginReflectApi {
    private volatile static GooglePluginApi INSTANCE;
    private final Plugin googlePlugin;

    public GooglePluginApi(){
        googlePlugin = PluginManager.getInstance().getPlugin("plugin_google");
    }
    public static GooglePluginApi getInstance(){
        if (INSTANCE==null){
            synchronized (GooglePluginApi.class){
                if (INSTANCE==null){
                    INSTANCE = new GooglePluginApi();
                }
            }
        }
        return INSTANCE;
    }

    //接入google登录
    public void login(Context context, Map<String,Object> map, CallBackListener callBackListener){
        if (googlePlugin != null) {
            invoke(googlePlugin, "googleLogin", new Class<?>[]{Context.class, Map.class, CallBackListener.class},
                    new Object[]{context, map, callBackListener});
        }
    }
    public void logout(Context context,CallBackListener callBackListener){
        if (googlePlugin != null) {
            invoke(googlePlugin, "googleLogout", new Class<?>[]{Context.class,CallBackListener.class},
                    new Object[]{context,callBackListener});
        }
    }

    public void revokeAccess(Context context,CallBackListener callBackListener){
        if (googlePlugin != null) {
            invoke(googlePlugin, "googleRevokeAccess", new Class<?>[]{Context.class,CallBackListener.class},
                    new Object[]{context,callBackListener});
        }
    }


    public void initReportEvent(Context context) {
        if (googlePlugin!=null){
            invoke(googlePlugin,"googleInitReportEvent",new Class[]{Context.class},
                    new Object[]{context});
        }
    }

    public void reportEvent(Context context,String eventName,String roleId,String roleName,String roleLevel) {
        if (googlePlugin!=null){
            invoke(googlePlugin,"googleReportEvent",new Class[]{Context.class,String.class,String.class,String.class,String.class},
                    new Object[]{context, eventName,roleId, roleName, roleLevel});
        }
    }
    public void reportEventWithPurchase(Context context,String roleId,String roleName,String roleLevel,String productName,String productId,double price) {
        if (googlePlugin!=null){
            invoke(googlePlugin,"googleReportEventWithPurchase",new Class[]{Context.class,String.class,String.class,String.class,String.class,String.class,double.class},
                    new Object[]{context, roleId, roleName, roleLevel, productName, productId,price});
        }
    }

    public void pay(Context context,Map<String,Object> map,CallBackListener callBackListener,CallBackListener getGPVerifyParamListener){
        if (googlePlugin != null){
            invoke(googlePlugin,"googlepay",new Class[]{Context.class,Map.class, CallBackListener.class,CallBackListener.class},
                new Object[]{context, map, callBackListener,getGPVerifyParamListener});
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (googlePlugin != null){
            invoke(googlePlugin,"onActivityResult",new Class[]{Context.class,int.class,int.class,Intent.class},
                    new Object[]{context,requestCode,resultCode,data});
        }

    }
}

