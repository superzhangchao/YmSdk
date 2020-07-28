package com.ym.game.sdk.callback.listener;

public interface SendVcodeListener {

    void onSuccess();
    void onFail(int status,String message);
}
