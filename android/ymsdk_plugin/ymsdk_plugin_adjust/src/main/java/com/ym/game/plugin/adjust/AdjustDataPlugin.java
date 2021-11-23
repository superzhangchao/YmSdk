package com.ym.game.plugin.adjust;

import android.app.Application;
import android.content.Context;

import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;
import com.ym.game.plugin.adjust.event.AdjustEvent;

public class AdjustDataPlugin extends Plugin {
    private static final String TAG = "AdjustPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    public void initAdjust(Context context,boolean debug){
        AdjustEvent.getInstance().init((Application) context, debug);
    }

    public void trackEvent(String eventToken){
        AdjustEvent.getInstance().trackEvent(eventToken);
    }

    public void trackEventWithRevenue(String eventToken,double revenue,String orderId,String currency){
        AdjustEvent.getInstance().trackEventWithRevenue(eventToken,revenue,orderId,currency);
    }
}
