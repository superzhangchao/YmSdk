package com.ym.game.sdk.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.AliPayResult;
import com.ym.game.net.bean.ResultOrderBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.sdk.YmConstants;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.base.interfaces.LifeCycleInterface;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.utils.LogUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.game.sdk.invoke.plugin.AlipayPluginApi;
import com.ym.game.sdk.invoke.plugin.WechatPluginApi;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseModel implements IPurchaseModel , LifeCycleInterface {

    @SuppressLint("StaticFieldLeak")
    private static PurchaseModel instance;
    private PurchaseStatusListener mPurchaseStatusListener;
    private PurchaseBean mPurchaseDate;
    private static final int SENDTIME = 1;
    private static final int SENDTIMEERROR = 2;
    private static final int SENDTIMENETERROR = 3;
    private static final int GETTOKENSUCCESS = 4;
    private static final int GETTOKENFAIL = 5;
    private static final int GETTOKENNETFAIL = 6;
    private static final int CHECKORDERSUCCESS = 7;
    private static final int CHECKORDERFAIL = 8;
    private static final int CHECKORDERNETFAIL = 9;



    private static final String PAYTYPEALI = "alipay";
    private static final String PAYTYPEWEIXIN = "wxpay";
    private String currentTs;
    private Context mContext;


    private String platformOrderId;


    public static PurchaseModel getInstance(){
        if (instance == null){
            instance = new PurchaseModel();
        }

        return instance;
    }

    private PurchaseModel(){

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Map<String, Object> errorData;
            switch (msg.what) {
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    currentTs = timeInfo.optString("ts");
                    getToken();
                    break;
                case GETTOKENSUCCESS:
                    String accessToken = (String) msg.obj;
                    mPurchaseDate.setAccessToken(accessToken);
                    checkorder(mPurchaseDate);
                    break;
                case CHECKORDERSUCCESS:
                    ResultOrderBean.DataBean dataBean = (ResultOrderBean.DataBean) msg.obj;
                    platformOrderId = dataBean.getPf_order_no();

                    String type = dataBean.getType();
                    if (TextUtils.equals(PAYTYPEALI,type)){
                        startAliPay(dataBean);
                    }else if(TextUtils.equals(PAYTYPEWEIXIN,type)){
                        startWXPay(dataBean);
                    }
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    mPurchaseStatusListener.onFail(ErrorCode.NET_DATA_NULL,mContext.getString(ResourseIdUtils.getStringId("ym_text_netdata_null")));
                    break;
                case SENDTIMENETERROR:
                    mPurchaseStatusListener.onFail(ErrorCode.NET_ERROR,mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case GETTOKENFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mPurchaseStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case GETTOKENNETFAIL:
                    mPurchaseStatusListener.onFail(ErrorCode.NET_ERROR,mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                case CHECKORDERFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mPurchaseStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case CHECKORDERNETFAIL:
                    mPurchaseStatusListener.onFail(ErrorCode.NET_ERROR,mContext.getString(ResourseIdUtils.getStringId("ym_text_neterror")));
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void initPay(Activity activity) {

    }

    @Override
    public void startPay(Context context, PurchaseBean purchaseDate, PurchaseStatusListener purchaseStatusListener) {
        mPurchaseStatusListener = purchaseStatusListener;
        mPurchaseDate = purchaseDate;
        mContext = context;
        getTime();
    }

    @Override
    public void destroy(Activity activity) {

    }



    private void startAliPay(final ResultOrderBean.DataBean dataBean) {

        final String orderInfo = dataBean.getAlipay().getInfo();   // 订单信息
        Map<String,Object> payInfo =new HashMap<>();
        payInfo.put("payment_info",orderInfo);
        AlipayPluginApi.getInstance().pay(mContext, payInfo, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                LogUtils.debug_d("ymsdkzc onSuccess");
                mPurchaseStatusListener.onSuccess(platformOrderId);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.debug_d("ymsdkzc onFailure");
                if (code ==ErrorCode.FAILURE){
                    mPurchaseStatusListener.onFail(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_text_payali_fail")));

                }else if (code ==ErrorCode.CANCEL){
                    mPurchaseStatusListener.onCancel();
                }
            }
        });
    }

    private void startWXPay(ResultOrderBean.DataBean dataBean){
        Map<String,Object> payInfo =new HashMap<>();
        payInfo.put("appId",dataBean.getWxpay().getWxAppId());
        payInfo.put("partnerId",dataBean.getWxpay().getPartnerId());
        payInfo.put("prepayId",dataBean.getWxpay().getPrepayId());
        payInfo.put("packageValue",dataBean.getWxpay().getPackageValue());
        payInfo.put("nonceStr",dataBean.getWxpay().getNonceStr());
        payInfo.put("timeStamp",dataBean.getWxpay().getTimeStamp());
        payInfo.put("sign",dataBean.getWxpay().getWxSign());

        WechatPluginApi.getInstance().pay(mContext, payInfo, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                mPurchaseStatusListener.onSuccess(platformOrderId);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code ==ErrorCode.FAILURE){
                    mPurchaseStatusListener.onFail(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_text_paywx_fail")));

                }else if (code ==ErrorCode.CANCEL){
                    mPurchaseStatusListener.onCancel();
                }
            }
        });

    }



    private  void getTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localTs = System.currentTimeMillis()+"";
                Call<String> token = YmApi.getInstance().getTime(localTs);
                token.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String body = response.body();
                        JSONObject timeInfo = new JSONObject();
                        Message message = new Message();
                        try {
                            timeInfo.put("ts",body);
                            message.obj= timeInfo;
                            message.what = SENDTIME;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            message.what = SENDTIMEERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
                        message.what = SENDTIMENETERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getToken() {

        Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.FROMKEY, YmConstants.FROM);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<TokenBean> token = YmApi.getInstance().getTokenInfo(YmConstants.APPID, YmConstants.FROM, currentTs, sign);
                token.enqueue(new Callback<TokenBean>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenBean> call, @NonNull Response<TokenBean> response) {
                        TokenBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {
                            message.obj = body.getData().getAccessToken();
                            message.what = GETTOKENSUCCESS;

                        }else {
                            Map<String,Object> errorData = new HashMap<>();
                            errorData.put("code",errorCode);
                            errorData.put("message",body.getMessage());
                            message.obj = errorData;
                            message.what = GETTOKENFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();

                        message.what = GETTOKENNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    private void checkorder(PurchaseBean mPurchaseDate) {

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.VERSIONKEY, YmConstants.VERSION);
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.CPID, YmConstants.CPIDKEY);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.USERID, mPurchaseDate.getUserId());
        param.put(YmConstants.ACCESSTOKEN, mPurchaseDate.getAccessToken());
        param.put(YmConstants.PAYTYPE, mPurchaseDate.getPayType());
        param.put(YmConstants.SERVERID, mPurchaseDate.getServerId());
        param.put(YmConstants.ROLEID, mPurchaseDate.getRoleId());
        param.put(YmConstants.ROLENAME, mPurchaseDate.getRoleName());
        param.put(YmConstants.ROLELEVEL, mPurchaseDate.getRoleLevel());

        param.put(YmConstants.PRODUCTID, mPurchaseDate.getProductId());
        param.put(YmConstants.PRODUCTNAME, mPurchaseDate.getProductName());
        param.put(YmConstants.PRODUCTDESC, mPurchaseDate.getProductDesc());
        param.put(YmConstants.PRODUCTPRICE, mPurchaseDate.getProductPrice());
        param.put(YmConstants.ORDERID, mPurchaseDate.getOrderId());


        param.put(YmConstants.GAMESIGN, mPurchaseDate.getGameSign());

        param.put(YmConstants.EXT, mPurchaseDate.getExt());

        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultOrderBean> token = YmApi.getInstance().checkorder(param);
                token.enqueue(new Callback<ResultOrderBean>() {
                    @Override
                    public void onResponse(Call<ResultOrderBean> call, Response<ResultOrderBean> response) {
                        ResultOrderBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {

                            message.obj = body.getData();
                            message.what = CHECKORDERSUCCESS;

                        }else {
                            Map<String,Object> errorData = new HashMap<>();
                            errorData.put("code",errorCode);
                            errorData.put("message",body.getMessage());
                            message.obj = errorData;
                            message.what = CHECKORDERFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultOrderBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = CHECKORDERNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onCreate(Context context, Bundle savedInstanceState) {

    }

    @Override
    public void onResume(Context context) {
        PluginManager.getInstance().onResume(context);
    }

    @Override
    public void onStart(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onStop(Context context) {

    }

    @Override
    public void onRestart(Context context) {

    }

    @Override
    public void onDestroy(Context context) {

    }

    @Override
    public void onNewIntent(Context context, Intent intent) {

    }

    @Override
    public void onConfigurationChanged(Context context, Configuration newConfig) {

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }


    public interface PurchaseStatusListener{
        void onSuccess(String platformOrderId);
        void onCancel();
        void onFail(int errorCode,String msg);
    }
}
