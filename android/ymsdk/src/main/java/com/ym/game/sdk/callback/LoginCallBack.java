package com.ym.game.sdk.callback;


import com.ym.game.sdk.base.interfaces.CallBackListener;

public interface LoginCallBack extends CallBackListener {
    @Override
    void onSuccess(Object o);

    void onCancel();
}
