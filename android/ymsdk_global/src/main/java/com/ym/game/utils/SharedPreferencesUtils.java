package com.ym.game.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ym.game.sdk.bean.HistoryAccountBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * SharedPreferences的一个工具类
 * 使用方法：
 * 调用setParam就能保存String, Integer, Boolean, Float, Long类型的参数
 * SharedPreferencesUtils.setParam(this,"spfilename", "String", "xiaonming");
 * SharedPreferencesUtils.setParam(this,"spfilename", "int", 10);
 * SharedPreferencesUtils.setParam(this,"spfilename", "boolean", true);
 * SharedPreferencesUtils.setParam(this,"spfilename", "long", 100L);
 * SharedPreferencesUtils.setParam(this,"spfilename", "float", 1.1f);
 * <p>
 * 同样调用getParam就能获取到保存在手机里面的数据
 * SharedPreferencesUtils.getParam(Activity.this,"spfilename", "String", "");
 * SharedPreferencesUtils.getParam(Activity.this,"spfilename", "int", 0);
 * SharedPreferencesUtils.getParam(Activity.this,"spfilename", "boolean", false);
 * SharedPreferencesUtils.getParam(Activity.this,"spfilename", "long", 0L);
 * SharedPreferencesUtils.getParam(Activity.this,"spfilename", "float", 0.0f);
 *
 *
 */
public class SharedPreferencesUtils {
    private static final String TAG = "SharedPreferencesUtils";
    /**
     * 保存在手机里面的文件名
     */

    private static SharedPreferences sp;

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     * @param ：SharedPreferencesUtils.setParam(this, "key", "value");
     *                                               key -- userid / accountId obj==
     */
    public static void setParam(Context context, String filename,String key, Object object) {
        String type = "String";
        if (object != null) {
            type = object.getClass().getSimpleName();
        }
        SharedPreferences sp = context.getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type) || "int".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type) || "boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type) || "float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type) || "long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key                                             关键字
     * @param defaultObject                                   若取回空值则返回此默认值
     * @param ：SharedPreferencesUtils.getParam(Activity.this, "key", "defaultValue");
     * @return
     */
    public static Object getParam(Context context,String filename, String key, Object defaultObject) {
        String type = "String";
        if (defaultObject != null) {
            type = defaultObject.getClass().getSimpleName();
        }
        SharedPreferences sp = context.getSharedPreferences(filename, MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type) || "int".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type) || "boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type) || "float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type) || "long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    //删除指定的key
    public static void removeParam(Context context,String filename, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();


    }

    /**
     * 4.存储历史账号的list
     */
    public static void putHistoryAccountBean(Context context,String filename, List<HistoryAccountBean> phoneList, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(filename, MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(phoneList);
        editor.putString(key, json);
        editor.commit();
    }


    /**
     * 读取历史账号的list
     */
    public static List<HistoryAccountBean> getHistoryAccountBean(Context context, String filename, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(filename, MODE_PRIVATE);
        }
        Gson gson = new Gson();
        String json = sp.getString(key, null);

        Log.e(TAG, "getHistoryAccountBean: json   >>>   " + json);
        Type type = new TypeToken<List<HistoryAccountBean>>() {}.getType();
        List<HistoryAccountBean> arrayList = gson.fromJson(json, type);
        return arrayList;
    }




    //缓存map集合
    public static void putHashMapData(Context context,String fileName, String key, Map<String, Object> datas) {
        JSONArray mJsonArray = new JSONArray();
        Iterator<Map.Entry<String, Object>> iterator = datas.entrySet().iterator();

        JSONObject object = new JSONObject();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {

            }
        }
        mJsonArray.put(object);

        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, mJsonArray.toString());
        editor.commit();
    }

    //获取map缓存数据
    public static Map<String, Object> getHashMapData(Context context,String fileName, String key) {

        Map<String, Object> datas = new HashMap<>();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        datas.put(name, value);
                    }
                }
            }
        } catch (JSONException e) {

        }

        return datas;
    }

    //存数据到SD卡里面
    public static void storetosd(File file, List<HistoryAccountBean> data) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            OutputStream os = new FileOutputStream(file);
            os.write(json.getBytes("utf-8"));
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取SD卡里面的数据
    public static List<HistoryAccountBean> readbysd(File file) {
        List<HistoryAccountBean> arrayList = null;
        Gson gson = new Gson();
        try {
            InputStream is = new FileInputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            String content = new String(data, "utf-8");
            Type type = new TypeToken<List<HistoryAccountBean>>() {
            }.getType();
            arrayList = gson.fromJson(content, type);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }



}









