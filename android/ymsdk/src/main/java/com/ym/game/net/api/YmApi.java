package com.ym.game.net.api;

import com.ym.game.net.bean.ResultOrderBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.AccoutBean;

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

    public Call<TokenBean> getTokenInfo(String appId,String from,String ts,String sign){
        return  service.getTokenInfo(appId,from,ts,sign);
    }
    public Call<String> getTime(){
        return service.getTime();
    }

    public Call<AccoutBean>  getWeixinAccoutInfo(Map<String, String> accountReq){
        return service.getWeixinAccoutInfo(accountReq);
    }

    public Call<AccoutBean>  getQQAccoutInfo(Map<String, String> accountReq){
        return service.getQQAccoutInfo(accountReq);
    }
    public Call<AccoutBean>  getGuestAccoutInfo(Map<String, String> accountReq){
        return service.getGuestAccoutInfo(accountReq);
    }

    public Call<AccoutBean> quickLogin(Map<String, String> accountReq){
        return service.quickLogin(accountReq);
    }

    public Call<ResultOrderBean> checkorder(Map<String, String> orderReq){
        return service.checkorder(orderReq);
    }

}
