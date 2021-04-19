package com.ym.game.sdk.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.ResultOrderBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.sdk.YmConstants;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;

import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.game.sdk.invoke.plugin.AlipayPluginApi;
import com.ym.game.sdk.invoke.plugin.WechatPluginApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseModel implements IPurchaseModel{

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

    private boolean mIsPaywx = false;

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
            String messageName = "ym_text_neterror";
            int netError = ErrorCode.NET_ERROR;
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
                case GETTOKENNETFAIL:
                case CHECKORDERNETFAIL:
                    netError = (int) msg.obj;
                    if (netError ==ErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mPurchaseStatusListener.onFail(netError,mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case GETTOKENFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mPurchaseStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case CHECKORDERFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mPurchaseStatusListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                default:
                    break;
            }
        }
    };

//    @SuppressLint("HandlerLeak")
//    private Handler mAliHandler = new Handler() {
//        @SuppressWarnings("unused")
//        public void handleMessage(Message msg) {
//            if (msg.what == SDK_ALIPAY_FLAG) {
//                @SuppressWarnings("unchecked")
//                AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
//                /**
//                 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                 */
//                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                String resultStatus = payResult.getResultStatus();
//                if (TextUtils.equals(resultStatus, "9000")) {
//                    mPurchaseStatusListener.onSuccess(platformOrderId,PAYTYPEALI);
//                } else if (TextUtils.equals(resultStatus, "6001")) {
//                    //不需要处理
//                    mPurchaseStatusListener.onCancel();
//                } else {
//                    mPurchaseStatusListener.onFail(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_text_payali_fail")));
//                }
//            }
//        }
//    };

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

//        final String orderInfo = dataBean.getAlipay().getInfo();   // 订单信息
//        Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask((Activity) mContext);
//                String version = alipay.getVersion();
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Message msg = new Message();
//                msg.what = SDK_ALIPAY_FLAG;
//                msg.obj = result;
//                mAliHandler.sendMessage(msg);
//            }
//        };
//        // 必须异步调用
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
        Map<String,Object> alipayMap = new HashMap<>();
        alipayMap.put("orderInfo", dataBean.getAlipay().getInfo());
        CallBackListener alipaycallBackListener = new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                mPurchaseStatusListener.onSuccess(platformOrderId,PAYTYPEALI);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code ==ErrorCode.CANCEL){
                    mPurchaseStatusListener.onCancel();
                }else if (code == ErrorCode.FAILURE){
                    mPurchaseStatusListener.onFail(ErrorCode.FAILURE,msg);
                }
            }
        };
        AlipayPluginApi.getInstance().pay(mContext,alipayMap,alipaycallBackListener);
    }

    private void startWXPay(ResultOrderBean.DataBean dataBean){

        Map<String,Object> payMap = new HashMap<>();
        payMap.put("appId",dataBean.getWxpay().getWxAppId());
        payMap.put("partnerId",dataBean.getWxpay().getPartnerId());
        payMap.put("prepayId",dataBean.getWxpay().getPrepayId());
        payMap.put("packageValue",dataBean.getWxpay().getPackageValue());
        payMap.put("nonceStr",dataBean.getWxpay().getNonceStr());
        payMap.put("timeStamp",dataBean.getWxpay().getTimeStamp());
        payMap.put("sign",dataBean.getWxpay().getWxSign());
        CallBackListener wechatcallBackListener = new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                mPurchaseStatusListener.onSuccess(platformOrderId,PAYTYPEWEIXIN);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code ==ErrorCode.CANCEL){
                    mPurchaseStatusListener.onCancel();
                }else if (code == ErrorCode.FAILURE){
                    mPurchaseStatusListener.onFail(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_text_paywx_fail"))+"fsdds");
                }
            }
        };
        WechatPluginApi.getInstance().pay(mContext,payMap,wechatcallBackListener);
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
                        Message message = new Message();
                        if (response.isSuccessful()) {
                            String body = response.body();
                            JSONObject timeInfo = new JSONObject();
                            try {
                                timeInfo.put("ts",body);
                                message.obj= timeInfo;
                                message.what = SENDTIME;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                message.what = SENDTIMEERROR;
                            }
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = SENDTIMENETERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
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
                        Message message = new Message();
                        if (response.isSuccessful()) {
                            TokenBean body = response.body();
                            int errorCode = body.getCode();
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
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETTOKENNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
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
                        Message message = new Message();
                        if (response.isSuccessful()) {
                            ResultOrderBean body = response.body();
                            int errorCode = body.getCode();
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
                        }else {
                            int errorCode = ErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = CHECKORDERNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultOrderBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = ErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = CHECKORDERNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    public interface PurchaseStatusListener{
        void onSuccess(String platformOrderId,String payType);
        void onCancel();
        void onFail(int errorCode,String msg);
    }
}
