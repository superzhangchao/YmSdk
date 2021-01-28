package com.ym.game.sdk.callback;

public class CallbackMananger {
    private static LoginCallBack loginCallBack;

    private static PayCallBack payCallBack;
    private static RealNameCallBack realNameCallBack;

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

    public static RealNameCallBack getRealNameCallBack() {
        return realNameCallBack;
    }

    public static void setRealNameCallBack(RealNameCallBack realNameCallBack) {
        CallbackMananger.realNameCallBack = realNameCallBack;
    }
}
