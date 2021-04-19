package com.ym.game.sdk.model;


import android.content.Context;

import com.ym.game.sdk.bean.AccountBean;

public interface IUserView {
    Context getContext();
    void showLoading();
    AccountBean getAccountData();
    void dismissLoading();

    void closeActivity();

    void closeCurrnetFragment();
    void cancelLogin();

}
