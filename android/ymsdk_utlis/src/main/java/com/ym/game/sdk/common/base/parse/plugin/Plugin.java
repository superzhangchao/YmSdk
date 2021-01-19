package com.ym.game.sdk.common.base.parse.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.ym.game.sdk.common.base.interfaces.LifeCycleInterface;


/**
 *
 * 基础功能插件基类
 */

public class Plugin implements LifeCycleInterface {

    private static final String TAG = "Plugin";

    public PluginBeanList.PluginBean pluginBean;

    private boolean hasInited;

    protected synchronized void initPlugin() {
        if (hasInited) {
            return;
        }
        hasInited = true;
    }

    @Override
    public String toString() {
        return "Plugin{" + "pluginMessage=" + pluginBean + ", hasInited=" + hasInited + '}';
    }

    /****************************************生命周期方法*********************************************/


    public void onCreate(Context context, Bundle savedInstanceState) {
    }

    public void onStart(Context context) {
    }



    public void onResume(Context context) {
    }

    public void onPause(Context context) {
    }

    public void onStop(Context context) {
    }

    public void onRestart(Context context) {
    }

    public void onDestroy(Context context) {
    }

    public void onNewIntent(Context context, Intent intent){

    }

    @Override
    public void onConfigurationChanged(Context context, Configuration newConfig) {

    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
    }

    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
    }
}
