package com.ym.game.sdk.model;


import android.app.Activity;
import android.content.Context;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.PayCallBack;

public interface IPurchaseModel {
//    void initNowPay(Activity activity);
//    void createOrderId(Context context, String channelId, Purchase purchase);
    void initPay(Activity activity);
    void startPay(Context context, PurchaseBean purchaseBean, PurchaseModel.PurchaseStatusListener listener);

    void destroy(Activity activity);
//    void startBankPay(Context context, Purchase purchase, PurchaseModel.PurchaseStatusListener listener);
//    void startWeChatPay(Context context, Purchase purchase, PurchaseModel.PurchaseStatusListener listener);
//    void getCoins(Context context, PurchaseModel.PurchaseCoinsListener listener);
//    void coinsPay(Context context, Purchase purchase, PurchaseModel.PurchaseStatusListener listener);
}
