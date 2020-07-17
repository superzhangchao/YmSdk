package com.ym.game.net.api;

import com.ym.game.net.bean.ResultVcodeBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.AccoutBean;
import com.ym.game.net.bean.ResultOrderBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface YmApiService {

    @GET("token")
    Call<TokenBean>  getTokenInfo(@Query("app_id")String appId,
                                         @Query("from")String from,@Query("ts")String ts,@Query("sign")String sign);

    @GET("time")
    Call<String> getTime();

    @GET("sms")
    Call<ResultVcodeBean> getVcode(@QueryMap Map<String, String> vcodeReq);

    @GET("user/login/weixin")
    Call<AccoutBean> getWeixinAccoutInfo(@QueryMap Map<String, String> accountReq);


    @GET("user/login/qq")
    Call<AccoutBean> getQQAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/guest")
    Call<AccoutBean> getGuestAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/quick")
    Call<AccoutBean> quickLogin(@QueryMap Map<String, String> accountReq);

    @GET("pay")
    Call<ResultOrderBean> checkorder(@QueryMap Map<String, String> orderReq);
}
