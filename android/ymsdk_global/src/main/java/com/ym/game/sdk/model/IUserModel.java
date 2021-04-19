package com.ym.game.sdk.model;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.BindStatusListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;

public interface IUserModel {
    AccountBean getLoginAccountInfo();

    void saveAccountInfo(Context context, AccountBean saveAccountInfoBean);



    String getLastUid(Context context);

    String getLastToken(Context context);






    void getVerifyData(Context context, GetVerifyDataListener getVerifyDataListener);

    void loginByType(Activity activity, AccountBean accountBean, LoginStatusListener loginStatusListener);

    void onActivityResult(Context context, int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults);

    void autoLogin(Context context,AccountBean accountBean, LoginStatusListener loginStatusListener);

    void bindByType(Activity context, AccountBean accountBean, BindStatusListener bindStatusListener);

    void logout(Activity activity);




}
