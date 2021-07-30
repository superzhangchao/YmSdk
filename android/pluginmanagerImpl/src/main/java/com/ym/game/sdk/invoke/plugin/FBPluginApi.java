package com.ym.game.sdk.invoke.plugin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.base.parse.plugin.PluginReflectApi;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;

import java.util.Map;

public class FBPluginApi extends PluginReflectApi {
    private volatile static FBPluginApi INSTANCE;
    private final Plugin fbPlugin;

    private FBPluginApi(){
         fbPlugin = PluginManager.getInstance().getPlugin("plugin_facebook");
    }

    public static FBPluginApi getInstance() {
        if (INSTANCE==null){
            synchronized (FBPluginApi.class){
                if (INSTANCE==null){
                    INSTANCE = new FBPluginApi();
                }
            }
        }
        return INSTANCE;
    }

    public void login(Context context, Map<String,Object> map, CallBackListener callBackListener){
        if (fbPlugin!=null){
            invoke(fbPlugin,"fbLogin",new Class<?>[]{Context.class, Map.class, CallBackListener.class},
                new Object[]{context, map, callBackListener});
        }
    }

    public void logout(Context context){
        if (fbPlugin!=null){
            invoke(fbPlugin,"fbLogout",new Class[]{Context.class},
                    new Object[]{context});
        }
    }

    public void reportEvent(Context context,Map<String,Object> eventMap){
        if (fbPlugin!=null){
            invoke(fbPlugin,"fbReportEvent",new Class[]{Context.class,Map.class},new Object[]{context,eventMap});
        }
    }

    public void share(Context context,Map<String,Object> shareMap,CallBackListener callBackListener){
        if (fbPlugin!=null) {
            invoke(fbPlugin, "share", new Class[]{Context.class, Map.class, CallBackListener.class}, new Object[]{context, shareMap, callBackListener});
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (fbPlugin != null){
            invoke(fbPlugin,"onActivityResult",new Class[]{Context.class,int.class,int.class,Intent.class},
                    new Object[]{context,requestCode,resultCode,data});
        }

    }

}
