package com.ym.game.sdk.bean;

public class HistoryAccountBean {

    public String phone;
    public String password;
    public long lastLoginTime;
    public boolean isCheck;

    public HistoryAccountBean(){};
    public HistoryAccountBean(String phone, String password, long lastLoginTime, boolean isCheck) {
        this.phone = phone;
        this.password = password;
        this.lastLoginTime = lastLoginTime;
        this.isCheck = isCheck;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
