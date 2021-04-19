package com.ym.game.net.api;

import android.text.TextUtils;

import com.ym.game.net.bean.ResultOrderBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.sdk.config.Config;
import com.ym.game.sdk.constants.YmConstants;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YmApi {
    private static YmApi instance;
    private static String baseUrl;
    private YmApiService service;

    private YmApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = Config.RELEASEURL;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(YmApiService.class);

    }

    public static void setBaseUrl(String url){
        baseUrl = url;
    }

    public static YmApi getInstance(){
        if(instance == null){
            instance = new YmApi();
        }
        return instance;
    }

//    public Call<ResultInitBean> getInitInfo(Map<String,String> initReq){
//        return service.getInitInfo(initReq);
//    }
    public Call<TokenBean> getAccessToken(Map<String, String> tokenReq){
        return  service.getAccessToken(tokenReq);
    }
    public Call<String> getTime(){
        return service.getTime();
    }


    public Call<ResultAccoutBean>  getFBAccoutInfo(Map<String, String> accountReq){
        return service.getFBAccoutInfo(accountReq);
    }

    public Call<ResultAccoutBean>  getGoogleAccoutInfo(Map<String, String> accountReq){
        return service.getGoogleAccoutInfo(accountReq);
    }

    public Call<ResultAccoutBean>  getGuestAccoutInfo(Map<String, String> accountReq){
        return service.getGuestAccoutInfo(accountReq);
    }

    public Call<ResultAccoutBean> quickLogin(Map<String, String> accountReq){
        return service.quickLogin(accountReq);
    }

    public Call<ResultAccoutBean> bindFbAccount(Map<String,String> accountReq){
        return service.bindFbAccount(accountReq);
    }
    public Call<ResultAccoutBean> bindGoolgeAccount(Map<String,String> accountReq){
        return service.bindGoolgeAccount(accountReq);
    }

    public Call<ResultOrderBean> createOrder(Map<String, String> orderReq){
        return service.createOrder(orderReq);
    }

    public Call<ResultOrderBean> verifyGoogleOrder(Map<String, String> orderReq){
        return service.verifyGoogleOrder(orderReq);
    }


}
