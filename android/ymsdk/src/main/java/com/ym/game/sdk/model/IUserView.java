package com.ym.game.sdk.model;


import android.content.Context;

import com.ym.game.sdk.bean.AccountBean;

public interface IUserView {
    Context getContext();
    void showLoading();
    AccountBean getAccountData();
    void dismissLoading();
//    String getPhone();
//    String getPassword();
//    String getVcode();
//    String getNickName();
////    String clearPhone();
////    String clearPassword();
////    String clearVcode();
    void closeActivity();

    void cancelLogin();
//    void showToast(String msg);
//    void loginSucess(boolean isRegister);
//    void setHistoryUser(Set<String> userList);
//    void sendCodeResult(boolean result, String msg);
//    void autoLoginFail();
}
