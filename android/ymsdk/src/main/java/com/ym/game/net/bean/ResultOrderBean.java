package com.ym.game.net.bean;

import com.google.gson.annotations.SerializedName;

public class ResultOrderBean {


    /**
     * code : 0
     * message :
     * data : {"pf_order_no":"201223479234","type":"alipay/wxpay","alipay":{"info":""},"wxpay":{"appId":"wxd930ea5d5a258f4f","partnerId":"1900000109","prepayId":"1101000000140415649af9fc314aa427","packageValue":"Sign=WXPay","nonceStr":"1101000000140429eb40476f8896f4c9","timeStamp":"1398746574","sign":"7FFECB600D7157C5AA49810D2D8F28BC2811827B"}}
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
         * pf_order_no : 201223479234
         * type : alipay/wxpay
         * alipay : {"info":""}
         * wxpay : {"appId":"wxd930ea5d5a258f4f","partnerId":"1900000109","prepayId":"1101000000140415649af9fc314aa427","packageValue":"Sign=WXPay","nonceStr":"1101000000140429eb40476f8896f4c9","timeStamp":"1398746574","sign":"7FFECB600D7157C5AA49810D2D8F28BC2811827B"}
         */

        private String pf_order_no;
        private String type;
        private AlipayBean alipay;
        private WxpayBean wxpay;

        public String getPf_order_no() {
            return pf_order_no;
        }

        public void setPf_order_no(String pf_order_no) {
            this.pf_order_no = pf_order_no;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public AlipayBean getAlipay() {
            return alipay;
        }

        public void setAlipay(AlipayBean alipay) {
            this.alipay = alipay;
        }

        public WxpayBean getWxpay() {
            return wxpay;
        }

        public void setWxpay(WxpayBean wxpay) {
            this.wxpay = wxpay;
        }

        public static class AlipayBean {
            /**
             * info :
             */

            private String info;

            public String getInfo() {
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }
        }

        public static class WxpayBean {
            /**
             * appId : wxd930ea5d5a258f4f
             * partnerId : 1900000109
             * prepayId : 1101000000140415649af9fc314aa427
             * packageValue : Sign=WXPay
             * nonceStr : 1101000000140429eb40476f8896f4c9
             * timeStamp : 1398746574
             * sign : 7FFECB600D7157C5AA49810D2D8F28BC2811827B
             */

            @SerializedName("app_id")
            private String wxAppId;

            @SerializedName("partner_id")
            private String partnerId;

            @SerializedName("prepay_id")
            private String prepayId;

            @SerializedName("package")
            private String packageValue;

            @SerializedName("nonce_str")
            private String nonceStr;

            @SerializedName("time_stamp")
            private String timeStamp;

            @SerializedName("sign")
            private String wxSign;

            public String getWxAppId() {
                return wxAppId;
            }

            public void setWxAppId(String wxAppId) {
                this.wxAppId = wxAppId;
            }

            public String getPartnerId() {
                return partnerId;
            }

            public void setPartnerId(String partnerId) {
                this.partnerId = partnerId;
            }

            public String getPrepayId() {
                return prepayId;
            }

            public void setPrepayId(String prepayId) {
                this.prepayId = prepayId;
            }

            public String getPackageValue() {
                return packageValue;
            }

            public void setPackageValue(String packageValue) {
                this.packageValue = packageValue;
            }

            public String getNonceStr() {
                return nonceStr;
            }

            public void setNonceStr(String nonceStr) {
                this.nonceStr = nonceStr;
            }

            public String getTimeStamp() {
                return timeStamp;
            }

            public void setTimeStamp(String timeStamp) {
                this.timeStamp = timeStamp;
            }

            public String getWxSign() {
                return wxSign;
            }

            public void setWxSign(String wxSign) {
                this.wxSign = wxSign;
            }
        }
    }
}
