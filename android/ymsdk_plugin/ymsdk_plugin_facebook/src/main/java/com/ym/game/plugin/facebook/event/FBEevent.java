package com.ym.game.plugin.facebook.event;

import android.content.Context;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;

import java.util.Map;

public class FBEevent {
    private volatile static FBEevent INSTANCE;
    private CallbackManager callbackManager;
    private Context mContext;
    private CallBackListener mloginCallbackListener;

    private FBEevent(){}

    public static FBEevent getInstance(){
        if (INSTANCE==null){
            synchronized (FBEevent.class){
                if (INSTANCE==null){
                    INSTANCE = new FBEevent();
                }
            }
        }
        return INSTANCE;
    }


    public void reportManuallyEvents(Context context,Map<String,Object> params){
        String eventName = (String) params.get("eventName");
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);
        Bundle bundle = new Bundle();
        bundle.putString("dataKey","2");
        logger.logEvent(eventName,bundle);
    }

}
