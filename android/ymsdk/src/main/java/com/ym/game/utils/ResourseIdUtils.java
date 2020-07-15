package com.ym.game.utils;

import android.content.Context;

import com.ym.game.sdk.config.ApplicationCache;


public class ResourseIdUtils {
    public static int getId(String name) {
        return getResourceIdByName("id", name);
    }

    public static int getStyleByName(String name) {
        return getResourceIdByName("style", name);
    }

    public static int getStringId(String name) {
        return getResourceIdByName("string", name);
    }

    public static int getColorId(String name) {
        return getResourceIdByName("color", name);
    }

    public static int getStringArrayId(String name) {
        return getResourceIdByName("array", name);
    }

    public static int getDimenId(String dimenName) {
        return getResourceIdByName("dimen", dimenName);
    }

    public static int getDrawableId(String name) {
        return getResourceIdByName("drawable", name);
    }
    public static int getMipmapId(String name) {
        return getResourceIdByName("mipmap", name);
    }
    public static int getLayoutId(String name) {
        return getResourceIdByName("layout", name);
    }

    public static int getAnimId(String name) {
        return getResourceIdByName("anim", name);
    }

    public static int getResourceIdByName(String className, String name) {
        int id = 0;
        try {
            Context context = ApplicationCache.getInstance().getApplicationContext();
            String packageName = context.getPackageName();
//            return context.getResources().getIdentifier(layoutName,"layout",context.getPackageName());

            id = context.getResources().getIdentifier(name, className, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
