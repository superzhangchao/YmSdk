package com.ym.game.sdk.callback.listener;

public interface CheckBindListener {
    void getBindStatus(int status,String message);
    void onFail(int status,String message);
}
