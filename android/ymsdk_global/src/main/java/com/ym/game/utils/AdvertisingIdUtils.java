package com.ym.game.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

public class AdvertisingIdUtils {
    private static volatile String advertisingId = "";

    public AdvertisingIdUtils() {
    }

    public static void initialAdvertisingId(final Context cxt) {
        (new Thread() {
            public void run() {
                try {
                   AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(cxt);
                    AdvertisingIdUtils.advertisingId = adInfo.getId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static String getAdvertisingId(Context mContext) {
        if (!TextUtils.isEmpty(advertisingId)) {
            return advertisingId;
        } else {
            return "";
        }
    }
}
