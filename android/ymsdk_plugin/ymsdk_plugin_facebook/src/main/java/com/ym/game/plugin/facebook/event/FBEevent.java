package com.ym.game.plugin.facebook.event;

import android.content.Context;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;

import java.math.BigDecimal;
import java.util.Currency;
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




    public void report(Context context,String eventName,String roleId,String roleName,String roleLevel){
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        Bundle bundle = new Bundle();
        bundle.putString("roleId",roleId);
        bundle.putString("roleName",roleName);
        bundle.putString("roleLevel",roleLevel);
        logger.logEvent(eventName);
    }

    public void reportWithPurchase(Context context,String roleId,String roleName,String roleLevel,String productName,String productId,double price){
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        BigDecimal bigDecimalPrice = new BigDecimal(price);
        Currency currency =  Currency.getInstance("USD");
        Bundle bundle = new Bundle();
        bundle.putString("roleId", roleId);
        bundle.putString("roleName", roleName);
        bundle.putString("roleLevel", roleLevel);
        bundle.putString("productName", productName);
        bundle.putString("productId", productId);
        logger.logPurchase(bigDecimalPrice,currency,bundle);

    }
}
