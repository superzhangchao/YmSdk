package com.ym.game.sdk.bean;

import java.io.Serializable;

public class PurchaseBean implements Serializable {
    private String mPayType;
    private String ts;
    private String accessToken;
    private String mProductDesc;//可以为空不签名
    private String mProductId;
    private String mProductName;
    private String mProductPrice;
    private String mGameOrderId;//游戏订单号
    private String mServerId;
    private String mRoleId;
    private String mRoleName;//可以为空不签名
    private String mRoleLevel;//可以为空不签名
    private String mUserId;//平台的唯一标识
    private String mGameSign;
    private String mGoogleOrderId;//谷歌订单号
    private String mPackageName;//包名
    private String mPurchaseToken;//谷歌购买token
    private String mExt;
    private String mPlatformOrderId;


//    private String notifyUrl;//不签名


    public String getPayType() {
        return mPayType;
    }
    public void setPayType(String payType) {
        this.mPayType = payType;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
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

    public String getGameOrderId() {
        return mGameOrderId;
    }
    public void setGameOrderId(String gameOrderId) {
        this.mGameOrderId = gameOrderId;
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

    public String getGoogleOrderId() {
        return mGoogleOrderId;
    }

    public void setGoogleOrderId(String googleOrderId) {
        this.mGoogleOrderId = googleOrderId;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public String getPurchaseToken() {
        return mPurchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.mPurchaseToken = purchaseToken;
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
        mPayType = builder.mPayType;
        mProductDesc = builder.mProductDesc;//可以为空不签名
        mProductId = builder.mProductId;
        mProductName = builder.mProductName;
        mProductPrice = builder.mProductPrice;
        mGameOrderId = builder.mGameOrderId;
        mServerId = builder.mServerId;
        mRoleId = builder.mRoleId;
        mRoleName = builder.mRoleName;//可以为空不签名
        mRoleLevel = builder.mRoleLevel;//可以为空不签名
        mUserId = builder.mUserId;
        mGoogleOrderId = builder.mGoogleOrderId;
        mPackageName = builder.mPackageName;
        mPurchaseToken =builder.mPurchaseToken;
        mGameSign = builder.mGameSign;
        mExt = builder.mExt;
    }

    public static  class  PurchaseBeanBuilder{
        private String mPayType;
        private String mProductDesc;
        private String mProductId;
        private String mProductName;
        private String mProductPrice;
        private String mGameOrderId;
        private String mServerId;
        private String mRoleId;
        private String mRoleName;
        private String mRoleLevel;
        private String mUserId;
        private String mGoogleOrderId;
        private String mPackageName;
        private String mPurchaseToken;
        private String mGameSign;
        private String mExt;

        public PurchaseBeanBuilder() {

        }

        public PurchaseBeanBuilder setPayType(String payType){
            mPayType = payType;
            return this;
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
        public PurchaseBeanBuilder setGameOrderId(String gameOrderId){
            mGameOrderId = gameOrderId;
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

        public PurchaseBeanBuilder setUserId(String userId){
            mUserId = userId;
            return this;
        }

        public PurchaseBeanBuilder setGoogleOrderId(String googleOrderId){
            mGoogleOrderId = googleOrderId;
            return this;
        }
        public PurchaseBeanBuilder setPackageName(String packageName){
            mPackageName = packageName;
            return this;
        }
        public PurchaseBeanBuilder setPurchaseToken(String purchaseToken){
            mPurchaseToken = purchaseToken;
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
