package com.ym.game.sdk.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;
import com.ym.game.sdk.model.IUserModel;
import com.ym.game.sdk.model.IUserView;
import com.ym.game.sdk.model.UserModel;
import com.ym.game.sdk.ui.activity.YmUserActivity;

public class UserPresenter {

    public static void showLoginActiviy(Activity activity){
        IUserModel userModel = UserModel.getInstance();
//        User user = userModel.getUserInfo(activity);
        String token = userModel.getToken(activity);
        if (TextUtils.isEmpty(token)){
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type",YmUserActivity.LOGIN);
            activity.startActivity(intent);
        }else{
            Intent intent = new Intent(activity, YmUserActivity.class);
            intent.putExtra("type",YmUserActivity.AUTOLOGIN);
            activity.startActivity(intent);
        }
    }

    public static void getVerifyData(IUserView userView){



    }

    public static void sendVcode(final IUserView userView, final String phone) {
        UserModel.getInstance().getVerifyData(new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts,String accessToken) {
                startSendVcode(userView,phone,ts,accessToken);
            }

            @Override
            public void onFail(String message) {

            }
        });


    }

    private static void startSendVcode(IUserView userView, String phone, String ts, String accessToken){
        UserModel.getInstance().sendVcode(userView.getContext(),phone,ts,accessToken, new SendVcodeListener() {


            @Override
            public void onSuccess() {
                //发送验证码成功
            }

            @Override
            public void onFail(String message) {

            }
        });
    }
}
