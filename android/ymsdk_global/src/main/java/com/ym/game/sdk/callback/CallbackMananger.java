package com.ym.game.sdk.callback;

public class CallbackMananger {
    private static LoginCallBack loginCallBack;
    private static BindCallBack bindCallBack;

    private static PayCallBack payCallBack;
    private static ShareCallBack shareCallBack;

    public static LoginCallBack getLoginCallBack() {
        return loginCallBack;
    }

    public static void setLoginCallBack(LoginCallBack loginCallBack) {
        CallbackMananger.loginCallBack = loginCallBack;
    }

    public static BindCallBack getBindCallBack() {
        return bindCallBack;
    }

    public static void setBindCallBack(BindCallBack bindCallBack) {
        CallbackMananger.bindCallBack = bindCallBack;
    }

    public static PayCallBack getPayCallBack() {
        return payCallBack;
    }

    public static void setPayCallBack(PayCallBack payCallBack) {
        CallbackMananger.payCallBack = payCallBack;
    }

    public static void setShareCallBack(ShareCallBack shareCallBack) {
        CallbackMananger.shareCallBack = shareCallBack;
    }

    public static ShareCallBack getShareCallBack() {
        return shareCallBack;
    }
}
