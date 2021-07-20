package com.ym.ysfj;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.plugin.google.dao.DaoUtils;
import com.ym.game.plugin.google.dao.LocalPurchaseBean;
import com.ym.game.sdk.YmSdkApi;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.BindCallBack;
import com.ym.game.sdk.callback.ExitCallBack;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.sdk.callback.ShareCallBack;
import com.ym.game.sdk.callback.listener.BindStatusListener;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.frame.logger.Logger;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.ysfjen.R;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.VectorEnabledTintResources;


public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "Ymsdk";
    private Button btInit;
    private Button btLogin;
    private Button btLogout;
    private Button btPay;
    private Button btSendinfo;

    private String text;
    private String key ="vJV8kCxV";
    private String encryptDES;
    private TextView tv;
    private Button btShare;


    private Button btBind;
    private String uid;
    private Button btProduct1;
    private Button btProduct2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btInit = (Button) findViewById(R.id.bt_init);
        btLogin = (Button) findViewById(R.id.bt_login);
        btLogout = (Button) findViewById(R.id.bt_logout);
        btPay = (Button) findViewById(R.id.bt_pay);
        btShare = (Button) findViewById(R.id.bt_share);
        btSendinfo = (Button) findViewById(R.id.bt_sendinfo);
        btProduct1 = (Button) findViewById(R.id.bt_product1);
        btProduct2 = (Button) findViewById(R.id.bt_product2);


        btBind = (Button) findViewById(R.id.bt_bind);
        tv = (TextView) findViewById(R.id.tv);
        btInit.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        btShare.setOnClickListener(this);
        btPay.setOnClickListener(this);
        btSendinfo.setOnClickListener(this);
        btBind.setOnClickListener(this);
        btProduct1.setOnClickListener(this);
        btProduct2.setOnClickListener(this);


        YmSdkApi.getInstance().onCreate(this,savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity2.this ==null){
            Logger.i("context is null");
        }
        YmSdkApi.getInstance().onResume(MainActivity2.this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onPause() {
        super.onPause();
        YmSdkApi.getInstance().onPuase(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        YmSdkApi.getInstance().onStart(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YmSdkApi.getInstance().onDestroy(this);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YmSdkApi.getInstance().onActivityResult(this,requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        YmSdkApi.getInstance().onConfigurationChanged(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            setFullScreen();

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==btInit.getId()){
//            YmSdkApi.getInstance().setDebugMode(true);
            DaoUtils daoUtils = new DaoUtils(this);
            List<LocalPurchaseBean> localPurchaseBeans = daoUtils.queryAll();
            Log.i(TAG, "onClick: ");
        }else if(btLogin.getId()==v.getId()){
            YmSdkApi.getInstance().login(this, new LoginCallBack() {
                @Override
                public void onSuccess(Object o) {
                    ResultAccoutBean.DataBean dataBean = (ResultAccoutBean.DataBean)o;
                    uid = dataBean.getUid();
                    String loginToken = dataBean.getLoginToken();
                    String nickName = dataBean.getNickName();
                    String loginType = dataBean.getLoginType();
                    Logger.i("ysfjen login onSuccess:"+(String)o.toString());
//                    ToastUtils.showToast(MainActivity2.this,"login is success");
                }

                @Override
                public void onCancel() {
                    Logger.i("ysfjen login onCancel:");
            //                    ToastUtils.showToast(MainActivity2.this,"login is cancel");
                }

                @Override
                public void onFailure(int code, String msg) {
                    Logger.i("ysfjen login onFailure:"+msg);
            //                    ToastUtils.showToast(MainActivity2.this,"login is fail");
                }
            });

        }else if (btSendinfo.getId()==v.getId()){
        }else if(btLogout.getId() ==v.getId()) {
            YmSdkApi.getInstance().logout(this);
        }else if(btPay.getId()==v.getId()){

            YmSdkApi.getInstance().pay(this, getPurchaseBean("com.ysfjen.1usd"), new PayCallBack() {
                @Override
                public void onSuccess(Object o) {
                    Log.i(TAG, "onSuccess: 支付成功");
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "onCancel: 支付取消");
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.i(TAG, "onFailure: 支付失败");
                }
            });

        }else if(btShare.getId()==v.getId()){
            YmSdkApi.getInstance().share(this,null, new ShareCallBack() {
                @Override
                public void onSuccess(Object o) {
                    Logger.i("ysfjen login onSuccess:"+o.toString());
                }

                @Override
                public void onFailure(int code, String msg) {
                    Logger.i("ysfjen login onFailure:"+msg);
                }

                @Override
                public void onCancel() {
                    Logger.i("ysfjen login onCancel:");
                }
            });
        }else if (btBind.getId()==v.getId()) {

            YmSdkApi.getInstance().bind(this, new BindCallBack() {
                @Override
                public void onSuccess(Object o) {
                    Logger.i("ysfjen bind onSuccess:" + (String) o.toString());
//                    ToastUtils.showToast(MainActivity2.this,"bind is success");

                }

                @Override
                public void onFailure(int code, String msg) {
                    Logger.i("ysfjen btBind onFailure:" + msg);
//                    ToastUtils.showToast(MainActivity2.this,msg);
                }

                @Override
                public void onCancel() {
                    Logger.i("ysfjen btBind onCancel:");

//                    ToastUtils.showToast(MainActivity2.this,"bind is cancel");
                }

                @Override
                public void onSwitch() {
                    Logger.i("ysfjen btBind onSwitch:");

                }
            });
        }else if (btProduct1.getId()==v.getId()){
            YmSdkApi.getInstance().pay(this, getPurchaseBean("com.ysfjen.1usd"), new PayCallBack() {
                @Override
                public void onSuccess(Object o) {
                    Log.i(TAG, "onSuccess: 支付成功");
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "onCancel: 支付取消");
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.i(TAG, "onFailure: 支付失败");
                }
            });
        }else if (btProduct2.getId()==v.getId()){
//            YmSdkApi.getInstance().pay(this, getPurchaseBean("com.ysfjen.1usd"), new PayCallBack() {
//                @Override
//                public void onSuccess(Object o) {
//                    Log.i(TAG, "onSuccess: 支付成功");
//                }
//
//                @Override
//                public void onCancel() {
//                    Log.i(TAG, "onCancel: 支付取消");
//                }
//
//                @Override
//                public void onFailure(int code, String msg) {
//                    Log.i(TAG, "onFailure: 支付失败");
//                }
//            });
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("网络异常，无法下载");
            builder.setCancelable(false);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            if (!this.isFinishing()) {
                builder.show();
            }
        }
    }

    public PurchaseBean getPurchaseBean(String productId){
        //"com.ym.ysfjtest02"
        int random = (int) (Math.random() * 1000);
        int productPrice = (int) (Double.parseDouble("0.99")*100);
        String gameSign = getGameSign(productId,"新手装备大礼包",productPrice+"","g123456"+random,
                "s1","147258","张三","1","gt-1258");
        PurchaseBean purchaseBean = new PurchaseBean.PurchaseBeanBuilder()
                .setUserId(uid)
                .setPayType("google")
                .setGameOrderId("g123456"+random)
                .setProductId(productId)
                .setServerId("s1")
                .setRoleId("147258")
                .setRoleName("张三")
                .setRoleLevel("1")
                .setProductName("新手装备大礼包")
                .setProductDesc("新手装备大礼包")
                .setProductPrice(productPrice+"")
                .setExt("sdfsdfsf")
                .setGameSign(gameSign)
                .build();
        return purchaseBean;
    }

    public String getGameSign(String productId, String productName, String productPrice, String orderId,
                               String serverId, String roleId, String roleName, String rolelevel, String userId) {
        Map<String,String> parasign = new HashMap<>();
        parasign.put("product_id",productId);
        parasign.put("product_name",productName);
        parasign.put("product_price",productPrice);
        parasign.put("game_order_no",orderId);
        return YmSignUtils.getYmSign(parasign,"618AD3E8BB014660504C7931EB518FDF");

    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (android.os.Build.VERSION.SDK_INT > 18) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {

                        @SuppressLint("NewApi") @Override
                        public void onSystemUiVisibilityChange(int visibility) {

                            getWindow()
                                    .getDecorView()
                                    .setSystemUiVisibility(
                                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }

                    });
        }

    }

}
