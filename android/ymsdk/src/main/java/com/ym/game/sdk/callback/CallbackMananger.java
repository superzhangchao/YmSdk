package com.ym.game.sdk.callback;

public class CallbackMananger {
    private static LoginCallBack loginCallBack;

    private static PayCallBack payCallBack;

    public static LoginCallBack getLoginCallBack() {
        return loginCallBack;
    }

    public static void setLoginCallBack(LoginCallBack loginCallBack) {
        CallbackMananger.loginCallBack = loginCallBack;
    }

    public static PayCallBack getPayCallBack() {
        return payCallBack;
    }

    public static void setPayCallBack(PayCallBack payCallBack) {
        CallbackMananger.payCallBack = payCallBack;
    }

}
