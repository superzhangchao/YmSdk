package com.ym.game.sdk.callback.listener;

public interface GetVerifyDataListener {
    void onSuccess(String ts, String accessToken);
    void onFail(String message);
}
