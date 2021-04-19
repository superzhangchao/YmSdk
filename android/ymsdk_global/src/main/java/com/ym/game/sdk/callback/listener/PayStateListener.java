package com.ym.game.sdk.callback.listener;

import com.ym.game.net.bean.ResultAccoutBean;

public interface PayStateListener {
    void onSuccess();
    void onCancel();
    void onFail(int status,String message);
}

