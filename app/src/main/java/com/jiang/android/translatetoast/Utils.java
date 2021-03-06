package com.jiang.android.translatetoast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jiang.android.translatetoast.model.TranslateModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import static com.jiang.android.translatetoast.App.API_KEY;

public final class Utils {

    private final static String LOG_TAG = "uc-toast";

    public static String bundleToString(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    public static void printIntent(String tag, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            Log.d(LOG_TAG, String.format("%s, intent: %s", tag, intent));
            return;
        }

        Bundle bundle = intent.getExtras();
        Log.d(LOG_TAG, String.format("%s, intent: %s, %s", tag, intent, bundleToString(bundle)));
    }

    public static String getResultByModel(TranslateModel resultModel) {

        StringBuilder result = new StringBuilder();
        if (resultModel.getBasic() == null || resultModel.getBasic().getExplains() == null || resultModel.getBasic().getExplains().size() == 0) {
            if (resultModel.getTranslation() != null && resultModel.getTranslation().size() > 0) {
                result.append(resultModel.getTranslation().get(0));
            } else {
                result.append("暂无结果");
            }
            return result.toString();
        }
        int size = resultModel.getBasic().getExplains().size();
        if (size == 0) {
            result.append("暂无结果");
        } else {
            for (int i = 0; i < size; i++) {
                result.append(resultModel.getBasic().getExplains().get(i)).append(" \n");
            }
        }
        return result.substring(0, result.length() - 2);

    }

    public static boolean isNetworkConnected(Context ct) {
        ConnectivityManager cm = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


    // 将字符串写入到文本文件中
    public static boolean writeTxtToFile(Context context, String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(context, filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
            return true;
        } catch (Exception e) {
            showToast(context, e.toString());
            return false;
        }
    }

    // 生成文件
    public static File makeFilePath(Context context, String filePath, String fileName) {
        File file = null;
        makeRootDirectory(context, filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(Context context, String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            showToast(context, e.toString());
        }
    }

    public static String ReadTxtFile(Context context, String strFilePath) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            showToast(context, "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line;
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                showToast(context, "The File doesn't not exist.");
            } catch (IOException e) {
                showToast(context, e.getMessage());
            }
        }
        return content;
    }

    private static void showToast(final Context context, final String s) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static String getKey(Context context, String key) {
        String value = SharePrefUtil.getString(context.getApplicationContext(), key, API_KEY);
        if (TextUtils.isEmpty(value)) {
            return App.API_KEY;
        }
        return value;
    }

    public static String getForm(Context context, String key) {
        String value = SharePrefUtil.getString(context.getApplicationContext(), key, App.keyfrom);
        if (TextUtils.isEmpty(value)) {
            return App.keyfrom;
        }
        return value;
    }

}
