package com.ym.game.plugin.google.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LocalPurchaseBean {

    @Id(autoincrement = true)
    public Long id;

    @NotNull
    public String uid;
    public String gameOrderId;
    public String googleOrderId;
    public String purchaseToken;
    public String googleAccountId;
    public String productId;
    public String packageName;
    //0代表消费成功，1发起支付，2查询补单付款完成未消费,3代表消费失败
    public int purchaseState;
    public long purchaseTime;
    @Generated(hash = 155425497)
    public LocalPurchaseBean(Long id, @NotNull String uid, String gameOrderId,
            String googleOrderId, String purchaseToken, String googleAccountId,
            String productId, String packageName, int purchaseState,
            long purchaseTime) {
        this.id = id;
        this.uid = uid;
        this.gameOrderId = gameOrderId;
        this.googleOrderId = googleOrderId;
        this.purchaseToken = purchaseToken;
        this.googleAccountId = googleAccountId;
        this.productId = productId;
        this.packageName = packageName;
        this.purchaseState = purchaseState;
        this.purchaseTime = purchaseTime;
    }
    @Generated(hash = 1621016821)
    public LocalPurchaseBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getGameOrderId() {
        return this.gameOrderId;
    }
    public void setGameOrderId(String gameOrderId) {
        this.gameOrderId = gameOrderId;
    }

    public String getPurchaseToken() {
        return this.purchaseToken;
    }
    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getProductId() {
        return this.productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public long getPurchaseTime() {
        return this.purchaseTime;
    }
    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }
    public int getPurchaseState() {
        return this.purchaseState;
    }
    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }
    public String getGoogleOrderId() {
        return this.googleOrderId;
    }
    public void setGoogleOrderId(String googleOrderId) {
        this.googleOrderId = googleOrderId;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getGoogleAccountId() {
        return this.googleAccountId;
    }
    public void setGoogleAccountId(String googleAccountId) {
        this.googleAccountId = googleAccountId;
    }

}
