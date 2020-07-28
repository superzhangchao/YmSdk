package com.ym.game.net.bean;

import com.google.gson.annotations.SerializedName;

public class ResultAccoutBean {

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
         * login_type:
         * auth_status:
         */

        private String uid;
        @SerializedName("login_token")
        private String loginToken;


        @SerializedName("nick_name")
        private String nickName;

        @SerializedName("login_type")
        private String loginType;

        @SerializedName("auth_status")
        private int authStatus;


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



        public String getLoginType() {
            return loginType;
        }

        public void setLoginType(String loginType) {
            this.loginType = loginType;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getAuthStatus() {
            return authStatus;
        }

        public void setAuthStatus(int authStatus) {
            this.authStatus = authStatus;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "uid='" + uid + '\'' +
                    ", loginToken='" + loginToken + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", loginType='" + loginType + '\'' +
                    ", authStatus=" + authStatus +
                    '}';
        }
    }
}
