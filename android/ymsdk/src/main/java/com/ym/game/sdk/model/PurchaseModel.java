package com.ym.game.sdk.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.ym.game.sdk.YmSdkApi;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;
import com.ym.game.utils.YmSignUtils;

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


    private static final int SDK_ALIPAY_FLAG = 1;
    private static final String PAYTYPEALI = "alipay";
    private static final String PAYTYPEWEIXIN = "wxpay";
    private String currentTs;
    private Context mContext;
    private IWXAPI msgApi;
    private BroadcastReceiver wxPayBroadcastReceiver;
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
            switch (msg.what) {
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    currentTs = timeInfo.optString("ts");
                    getToken();
                    break;
                case GETTOKENSUCCESS:
                    //TODO:
                    String accessToken = (String) msg.obj;
                    mPurchaseDate.setAccessToken(accessToken);
                    checkorder(mPurchaseDate);
                    break;
                case CHECKORDERSUCCESS:
                    ResultOrderBean.DataBean dataBean = (ResultOrderBean.DataBean) msg.obj;
                    //TODO:赋值平台订单
                    platformOrderId = "11544662";

                    String type = dataBean.getType();
                    if (TextUtils.equals(PAYTYPEALI,type)){
                        startAliPay(dataBean);
                    }else if(TextUtils.equals(PAYTYPEWEIXIN,type)){
                        startWXPay(dataBean);
                    }
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    //TODO:请求ts失败
                    break;
                case SENDTIMENETERROR:
                    //TODO:请求ts网络失败
                    break;
                case GETTOKENFAIL:
                    //TODO:请求token失败
                    break;
                case GETTOKENNETFAIL:
                    //TODO:
                    //TODO:网络请求token失败
                    break;
                case CHECKORDERFAIL:
                    //TODO:检查订单校验失败
                    break;
                case CHECKORDERNETFAIL:
                    //TODO:检查订单网络失败
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mAliHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            if (msg.what == SDK_ALIPAY_FLAG) {
                @SuppressWarnings("unchecked")
                AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                /**
                 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                if (TextUtils.equals(resultStatus, "9000")) {
                    mPurchaseStatusListener.onSuccess(platformOrderId);
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    //不需要处理
                    mPurchaseStatusListener.onFail(6001,"alipay oncancel");
                } else {
                    mPurchaseStatusListener.onFail(9001,"alipay onfail");
                }
            }
        }
    };

    @Override
    public void initPay(Activity activity) {
        wxPayBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int paycode = intent.getIntExtra("PAYCODE", -1);
                switch (paycode) {
                    case YmConstants.WXPAY_RESULT_SUCC_CODE:
                        mPurchaseStatusListener.onSuccess(platformOrderId);
                        break;
                    case YmConstants.WXPAY_RESULT_CANCEL_CODE:
                        mPurchaseStatusListener.onFail(6001,"wxpay oncancel");
                        break;
                    case YmConstants.WXPAY_RESULT_FAIL_CODE:
                        mPurchaseStatusListener.onFail(9001,"wxpay onfail");
                        break;
                    default:
                        break;
                }
            }
        };
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
        try {
            if (null != wxPayBroadcastReceiver) {
                activity.unregisterReceiver(wxPayBroadcastReceiver);
            }
        } catch (IllegalArgumentException e) {
//            Log.e("ysqy",  e.toString());
        }
    }

    private void startAliPay(final ResultOrderBean.DataBean dataBean) {

        final String orderInfo = dataBean.getAlipay().getInfo();   // 订单信息
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) mContext);
                String version = alipay.getVersion();
                Map<String, String> result = alipay.payV2(orderInfo, true);
//                result.put("platformOrderId",resultOrderBean)

                Message msg = new Message();
                msg.what = SDK_ALIPAY_FLAG;
                msg.obj = result;
                mAliHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void startWXPay(ResultOrderBean.DataBean dataBean){
        msgApi = WXAPIFactory.createWXAPI(mContext, YmConstants.WX_APP_ID, true);
        msgApi.registerApp(YmConstants.WX_APP_ID);
        IntentFilter filter = new IntentFilter(YmConstants.WXPAYACTION);
        mContext.registerReceiver(wxPayBroadcastReceiver, filter);
        PayReq request = new PayReq();
        request.appId = dataBean.getWxpay().getAppId();
        request.partnerId = dataBean.getWxpay().getPartnerId();
        request.prepayId = dataBean.getWxpay().getPrepayId();
        request.packageValue = dataBean.getWxpay().getPackageValue();
        request.nonceStr = dataBean.getWxpay().getNonceStr();
        request.timeStamp = dataBean.getWxpay().getTimeStamp();
        request.sign = dataBean.getWxpay().getSign();
        int wxSdkVersion = msgApi.getWXAppSupportAPI();
        if (wxSdkVersion >= Build.OFFLINE_PAY_SDK_INT) {
            msgApi.sendReq(request);
        } else {
            ToastUtils.showToast(mContext, mContext.getString(ResourseIdUtils.getStringId("ym_no_install_wechat")));

        }
    }



    private  void getTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<String> token = YmApi.getInstance().getTime();
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
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.ACCESSTOKEN, mPurchaseDate.getAccessToken());
        param.put(YmConstants.CPID, currentTs);
        param.put(YmConstants.PAYTYPE, mPurchaseDate.getPayType());
        param.put(YmConstants.PRODUCTDESC, mPurchaseDate.getProductDesc());
        param.put(YmConstants.PRODUCTID, mPurchaseDate.getProductId());
        param.put(YmConstants.PRODUCTNAME, mPurchaseDate.getProductName());
        param.put(YmConstants.PRODUCTPRICE, mPurchaseDate.getProductPrice());
        param.put(YmConstants.ORDERID, mPurchaseDate.getOrderId());
        param.put(YmConstants.SERVERID, mPurchaseDate.getServerId());
        param.put(YmConstants.ROLEID, mPurchaseDate.getRoleId());
        param.put(YmConstants.ROLENAME, mPurchaseDate.getRoleName());
        param.put(YmConstants.ROLELEVEL, mPurchaseDate.getRoleLevel());
        param.put(YmConstants.USERID, mPurchaseDate.getUserId());
        param.put(YmConstants.GAMESIGN, mPurchaseDate.getGameSign());
        param.put(YmConstants.VERSIONKEY, YmConstants.VERSION);
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

                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultOrderBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = CHECKORDERFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    public interface PurchaseStatusListener{
        void onSuccess(String platformOrderId);
        void onFail(int errorCode,String msg);
    }
}
