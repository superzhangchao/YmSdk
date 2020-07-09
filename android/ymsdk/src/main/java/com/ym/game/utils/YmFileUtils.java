package com.ym.game.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class YmFileUtils {
    public static String ym_uuid = "";
    public static synchronized String getUUid(Context ctx) {
        if (!TextUtils.isEmpty(ym_uuid) && ym_uuid.length() > 30) {
            return ym_uuid;
        } else if (isAccess()) {
            String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dataPath = sdcardPath + File.separator + "Android" + File.separator + "data" + File.separator;
            String ymdataPathDir = dataPath + "clg" + File.separator;
            File dir = new File(ymdataPathDir);
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("Ymsdk", "没有添加android.permission.WRITE_EXTERNAL_STORAGE权限?");
            }

            if (dir.exists() && dir.isDirectory()) {
                String ymdataPath = ymdataPathDir + "ymdata-uuid.txt";

                try {
                    ym_uuid = readFile(ymdataPath);
                    if (!TextUtils.isEmpty(ym_uuid)) {
                        return ym_uuid;
                    }

                    String uuid = UUID.randomUUID().toString();
                    if (writeFileData(ctx, ymdataPath, uuid)) {
                        ym_uuid = uuid;
                    }
                } catch (IOException e) {
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
        return readFile((Context)null, (String)fileName);
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

        while((data = is.readLine()) != null) {
            content.append(data);
        }

        is.close();
        inputStream.close();
        return content.toString();
    }

    public static boolean writeFileData(String filePath, String data) throws IOException {
        File f = new File(filePath);
        return writeFileData((Context)null, (File)f, data);
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
}
