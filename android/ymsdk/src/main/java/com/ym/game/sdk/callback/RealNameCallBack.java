package com.ym.game.sdk.callback;


import com.ym.game.sdk.common.base.interfaces.CallBackListener;

public interface RealNameCallBack extends CallBackListener {
    @Override
    void onSuccess(Object o);

    void onCancel();
}
