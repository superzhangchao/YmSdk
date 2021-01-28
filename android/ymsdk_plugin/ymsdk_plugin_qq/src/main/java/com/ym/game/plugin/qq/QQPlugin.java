package com.ym.game.plugin.qq;

import android.content.Context;
import android.content.Intent;

import com.tencent.tauth.Tencent;
import com.ym.game.plugin.qq.login.QQLogin;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.utils.LogUtils;

import java.util.Map;

public class QQPlugin extends Plugin {
    private  String TAG = "QQPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    public void qqLogin(Context context, Map<String,Object>loginMap, CallBackListener callBackListener){
        QQLogin.getInstance().login(context,loginMap,callBackListener);
    }

    public void qqLogout(Context context){
        QQLogin.getInstance().logout(context);
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        QQLogin.getInstance().onActivityResult(context,requestCode,resultCode,data);


    }
}
