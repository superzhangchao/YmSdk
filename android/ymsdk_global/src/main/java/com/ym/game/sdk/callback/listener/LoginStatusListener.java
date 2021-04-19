package com.ym.game.sdk.callback.listener;

import com.ym.game.net.bean.ResultAccoutBean;

public interface LoginStatusListener {
    void onSuccess(ResultAccoutBean resultAccountBean);
    void onCancel();
    void onFail(int status,String message);
}
