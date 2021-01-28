package com.ym.game.sdk.model;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.CheckBindListener;
import com.ym.game.sdk.callback.listener.CheckRegisterListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.callback.listener.RealNameStatusListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;

public interface IUserModel {
    AccountBean getLoginAccountInfo();

    void saveAccountInfo(Context context);
    String getToken(Context context);
    String getUid(Context context);
    void getVerifyData(Context context,GetVerifyDataListener getVerifyDataListener);

    void sendVcode(Context context, String phone,String ts,String accessToken, SendVcodeListener sendVcodeListener);

    void checkRegister(Context context, String phone, String ts, String accessToken, CheckRegisterListener checkRegisterListener);



    void startRegister(Activity activity, AccountBean accountBean, LoginStatusListener loginStatusListener);

    void loginByType(Activity activity, AccountBean accountBean, LoginStatusListener loginStatusListener);

    void onActivityResult(Context context, int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults);

    void autoLogin(Context context,AccountBean accountBean, LoginStatusListener loginStatusListener);

    void bindAccount(Activity context, AccountBean accountBean, LoginStatusListener loginStatusListener);

    void logout(Activity activity);

    void realName(Activity context, AccountBean accountBean, RealNameStatusListener realNameStatusListener);

    void saveXieyiStatud(Context context,boolean status);

    boolean getXieyiStatus(Context context);

    int getRealNameStatus();


}
