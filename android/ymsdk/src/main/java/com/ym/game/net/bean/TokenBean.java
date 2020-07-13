package com.ym.game.net.bean;

import com.google.gson.annotations.SerializedName;

public class TokenBean {

    /**
     * code : 0
     * message :
     * data : {"access_token":"3GezYZALhb5DJFC2_00NC.ngIaQkYxnl6aDzJM8hSNOLyDF.1abEAaWtu_wWt1ggYbuicQjWtt","expire_time":300}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * access_token : 3GezYZALhb5DJFC2_00NC.ngIaQkYxnl6aDzJM8hSNOLyDF.1abEAaWtu_wWt1ggYbuicQjWtt
         * expire_time : 300
         */

        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("expire_time")
        private int expireTime;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }
}
