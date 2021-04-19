package com.ym.game.sdk.model;


import android.app.Activity;
import android.content.Context;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.PayStateListener;

import java.util.Map;


public interface IPurchaseModel {

    void initPay(Activity activity);


    void destroy(Activity activity);


    void getVerifyData(Context context, GetVerifyDataListener getVerifyDataListener);

    void startPay(Context context, PurchaseBean purchaseBean, PayStateListener payStateListener);




}
