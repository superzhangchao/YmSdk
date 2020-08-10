package com.ym.game.net.api;

import com.ym.game.net.bean.ResultVcodeBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.ResultAccoutBean;
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
    Call<String> getTime(@Query("localts")String localTs);

    @GET("sms")
    Call<ResultVcodeBean> getVcode(@QueryMap Map<String, String> vcodeReq);

    @GET("user/check/phone")
    Call<ResultVcodeBean> checkBind(@QueryMap Map<String, String> vcodeReq);

    @GET("user/login/phone")
    Call<ResultAccoutBean> getPhoneAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/weixin")
    Call<ResultAccoutBean> getWeixinAccoutInfo(@QueryMap Map<String, String> accountReq);


    @GET("user/login/qq")
    Call<ResultAccoutBean> getQQAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/guest")
    Call<ResultAccoutBean> getGuestAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/quick")
    Call<ResultAccoutBean> quickLogin(@QueryMap Map<String, String> accountReq);

    @GET("user/bind/phone")
    Call<ResultAccoutBean> bindAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/bind/idcard")
    Call<ResultAccoutBean> realName(@QueryMap Map<String, String> accountReq);

    @GET("pay")
    Call<ResultOrderBean> checkorder(@QueryMap Map<String, String> orderReq);
}
