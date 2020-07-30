package com.ym.game.sdk.model;


import android.app.Activity;
import android.content.Context;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.PayCallBack;

public interface IPurchaseModel {

    void initPay(Activity activity);
    void startPay(Context context, PurchaseBean purchaseBean, PurchaseModel.PurchaseStatusListener listener);

    void destroy(Activity activity);

    boolean getWxPayStatus();

    void resetWxPay();

}
