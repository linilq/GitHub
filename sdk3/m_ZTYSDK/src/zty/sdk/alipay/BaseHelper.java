/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package zty.sdk.alipay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 工具类
 */
public class BaseHelper {

    /**
     * 流转字符串方法
     *
     * @param is InnputStream
     * @return String
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 显示dialog
     *
     * @param context  环境
     * @param strTitle 标题
     * @param strText  内容
     * @param icon     图标
     */
    public static AlertDialog.Builder showDialog(Context context, String strTitle,
                                                 String strText, int icon, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
        tDialog.setIcon(icon);
        tDialog.setTitle(strTitle);
        tDialog.setMessage(strText);
        tDialog.setPositiveButton("确定", listener);
        tDialog.show();
        return tDialog;

    }

    /**
     * 打印信息
     *
     * @param tag  标签
     * @param info 信息
     */
    public static void log(String tag, String info) {
        Log.d(tag, info);
    }

    /**
     * 获取权限
     *
     * @param permission 权限
     * @param path       路径
     */
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    // show the progress bar.

    /**
     * 显示进度条
     *
     * @param context       环境
     * @param title         标题
     * @param message       信息
     * @param indeterminate 确定性
     * @param cancelable    可撤销
     * @return ProgressDialog
     */
    public static ProgressDialog showProgress(Context context,
                                              CharSequence title, CharSequence message, boolean indeterminate,
                                              boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(false);
        dialog.setOnCancelListener(null);

        dialog.show();
        return dialog;
    }

    /**
     * 字符串转json对象
     *
     * @param str   string
     * @param split split
     * @return JSONObject
     */
    public static JSONObject string2JSON(String str, String split) {

        JSONObject json = new JSONObject();
        try {
            String[] arrStr = str.split(split);
            for (String anArrStr : arrStr) {
                String[] arrKeyValue = anArrStr.split("=");
                json.put(arrKeyValue[0],
                        anArrStr.substring(arrKeyValue[0].length() + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;

    }
}