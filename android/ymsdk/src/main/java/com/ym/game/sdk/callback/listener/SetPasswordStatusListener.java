package com.ym.game.sdk.callback.listener;

import com.ym.game.net.bean.ResultAccoutBean;

public interface SetPasswordStatusListener {
    void onSuccess();
    void onFail(int status,String message);
}
