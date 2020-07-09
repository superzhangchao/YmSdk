package com.ym.game.sdk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ym.game.sdk.YmConstants;

public class YmWXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, YmConstants.Wechat_APP_ID, false);

        this.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            //同意授权
             case BaseResp.ErrCode.ERR_OK:
                 SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                 // 获得code
                 String userCode = resp.code;
                 sendWeChatCode(YmConstants.LOGIN_SUCC_CODE,userCode);

                 break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                sendWeChatCode(YmConstants.LOGIN_CANCEL_CODE,"");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权
            default:
                sendWeChatCode(YmConstants.LOGIN_FAIL_CODE,"");
                break;
        }

    }

    private void sendWeChatCode(int code,String userCode) {
        Intent intent=new Intent(YmConstants.WECHATLOGINACTION);
        intent.putExtra("ERRORCODE",code);
        intent.putExtra("USERCODE",userCode);
        this.sendBroadcast(intent);
        this.finish();
    }
}
