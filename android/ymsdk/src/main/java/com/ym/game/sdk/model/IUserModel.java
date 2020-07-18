package com.ym.game.sdk.model;


import android.content.Context;

import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;

public interface IUserModel {
//    User getUser();
//    void login(Context context, String phone, String pwd, User.UserListener userListener);
//    void register(Context context, String phone, String pwd, String vcode, User.UserListener userListener);
//    void logout(Context context, User.UserListener userListener);
//    void updateNickName(Context context, String nickName, User.UserListener userListener);
//    void forgetPass(Context context, String phone, String pwd, String vcode, User.UserListener userListener);
//    void fastLogin(Context context, User.UserListener userListener);
//    void bindPhone(Context context, String phone, String pwd, String vcode, User.UserListener userListener);
//    void getThirdUserInfo(Context context, String url, User.UserListener userListener);
//    void parseThirdUserInfo(Context context, JSONObject response, User.UserListener userListener);
//    void saveUserPhone(Context context, String phone);
//    Set<String> getAllPhone(Context context);
//    void deleteUserPhone(Context context, String phone);
////    void saveUserInfo(Context context);
////    User getUserInfo(Context context);
////    void setGuestFlag(Context context,boolean isGuest);
////    boolean getGuestFLag(Context context);
    void saveToken(Context context, String token);
    String getToken(Context context);
    void sendVcode(Context context, String phone,String ts,String accessToken, SendVcodeListener sendVcodeListener);
    void getVerifyData(GetVerifyDataListener getVerifyDataListener);
//    void autoLogin(Context context, User.UserListener userListener);
//    void saveNickName(Context context, String nickName);
//    String getNickName(Context context);
}
