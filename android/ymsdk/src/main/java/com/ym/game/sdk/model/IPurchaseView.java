package com.ym.game.sdk.model;

import android.content.Context;

import com.ym.game.sdk.bean.PurchaseBean;


public interface IPurchaseView {
    Context getContext();
    void showLoading();
    void dismissLoading();
    PurchaseBean getPurchaseData();
    void closeActivity();

    void cancelPay();

}
