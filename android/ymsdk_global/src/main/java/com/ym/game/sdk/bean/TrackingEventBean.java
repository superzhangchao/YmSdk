package com.ym.game.sdk.bean;

public class TrackingEventBean {
    public String mEventName;
    public String mEventToken;
    public String mRoleId;
    public String mRoleName;
    public String mRoleLevel;
    public String mProductName;
    public String mProductId;
    public String mCurrency;
    public String mOrderId;
    public double mPrice;

    private TrackingEventBean(TrackingEventBean.TrackingEventBeanBuilder builder) {
        mEventName = builder.mEventName;
        mEventToken = builder.mEventToken;
        mRoleId = builder.mRoleId;
        mRoleName = builder.mRoleName;
        mRoleLevel = builder.mRoleLevel;
        mProductName = builder.mProductName;
        mProductId = builder.mProductId;
        mCurrency = builder.mCurrency;
        mOrderId = builder.mOrderId;
        mPrice = builder.mPrice;

    }

    public static class TrackingEventBeanBuilder {
        public String mEventName;
        public String mEventToken;
        public String mRoleId;
        public String mRoleName;
        public String mRoleLevel;
        public String mProductName;
        public String mProductId;
        public String mCurrency;
        public String mOrderId;
        public double mPrice;

        public TrackingEventBeanBuilder() {

        }

        public TrackingEventBean.TrackingEventBeanBuilder setEventName(String eventName) {
            mEventName = eventName;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setEventToken(String eventToken) {
            mEventToken = eventToken;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setRoleId(String roleId) {
            mRoleId = roleId;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setRoleName(String roleName) {
            mRoleName = roleName;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setRoleLevel(String roleLevel) {
            mRoleLevel = roleLevel;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setProductId(String productId) {
            mProductId = productId;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setProductName(String productName) {
            mProductName = productName;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setCurrency(String currency) {
            mCurrency = currency;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setOrderId(String orderId) {
            mOrderId = orderId;
            return this;
        }

        public TrackingEventBean.TrackingEventBeanBuilder setPrice(double price) {
            mPrice = price;
            return this;
        }

        public TrackingEventBean build() {
            return new TrackingEventBean(this);
        }

    }
}