package com.ym.game.sdk.model;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.CheckBindListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.LoginStatusListener;
import com.ym.game.sdk.callback.listener.RealNameStatusListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;

public interface IUserModel {
    AccountBean getLoginAccountInfo();
//    void login(Context context, String phone, String pwd, User.UserListener userListener);
//    void register(Context context, String phone, String pwd, String vcode, User.UserListener userListener);
//    void logout(Context context, User.UserListener userListener);
//    void updateNickName(Context context, String nickName, User.UserListener userListener);
//    void forgetPass(Context context, String phone, String pwd, String vcode, User.UserListener userListener);
//    void fastLogin(Context context, User.UserListener userListener);

    //    void getThirdUserInfo(Context context, String url, User.UserListener userListener);
//    void parseThirdUserInfo(Context context, JSONObject response, User.UserListener userListener);
//    void saveUserPhone(Context context, String phone);
//    Set<String> getAllPhone(Context context);
//    void deleteUserPhone(Context context, String phone);
////    void saveUserInfo(Context context);
////    User getUserInfo(Context context);
////    void setGuestFlag(Context context,boolean isGuest);
////    boolean getGuestFLag(Context context);
    void saveAccountInfo(Context context,String uid, String token);
    String getToken(Context context);
    String getUid(Context context);
    void getVerifyData(Context context,GetVerifyDataListener getVerifyDataListener);

    void sendVcode(Context context, String phone,String ts,String accessToken, SendVcodeListener sendVcodeListener);

    void checkBind(Context context, String phone, String ts, String accessToken, CheckBindListener checkBindListener);

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
