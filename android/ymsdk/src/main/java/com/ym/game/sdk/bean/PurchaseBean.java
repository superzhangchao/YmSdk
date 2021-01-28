package com.ym.game.sdk.bean;

import java.io.Serializable;

public class PurchaseBean implements Serializable {
    private String payType;
    private String accessToken;
    private String mProductDesc;//可以为空不签名
    private String mProductId;
    private String mProductName;
    private String mProductPrice;
    private String mOrderId;
    private String mServerId;
    private String mRoleId;
    private String mRoleName;//可以为空不签名
    private String mRoleLevel;//可以为空不签名
    private String mUserId;//平台的唯一标识
    private String mGameSign;
    private String mExt;
    private String mPlatformOrderId;
//    private String notifyUrl;//不签名


    public String getPayType() {
        return payType;
    }
    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProductDesc() {
        return mProductDesc;
    }
    public void setProductDesc(String productDesc) {
        this.mProductDesc = productDesc;
    }

    public String getProductId() {
        return mProductId;
    }
    public void setProductId(String productId) {
        this.mProductId = productId;
    }

    public String getProductName() {
        return mProductName;
    }
    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public String getProductPrice() {
        return mProductPrice;
    }
    public void setProductPrice(String productPrice) {
        this.mProductPrice = productPrice;
    }

    public String getOrderId() {
        return mOrderId;
    }
    public void setOrderId(String orderId) {
        this.mOrderId = orderId;
    }

    public String getServerId() {
        return mServerId;
    }
    public void setServerId(String serverId) {
        this.mServerId = serverId;
    }

    public String getRoleId() {
        return mRoleId;
    }
    public void setRoleId(String roleId) {
        this.mRoleId = roleId;
    }

    public String getRoleName() {
        return mRoleName;
    }
    public void setRoleName(String roleName) {
        this.mRoleName = roleName;
    }

    public String getRoleLevel() {
        return mRoleLevel;
    }
    public void setRoleLevel(String roleLevel) {
        this.mRoleLevel = roleLevel;
    }

    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getGameSign() {
        return mGameSign;
    }
    public void setGameSign(String gameSign) {
        this.mGameSign = gameSign;
    }


    public String getExt() {
        return mExt;
    }
    public void setExt(String ext) {
        this.mExt = ext;
    }

    public String getPlatformOrderId() {
        return mPlatformOrderId;
    }

    public void setPlatformOrderId(String platformOrderId) {
        this.mPlatformOrderId = platformOrderId;
    }

    public PurchaseBean(){
    }

    private  PurchaseBean(PurchaseBeanBuilder builder){
          mProductDesc = builder.mProductDesc;//可以为空不签名
          mProductId = builder.mProductId;
          mProductName = builder.mProductName;
          mProductPrice = builder.mProductPrice;
          mOrderId = builder.mOrderId;
          mServerId = builder.mServerId;
          mRoleId = builder.mRoleId;
          mRoleName = builder.mRoleName;//可以为空不签名
          mRoleLevel = builder.mRoleLevel;//可以为空不签名
          mGameSign = builder.mGameSign;
          mExt = builder.mExt;
    }

    public static  class  PurchaseBeanBuilder{
        private String mProductDesc;
        private String mProductId;
        private String mProductName;
        private String mProductPrice;
        private String mOrderId;
        private String mServerId;
        private String mRoleId;
        private String mRoleName;
        private String mRoleLevel;
        private String mGameSign;
        private String mExt;

        public PurchaseBeanBuilder() {

        }

        public PurchaseBeanBuilder setProductDesc(String productDesc){
            mProductDesc = productDesc;
            return this;
        }
        public PurchaseBeanBuilder setProductId(String productId){
            mProductId = productId;
            return this;
        }
        public PurchaseBeanBuilder setProductName(String productName){
            mProductName = productName;
            return this;
        }
        public PurchaseBeanBuilder setProductPrice(String productPrice){
            mProductPrice = productPrice;
            return this;
        }
        public PurchaseBeanBuilder setOrderId(String orderId){
            mOrderId = orderId;
            return this;
        }
        public PurchaseBeanBuilder setServerId(String serverId){
            mServerId = serverId;
            return this;
        }
        public PurchaseBeanBuilder setRoleId(String roleId){
            mRoleId = roleId;
            return this;
        }
        public PurchaseBeanBuilder setRoleName(String roleName){
            mRoleName = roleName;
            return this;
        }
        public PurchaseBeanBuilder setRoleLevel(String roleLevel){
            mRoleLevel = roleLevel;
            return this;
        }

        public PurchaseBeanBuilder setGameSign(String gameSign){
            mGameSign = gameSign;
            return this;
        }
        public PurchaseBeanBuilder setExt(String ext){
            mExt = ext;
            return this;
        }
        public PurchaseBean build(){
            return new PurchaseBean(this);
        }
    }

}
