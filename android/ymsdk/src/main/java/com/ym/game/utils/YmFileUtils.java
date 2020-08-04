package com.ym.game.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.RequiresApi;

public class YmFileUtils {
    public static String ym_uuid = "";

    public static synchronized String getUUid(Context ctx) {
        String key = "c4869d06";
        if (!TextUtils.isEmpty(ym_uuid) && ym_uuid.length() > 30) {
            return ym_uuid;
        } else if (isAccess()) {
            String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dataPath = sdcardPath + File.separator + "Android" + File.separator + "data" + File.separator;
            String ymdataPathDir = dataPath + "ym" + File.separator;
            File dir = new File(ymdataPathDir);

            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("Ymsdk", "没有添加android.permission.WRITE_EXTERNAL_STORAGE权限?");
            }

            if (dir.exists() && dir.isDirectory()) {
                String ymdataPath = ymdataPathDir + "ymdata-uuid.txt";

                try {
                    ym_uuid = readFile(ymdataPath);
                    if (!TextUtils.isEmpty(ym_uuid)) {
                        try {
//                            return decryptDES(ym_uuid,key);
                            return ym_uuid;
                        } catch (Exception e) {
                            e.printStackTrace();
                            ym_uuid ="";
                        }

                    }

                    String uuid = UUID.randomUUID().toString();
//                    uuid = encryptDES(uuid,key);
                    if (writeFileData(ctx, ymdataPath, uuid)) {
                        ym_uuid = uuid;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ym_uuid;
            } else {
                return "";
            }
        } else {
            return "";
        }

    }

    public static boolean isExternalStorageExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean isAccess() {
        return isExternalStorageExist();
    }

    public static String readFile(String fileName) throws IOException {
        return readFile((Context) null, (String) fileName);
    }

    public static String readFile(Context context, String fileName) throws IOException {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        } else {
            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                return readFile(context, inputStream);
            } else {
                return "";
            }
        }
    }

    public static String readFile(Context context, FileInputStream inputStream) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(inputStream));
        String data = "";
        StringBuffer content = new StringBuffer();

        while ((data = is.readLine()) != null) {
            content.append(data);
        }

        is.close();
        inputStream.close();
        return content.toString();
    }

    public static boolean writeFileData(String filePath, String data) throws IOException {
        File f = new File(filePath);
        return writeFileData((Context) null, (File) f, data);
    }

    public static boolean writeFileData(Context context, String filePath, String data) throws IOException {
        File f = new File(filePath);
        return writeFileData(context, f, data);
    }

    public static boolean writeFileData(Context context, File f, String data) throws IOException {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        OutputStream os = new FileOutputStream(f);
        os.write(data.getBytes());
        os.flush();
        os.close();
        return true;
    }

    private static byte[] iv = { 97, 98, 99, 100, 101, 1, 2, 42 };

    public static String encryptDES(String encryptText, String encryptKey) throws Exception{
        IvParameterSpec spec = new IvParameterSpec(iv);
        encryptKey = encryptKey.substring(0, 8);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        cipher.init(1, key, spec);

        byte[] encryptData = cipher.doFinal(encryptText.getBytes());

        return Base64.encodeToString(encryptData, 2);
    }

    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        decryptKey = decryptKey.substring(0, 8);
        byte[] base64byte = Base64.decode(decryptString, 2);

        IvParameterSpec spec = new IvParameterSpec(iv);

        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        cipher.init(2, key, spec);

        byte[] decryptedData = cipher.doFinal(base64byte);

        return new String(decryptedData);
    }
}
