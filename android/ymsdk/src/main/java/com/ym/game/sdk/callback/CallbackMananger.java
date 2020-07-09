package com.ym.game.sdk.callback;

public class CallbackMananger {
    private static LoginCallBack loginCallBack;

    public static LoginCallBack getLoginCallBack() {
        return loginCallBack;
    }

    public static void setLoginCallBack(LoginCallBack loginCallBack) {
        CallbackMananger.loginCallBack = loginCallBack;
    }

}
