package com.ym.game.sdk.model;

import android.content.Context;

import com.ym.game.sdk.bean.PurchaseBean;


public interface IPurchaseView {
    Context getContext();
    void showLoading(String msg);
    void dismissLoading();
    PurchaseBean getPurchaseDate();
//    Purchase getPurchase();
    void closeActivity();
//    void showToast(String msg);
//    int getCoins();
//    void setCoins(int coins);
}
