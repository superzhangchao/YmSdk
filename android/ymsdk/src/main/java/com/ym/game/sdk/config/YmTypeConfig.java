package com.ym.game.sdk.config;

import com.ym.game.sdk.common.base.config.TypeConfig;

public class YmTypeConfig extends TypeConfig {
    /**************************** 事件类型 *****************************/

    //账号事件配置
    public static final int REALNAME = 106;  //实名
    public static final int SETPASSWORD = 107;  //设置密码
    public static final int COMMONPWDPAGE = 108;  //普通密码页
    public static final int QUICKPWDPAGE = 109;  //快速密码页


    //购买事件配置
    public static final int PAY = 150;  // 支付
    public static final int SUBS = 151;  // 订阅


    //是否支持该功能接口类型
    public static final int FUNC_SWITCHACCOUNT = 250;  //切换
    public static final int FUNC_LOGOUT = 251;  //注销
    public static final int FUNC_SHOW_FLOATWINDOW = 252;  //显示浮窗
    public static final int FUNC_DISMISS_FLOATWINDOW = 253;  //隐藏浮窗
}
