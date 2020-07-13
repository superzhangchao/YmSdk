package com.ym.game.sdk.bean;

public class AccountBean {
    public String uid;
    public String token;
    public String nickName;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "AccountBean{" +
                "uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
