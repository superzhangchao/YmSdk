package com.ym.game.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;


import com.ym.game.net.api.YmApi;
import com.ym.game.net.api.YmApiService;
import com.ym.game.plugin.google.dao.DaoUtils;
import com.ym.game.plugin.google.dao.LocalPurchaseBean;
import com.ym.game.sdk.callback.BindCallBack;
import com.ym.game.sdk.callback.ExitCallBack;
import com.ym.game.sdk.callback.ShareCallBack;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;

import com.ym.game.sdk.common.utils.DevicesUtils;
import com.ym.game.sdk.config.YmErrorCode;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.sdk.common.base.net.ApiFactory;
import com.ym.game.sdk.common.base.net.RetrofitFactory;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.frame.logger.AndroidLogAdapter;
import com.ym.game.sdk.common.frame.logger.Logger;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.config.Config;


import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.constants.YmLanguageEnum;
import com.ym.game.sdk.constants.Ymlanguage;
import com.ym.game.sdk.invoke.plugin.FBPluginApi;
import com.ym.game.sdk.invoke.plugin.GooglePluginApi;
import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.sdk.presenter.UserPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YmSdkApi {


    private static final String TAG = "Ymsdk";
    private volatile static YmSdkApi instance;
    private Context mContext;
    private static boolean initState = false;

    private static Handler sApiHandler;

    private String[] ymlanguages= {"zh_CN", "zh_TW", "en", "ja", "ko", "vi", "th"};
    private String setLang = YmConstants.EN;

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
     * @param
     */
    public void initPlatform(final Context context, String gameId,String gameKey) {



        Config.setGameId(gameId);
        initDate();
        initLanguage();


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

    private void initDate() {

    }

    private void initLanguage() {
        String deviceLang = DevicesUtils.getDeviceLang();

        for (String lang: ymlanguages) {
            if (TextUtils.equals(deviceLang,lang)){
                setLang = lang;
            }
        }
        Config.setLanguage(setLang);
    }

    public void setLanguage(String lang){
        Config.setLanguage(lang);
    }

    public String getLanguage(){
        return Config.getLanguage();
    }

    public void setDebugMode(boolean isDebug) {
        if (isDebug) {
            YmApi.setBaseUrl(Config.TESTBASEURL);
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
    }

    public void initEventReport(Application application,String channel,boolean isDebug){


    }

    public void testNet(){
        String localTs = System.currentTimeMillis()+"";
        Call<String> token = YmApi.getInstance().getTime();
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
        Call<String> token = ApiFactory.getFactory().create(YmApiService.class).getTime();
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

    }

    public void loginEvent(String uid){

    }

    public void createOrder(String orderId,String  currencyType,float currencyAmount){

    }
    public void paySuccessEvent(String orderId,String paymentType,String  currencyType,float currencyAmount){
        //sdk支付订单 paymentType支付类型  货币单位：CNY人民币 货币金额：单位元 类型float

    }

    public void trackEvent(String eventName){

    }

//    public void reportRoleInfo(RoleInfo roleInfo){
//    }


    //sdk操作
    public void login(Activity activity,LoginCallBack loginCallBack){
        CallbackMananger.setLoginCallBack(loginCallBack);
        UserPresenter.showLoginActiviy(activity);

    }

    public void bind(Activity activity, BindCallBack bindCallBak){
        CallbackMananger.setBindCallBack(bindCallBak);
        UserPresenter.showBindActiviy(activity);
    }

    public void logout(Activity activity){
        UserPresenter.logout(activity);
    }

    public void pay(Activity activity, PurchaseBean purchaseBean, PayCallBack payCallBack){
        if (!UserPresenter.isLogin()){
            payCallBack.onFailure(YmErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_nologin")));
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_please_login")));
            return;
        }

        if (purchaseBean==null){
            payCallBack.onFailure(YmErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_order_error")));
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_order_error")));

            return;
        }

        if (PluginManager.getInstance().getPlugin("plugin_google") ==null){
            payCallBack.onFailure(YmErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_no_paytype")));
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_no_paytype")));
            return;
        }
        CallbackMananger.setPayCallBack(payCallBack);
        PurchasePresenter.createOrder(activity, purchaseBean);
    }



    public void share(Context context, Map<String,Object>shareMap, final ShareCallBack shareCallBack){

        CallBackListener shareCallBackListener = new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                shareCallBack.onSuccess(0);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ErrorCode.CANCEL){
                    shareCallBack.onCancel();
                }else if(code == ErrorCode.FAILURE){
                    shareCallBack.onFailure(ErrorCode.FAILURE,msg);
                }
            }

        };
        FBPluginApi.getInstance().share(context,shareMap,shareCallBackListener);
    }

    public void exit(Context context, ExitCallBack exitCallBack){
        //实现退出回调
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

    public void  onStart(Context context){
        PluginManager.getInstance().onStart(context);
    }

    public void onPuase(Context context) {
        PluginManager.getInstance().onPause(context);
    }

    public void onDestroy(Context context) {
        PluginManager.getInstance().onDestroy(context);

    }

    public void onConfigurationChanged(Context context) {
        PluginManager.getInstance().onDestroy(context);
    }

    public void onActivityResult(Context context,int requestCode, int resultCode, @Nullable Intent data) {
        PluginManager.getInstance().onActivityResult(context,requestCode,resultCode,data);
    }

}
