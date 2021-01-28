package com.ym.game.sdk.callback.listener;

public interface CheckRegisterListener {
    void getRegisterStatus(int status,String message);
    void onFail(int status,String message);
}
