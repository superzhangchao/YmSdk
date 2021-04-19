package com.ym.game.sdk.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YmSignUtils {


    // =
    public static final String QSTRING_EQUAL = "=";
    // &
    public static final String QSTRING_SPLIT = "&";
    private static String TAG = "YmSdk";


    public static String getYmSign(Map<String,String> para, String key) {
        // 除去数组中的空值和签名参数
        Map<String, String> filteredReq = paraFilter(para);

        String prestr = createLinkString(filteredReq, true, false);	//得到待签名字符串 需要对map进行sort，不需要对value进行URL编码
        String signstr = prestr + key;
        return MD5Utils.getMD5String(signstr).toLowerCase();
    }

    /**
     * 除去请求要素中的空值和签名参数
     * @param para 请求要素
     * @return 去掉空值的请求要素
     */

    public static Map<String, String> paraFilter(Map<String, String> para) {

        Map<String, String> result = new HashMap<String, String>();

        if (para == null || para.size() <= 0) {
            return result;
        }

        for (String key : para.keySet()) {
            String value = para.get(key);
            if (value == null || value.equals("")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }


    /**
     * 把请求要素按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param para 请求要素
     * @param sort 是否需要根据key值作升序排列
     * @param encode 是否需要URL编码
     * @return 拼接成的字符串
     */
    public static String createLinkString(Map<String, String> para, boolean sort, boolean encode) {

        List<String> keys = new ArrayList<String>(para.keySet());

        if (sort){
            Collections.sort(keys);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = para.get(key);

            if (encode) {
                try {

                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                }
            }

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                sb.append(key).append(QSTRING_EQUAL).append(value);
            } else {
                sb.append(key).append(QSTRING_EQUAL).append(value).append(QSTRING_SPLIT);
            }
        }
        return sb.toString();
    }
}
