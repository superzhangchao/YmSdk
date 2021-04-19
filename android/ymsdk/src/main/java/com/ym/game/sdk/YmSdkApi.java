package com.ym.game.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.Handler;
import android.os.HandlerThread;


import com.reyun.tracking.sdk.Tracking;
import com.ym.game.net.api.YmApi;
import com.ym.game.net.api.YmApiService;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.sdk.callback.RealNameCallBack;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.net.ApiFactory;
import com.ym.game.sdk.common.base.net.RetrofitFactory;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.frame.logger.AndroidLogAdapter;
import com.ym.game.sdk.common.frame.logger.Logger;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.config.Config;

import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.sdk.presenter.UserPresenter;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YmSdkApi {


    private static final String TAG = "Ymsdk";
    private static final int GONE_RECENT_LOGIN = 0;

    private static final int FASTLOGINVERIFYTOKE = 0;
    private static final int VERIFYTOKENFAIL = 1;
    private static final int VERIFYTOKENNETERROR = 2;
    private static final int GETACCOUNTSUCCESS = 3;
    private static final int GETACCOUNTFAIL = 4;
    private static final int GETACCOUNTNETFAIL = 5;

    private volatile static YmSdkApi instance;
    private Context mContext;
    private LoginCallBack mLoginCallBack;
    private String currentLoginType;
    private long currentTime;
    public static YmSdkApi getInstance() {

        if (instance == null) {
            synchronized (YmSdkApi.class) {
                if (instance == null) {
                    instance = new YmSdkApi();
                }
            }
        }
        return instance;
    }

    private YmSdkApi() {

    }

    /**
     * 初始化SDK
     *
     * @param gameId
     */
    private static Handler sApiHandler;
    private static boolean initState = false;

    public void initPlatform(final Context context, String gameId) {
        Config.setGameId(gameId);

        if (sApiHandler == null) {
            HandlerThread ht = new HandlerThread("project_sdk_thread",
                    Process.THREAD_PRIORITY_BACKGROUND);
            ht.start();
            sApiHandler = new Handler(ht.getLooper());
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //4、加载功能插件
                PluginManager.init(context).loadAllPlugins();



            }
        };
        sApiHandler.post(r);
    }


    public void setDebugMode(boolean isDebug) {
        if (isDebug) {
            YmApi.setBaseUrl(Config.TESTBASEURL);
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
    }

    public void initEventReport(Application application,String channel,boolean isDebug){
        currentTime  = System.currentTimeMillis();
        Tracking.initWithKeyAndChannelId(application,application.getString(ResourseIdUtils.getStringId("reyunappkey")),channel);
        if (isDebug){
            Tracking.setDebugMode(isDebug);
        }
    }

    public void testNet(){
        String localTs = System.currentTimeMillis()+"";
        Call<String> token = YmApi.getInstance().getTime(localTs);
        token.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String body = response.body();
                    Logger.i("获取当前时间成功："+body);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void testNet2(){
        RetrofitFactory.setBaseUrl(Config.TESTBASEURL);
        String localTs = System.currentTimeMillis()+"";
        Call<String> token = ApiFactory.getFactory().create(YmApiService.class).getTime(localTs);
        token.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String body = response.body();
                    Logger.i("获取当前时间成功："+body);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    public void registerEvent(){
        Tracking.setRegisterWithAccountID(Tracking.getDeviceId());
    }

    public void loginEvent(String uid){
        Tracking.setLoginSuccessBusiness(uid);
    }

    public void createOrder(String orderId,String  currencyType,float currencyAmount){
        Tracking.setOrder(orderId, currencyType, currencyAmount);
    }
    public void paySuccessEvent(String orderId,String paymentType,String  currencyType,float currencyAmount){
        //sdk支付订单 paymentType支付类型  货币单位：CNY人民币 货币金额：单位元 类型float
        Tracking.setPayment(orderId, paymentType, currencyType, currencyAmount);
    }

    public void trackEvent(String eventName){
        Tracking.setEvent(eventName);
    }

    //sdk操作
    public void login(Activity activity,LoginCallBack loginCallBack){
        CallbackMananger.setLoginCallBack(loginCallBack);
        UserPresenter.showLoginActiviy(activity);
    }

    public void logout(Activity activity){
        UserPresenter.logout(activity);
    }

    public void pay(Activity activity, PurchaseBean purchaseBean, PayCallBack payCallBack){
        if (purchaseBean==null){
            payCallBack.onFailure(ErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_order_error")));
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_order_error")));

            return;
        }

        if (PluginManager.getInstance().getPlugin("plugin_alipay") ==null&& PluginManager.getInstance().getPlugin("plugin_wechat")==null){
            payCallBack.onFailure(ErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_no_paytype")));
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_no_paytype")));
            return;
        }
        CallbackMananger.setPayCallBack(payCallBack);
        PurchasePresenter.showPurchasePage(activity, purchaseBean);
    }

    public int getRealNameStatus(){
        return UserPresenter.getRealNameStatus();
    }

    public void showRealName(Activity activity, boolean limit, RealNameCallBack realNameCallBack){
        CallbackMananger.setRealNameCallBack(realNameCallBack);
        AccountBean loginAccountInfo = UserPresenter.getLoginAccountInfo();
        int realNameType;
        if (limit){
            realNameType = YmConstants.LIMITREALNAMETYPE;
        }else {
            realNameType =YmConstants.UNLIMITREALNAMETYPE;
        }
        UserPresenter.showRealNameActiviy(activity,loginAccountInfo,realNameType);
    }
    public void onCreate(Context context, Bundle savedInstanceState) {
        mContext = context;
        PluginManager.getInstance().onCreate(context,savedInstanceState);
    }

    public void onResume(final Context context) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },100);
        PluginManager.getInstance().onResume(context);
    }

    public void onPuase(Context context) {
        PluginManager.getInstance().onPause(context);
    }

    public void onDestroy(Context context) {
        PluginManager.getInstance().onDestroy(context);
        long duration = System.currentTimeMillis() - currentTime;
        Tracking.setAppDuration(duration);
        Tracking.exitSdk();
    }

    public void onConfigurationChanged(Context context) {
        PluginManager.getInstance().onDestroy(context);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

}
