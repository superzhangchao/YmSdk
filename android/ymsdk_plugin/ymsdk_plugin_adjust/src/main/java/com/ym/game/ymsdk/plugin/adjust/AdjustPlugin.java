package com.ym.game.ymsdk.plugin.adjust;

import android.app.Application;
import android.content.Context;

import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;
import com.ym.game.ymsdk.plugin.adjust.analytics.AdjustAnalytics;

import java.util.Map;

public class AdjustPlugin extends Plugin {
    private static final String TAG = "AdjustPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    public void init(Application context, Map<String,String>initMap){
        AdjustAnalytics.getInstance().init(context,initMap);
    }

}
