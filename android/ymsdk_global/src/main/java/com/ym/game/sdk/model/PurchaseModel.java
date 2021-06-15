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
import com.ym.game.plugin.google.pay.GooglePay;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.listener.CreateOrderListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.PayStateListener;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.DevicesUtils;
import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.config.Config;
import com.ym.game.sdk.config.YmErrorCode;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.common.utils.ResourseIdUtils;

import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.game.sdk.invoke.plugin.GooglePluginApi;
import com.ym.game.plugin.google.dao.DaoUtils;
import com.ym.game.plugin.google.dao.LocalPurchaseBean;
import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.sdk.presenter.UserPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseModel implements IPurchaseModel{

    @SuppressLint("StaticFieldLeak")
    private static PurchaseModel instance;

    private PurchaseBean mPurchaseDate;
    private static final int SENDTIME = 1;
    private static final int SENDTIMEERROR = 2;
    private static final int SENDTIMENETERROR = 3;
    private static final int GETTOKENSUCCESS = 4;
    private static final int GETTOKENFAIL = 5;
    private static final int GETTOKENNETFAIL = 6;
    private static final int CREATEORDERSUCCESS = 7;
    private static final int CREATEORDERFAIL = 8;
    private static final int CREATEORDERNETFAIL = 9;
    private static final int VERIFYGOOGLEORDERSUCCESS = 10;
    private static final int VERIFYGOOGLEORDERFAIL = 11;
    private static final int VERIFYGOOGLEORDERNETFAIL = 12;



    private static final String PAYTYPEGP = "google";

    private String currentTs;
    private String currentAccessToken;
    private Context mContext;


    private String platformOrderId;
    private GetVerifyDataListener mGetVerifyDataListener;
    private CreateOrderListener mCreateOrderListener;
    private int reportNum = 3;


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
            int netError = YmErrorCode.NET_ERROR;
            switch (msg.what) {
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    currentTs = timeInfo.optString("ts");
                    getAccessToken();
                    break;
                case GETTOKENSUCCESS:
                    currentAccessToken = (String) msg.obj;
                    mGetVerifyDataListener.onSuccess(currentTs,currentAccessToken);
                    break;
                case CREATEORDERSUCCESS:
                    ResultOrderBean.DataBean dataBean = (ResultOrderBean.DataBean) msg.obj;
                    platformOrderId = dataBean.getPf_order_no();
                    mCreateOrderListener.onSuccess();

                    break;
                case VERIFYGOOGLEORDERSUCCESS:
                    LocalPurchaseBean deletePurchaseBean = (LocalPurchaseBean) msg.obj;
                    deleteSuccessOrder(deletePurchaseBean);
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    mGetVerifyDataListener.onFail(YmErrorCode.NET_DATA_NULL,mContext.getString(ResourseIdUtils.getStringId("ym_text_netdata_null")));
                    break;
                case SENDTIMENETERROR:
                case GETTOKENNETFAIL:
                case CREATEORDERNETFAIL:
                    netError = (int) msg.obj;
                    if (netError == YmErrorCode.NET_DISCONNET){
                        messageName = "ym_text_disconnet";
                    }
                    mCreateOrderListener.onFail(netError,mContext.getString(ResourseIdUtils.getStringId(messageName)));
                    break;
                case GETTOKENFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mGetVerifyDataListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case CREATEORDERFAIL:
                    errorData = (Map<String, Object>) msg.obj;
                    mCreateOrderListener.onFail((int) errorData.get("code"), (String) errorData.get("message"));
                    break;
                case VERIFYGOOGLEORDERFAIL:
                case VERIFYGOOGLEORDERNETFAIL:
                    rereportPurchaseDb();
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
    public void destroy(Activity activity) {
    }

    @Override
    public void getVerifyData(Context context, GetVerifyDataListener getVerifyDataListener) {
        mContext =context;
        mGetVerifyDataListener = getVerifyDataListener;
        getTime();
    }


    private  void getTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<String> token = YmApi.getInstance().getTime();
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
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = SENDTIMENETERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = SENDTIMENETERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getAccessToken() {

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.FROMKEY, YmConstants.FROM);
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN,sign);
        param.put(YmConstants.NOTE, DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<TokenBean> token = YmApi.getInstance().getAccessToken(param);
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
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = GETTOKENNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = GETTOKENNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

    public void createOrder(PurchaseBean mPurchaseDate, CreateOrderListener createOrderListener) {
        mCreateOrderListener = createOrderListener;
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.VERSIONKEY, YmConstants.VERSION);
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.CPID, YmConstants.CPIDKEY);
        param.put(YmConstants.TS, mPurchaseDate.getTs());
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
        param.put(YmConstants.GAMEORDERID, mPurchaseDate.getGameOrderId());


        param.put(YmConstants.GAMESIGN, mPurchaseDate.getGameSign());

        param.put(YmConstants.EXT, mPurchaseDate.getExt());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN,sign);
        param.put(YmConstants.NOTE, DevicesUtils.getExtra());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultOrderBean> token = YmApi.getInstance().createOrder(param);
                token.enqueue(new Callback<ResultOrderBean>() {
                    @Override
                    public void onResponse(Call<ResultOrderBean> call, Response<ResultOrderBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()) {
                            ResultOrderBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {

                                message.obj = body.getData();
                                message.what = CREATEORDERSUCCESS;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",errorCode);
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = CREATEORDERFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = CREATEORDERNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultOrderBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = CREATEORDERNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    @Override
    public void startPay(final Context context, PurchaseBean purchaseBean, final PayStateListener payStateListener) {
        final Map<String,Object> payMap = new HashMap<>();
        payMap.put("userId", purchaseBean.getUserId());
        payMap.put("gameOrderId",purchaseBean.getGameOrderId());
        payMap.put("productId",purchaseBean.getProductId());
        GooglePluginApi.getInstance().pay(context, payMap, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                //支付成功
                payStateListener.onSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                payStateListener.onFail(code,msg);
            }
        }, new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                reportPurchaseDb(context);
            }

            @Override
            public void onFailure(int code, String msg) {
                //获取验证参数失败不作处理
            }
        });
    }

    public void checkPurchaseState(Context context){
        mContext = context;
        DaoUtils daoUtils = new DaoUtils(context);
        List<LocalPurchaseBean> localPurchaseBeanList = daoUtils.queryAll();
        for (LocalPurchaseBean localPurchaseBean :localPurchaseBeanList) {
            if (localPurchaseBean.purchaseState ==0){
                reportNum--;
                reportPurchaseDb(context);
            }
        }
    }

    private void reportPurchaseDb(final Context context){
        getVerifyData(context, new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                DaoUtils daoUtils = new DaoUtils(context);
                List<LocalPurchaseBean> localPurchaseBeanList = daoUtils.queryAll();
                for (LocalPurchaseBean localPurchaseBean :localPurchaseBeanList) {
                    if (localPurchaseBean.purchaseState ==0){
                        getReportPurchaseState(ts,accessToken,localPurchaseBean);
                    }
                }
            }

            @Override
            public void onFail(int status, String message) {
                rereportPurchaseDb();
            }
        });
    }

    private void getReportPurchaseState(String ts, String accessToken, final LocalPurchaseBean localPurchaseBean){
        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.CPID, YmConstants.CPIDKEY);
        param.put(YmConstants.USERID, localPurchaseBean.getUid());
        param.put(YmConstants.TS, ts);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        param.put(YmConstants.GAMEORDERID, localPurchaseBean.getGameOrderId());
        param.put(YmConstants.GOOGLEORDERID,localPurchaseBean.getGoogleOrderId());
        param.put(YmConstants.PACKAGENAME, localPurchaseBean.getPackageName());
        param.put(YmConstants.PRODUCTID, localPurchaseBean.getProductId());
        param.put(YmConstants.GOOGLEPURCHASETOKEN, localPurchaseBean.getPurchaseToken());
        param.put(YmConstants.LANG,Config.getLanguage());
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put(YmConstants.SIGN,sign);
        param.put(YmConstants.NOTE, DevicesUtils.getExtra());
        //TODO:获取消费状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultOrderBean> token = YmApi.getInstance().verifyGoogleOrder(param);
                token.enqueue(new Callback<ResultOrderBean>() {
                    @Override
                    public void onResponse(Call<ResultOrderBean> call, Response<ResultOrderBean> response) {
                        Message message = new Message();
                        if (response.isSuccessful()) {
                            ResultOrderBean body = response.body();
                            int errorCode = body.getCode();
                            if (errorCode == YmConstants.SUCCESSCODE) {

                                message.obj = localPurchaseBean;
                                message.what = VERIFYGOOGLEORDERSUCCESS;

                            }else {
                                Map<String,Object> errorData = new HashMap<>();
                                errorData.put("code",errorCode);
                                errorData.put("message",body.getMessage());
                                message.obj = errorData;
                                message.what = VERIFYGOOGLEORDERFAIL;
                            }
                        }else {
                            int errorCode = YmErrorCode.NET_DISCONNET;
                            message.obj = errorCode;
                            message.what = VERIFYGOOGLEORDERNETFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultOrderBean> call, Throwable t) {
                        Message message = new Message();
                        int errorCode = YmErrorCode.NET_ERROR;
                        message.obj = errorCode;
                        message.what = VERIFYGOOGLEORDERNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void rereportPurchaseDb() {
        if (reportNum!=0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    reportNum--;
                    reportPurchaseDb(mContext);
                }
            }, 1000);
        }
    }

    private void deleteSuccessOrder(LocalPurchaseBean deletePurchaseBean) {
        DaoUtils daoUtils = new DaoUtils(mContext);
        daoUtils.deletePurchase(deletePurchaseBean);

    }
}
