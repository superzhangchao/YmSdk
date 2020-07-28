package com.ym.game.sdk.model;

import android.content.Context;

import com.ym.game.sdk.bean.PurchaseBean;


public interface IPurchaseView {
    Context getContext();
    void showLoading();
    void dismissLoading();
    PurchaseBean getPurchaseData();
//    Purchase getPurchase();
    void closeActivity();

    void cancelPay();
//    void showToast(String msg);
//    int getCoins();
//    void setCoins(int coins);
}
