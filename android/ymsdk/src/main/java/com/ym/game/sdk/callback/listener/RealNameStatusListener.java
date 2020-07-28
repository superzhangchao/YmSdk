package com.ym.game.sdk.callback.listener;

import com.ym.game.net.bean.ResultAccoutBean;

public interface RealNameStatusListener {
    void onSuccess(ResultAccoutBean resultAccountBean);
    void onFail(int status,String message);
}
