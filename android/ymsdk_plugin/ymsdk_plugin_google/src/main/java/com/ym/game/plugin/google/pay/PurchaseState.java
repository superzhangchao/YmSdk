package com.ym.game.plugin.google.pay;

public interface PurchaseState {
    // CONSUMESUCCESS 0代表消费成功，PURCHASING 1发起支付，NOCONSUME 2查询补单付款完成未消费,CONSUMEFAIL 3代表消费失败
    public static final int CONSUMESUCCESS = 0;
    public static final int PURCHASING = 1;
    public static final int NOCONSUME = 2;
    public static final int CONSUMEFAIL = 3;

}

