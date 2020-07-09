package com.ym.game.net.bean;

import com.google.gson.annotations.SerializedName;

public class AccoutBean {

    /**
     * code : 0
     * message :
     * data : {"uid":12121212,"login_token":""}
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
         * uid : "12121212"
         * login_token :
         */

        private String uid;
        @SerializedName("login_token")
        private String loginToken;


        @SerializedName("nick_name")
        private String nickName;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLoginToken() {
            return loginToken;
        }

        public void setLoginToken(String loginToken) {
            this.loginToken = loginToken;
        }

        public String getNickname() {
            return nickName;
        }

        public void setNickname(String nickName) {
            this.nickName = nickName;
        }
    }
}
