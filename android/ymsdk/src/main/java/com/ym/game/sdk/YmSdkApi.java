package com.ym.game.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;


import com.ym.game.net.api.YmApi;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.config.Config;

import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.sdk.presenter.UserPresenter;


import androidx.annotation.Nullable;

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
    private Context context;
    private LoginCallBack mLoginCallBack;
    private String currentLoginType;

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

    public void initPlatform(final Activity activity, String gameId) {
        this.context = activity;
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
                PluginManager.init(activity).loadAllPlugins();



            }
        };
        sApiHandler.post(r);
    }


    public void setDebugMode(boolean isDebug) {
        if (isDebug) {
            YmApi.setBaseUrl(Config.TESTBASEURL);
//            Logger.addLogAdapter(new AndroidLogAdapter());
        }
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
        CallbackMananger.setPayCallBack(payCallBack);
        PurchasePresenter.showPurchasePage(activity, purchaseBean);
    }

    public int getRealNameStatus(){
        return UserPresenter.getRealNameStatus();
    }

    public void onResume(Activity activity) {

    }

    public void onPuase(Activity activity) {

    }

    public void onDestroy(Activity activity) {
    }

    public void onConfigurationChanged(Activity activity) {

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

}
