package com.ym.game.sdk.config;

import android.content.Context;
import android.content.pm.PackageManager;



public class Config {
    private static boolean isInit;
    private static String gameId;
    public final static String Baseurl = "http://api.ysfj.loologames.com";


    public static String getGameId() {
        return gameId;
    }

    public static void setGameId(String gameId) {
        Config.gameId = gameId;
        Config.isInit = true;
    }

    public static boolean isInitPlatform(){
        return isInit;
    }


    /**
     * 渠道id
     * @return
     */
    public static String getPromote(Context context){
        String channel = "0";
        try {
            channel = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("gm_channel","0");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (channel == null){
            channel = "0";
        }
        return channel;
    }

}
