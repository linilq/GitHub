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
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.utils.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 检测安全支付服务是否正确安装，如果没有安装进行本地安装，或者下载安装， 检测安全支付服务版本，有新版本时进行下载。
 */
@SuppressWarnings("ConstantConditions")
public class MobileSecurePayHelper {

    static final String TAG = "MobileSecurePayHelper";

    private ProgressDialog mProgress = null;
    final Context mContext;

    public MobileSecurePayHelper(Context context) {
        this.mContext = context;
    }

//    /**
//     * 检测安全支付服务是否安装
//     *
//     * @return boolean
//     */
//    public boolean detectMobile_sp() {
//        boolean isMobile_spExist = isMobile_spExist();
//        if (!isMobile_spExist) {
//            // 获取系统缓冲绝对路径 获取/data/data//cache目录
//            File cacheDir = mContext.getCacheDir();
//            final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
//            // 捆绑安装
//            retrieveApkFromJar(mContext, AlipayConstant.ALIPAY_PLUGIN_NAME,
//                    cachePath);
//
//            mProgress = BaseHelper.showProgress(mContext, null, "正在检测安全支付服务版本",
//                    false, true);
//
//            // 实例新线程检测是否有新版本进行下载
//            new Thread(new Runnable() {
//                public void run() {
//                    // 检测是否有新的版本。
//                    PackageInfo apkInfo = getApkInfo(mContext, cachePath);
//                    String newApkdlUrl = checkNewUpdate(apkInfo);
//                    // 动态下载
//                    if (newApkdlUrl != null)
//                        retrieveApkFromNet(mContext, newApkdlUrl, cachePath);
//                    // 发送结果
//                    Message msg = new Message();
//                    msg.what = AlixDefine.RQF_INSTALL_CHECK;
//                    msg.obj = cachePath;
//                    mHandler.sendMessage(msg);
//                }
//            }).start();
//        }
//        return isMobile_spExist;
//
//    }

    /**
     * 显示确认安装的提示
     *
     * @param context   上下文环境
     * @param cachePath 安装文件路径
     */
    public void showInstallConfirmDialog(final Context context,
                                         final String cachePath) {
        AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
        tDialog.setIcon(android.R.drawable.ic_dialog_info);
        tDialog.setTitle("安装提示");
        tDialog.setMessage("为保证您的交易安全，需要您安装支付宝安全支付服务，才能进行付款。点击确定，立即安装。");

        tDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BaseHelper.chmod("777", cachePath);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.parse("file://" + cachePath),
                                "application/vnd.android.package-archive");
                        context.startActivity(intent);
                    }
                });

        tDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        tDialog.show();

    }

    /**
     * 遍历程序列表，判断是否安装安全支付服务
     *
     * @return boolean
     */
    public boolean isMobile_spExist() {
        PackageManager manager = mContext.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (PackageInfo pkg : pkgList) {
            if (pkg.packageName.equalsIgnoreCase("com.alipay.android.app"))
                return true;
        }
        return false;
    }

    /**
     * 安装安全支付服务，安装assets文件夹下的apk
     *
     * @param context  上下文环境
     * @param fileName apk名称
     * @param path     安装路径
     * @return boolean
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean retrieveApkFromAssets(Context context, String fileName,
                                         String path) {
        boolean result = false;

        try {

            InputStream is = context.getAssets().open(fileName);

            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            byte[] temp = new byte[1024];
            int i;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            result = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean retrieveApkFromJar(Context context, String fileName,
                                      String path) {
        boolean result = false;

        try {

            InputStream is = ResourceLoader.getInputStream(fileName);

            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            byte[] temp = new byte[1024];
            int i;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            result = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * 获取未安装的APK信息
     *
     * @param context         context
     * @param archiveFilePath APK文件的路径。如：/sdcard/download/XX.apk
     */
    public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(archiveFilePath,
                PackageManager.GET_META_DATA);
    }

    /**
     * 检查是否有新版本，如果有，返回apk下载地址
     *
     * @param packageInfo {@link PackageInfo}
     * @return String
     */
    public String checkNewUpdate(PackageInfo packageInfo) {
        String url = null;

        try {
            JSONObject resp = sendCheckNewUpdate(packageInfo.versionName);
            if (resp.getString("needUpdate").equalsIgnoreCase("true")) {
                url = resp.getString("updateUrl");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * 发送当前版本信息，返回是否需要升级 如果需要升级返回更新apk地址
     *
     * @param versionName 当前版本号
     * @return JSONObject
     */
    public JSONObject sendCheckNewUpdate(String versionName) {
        JSONObject objResp = null;
        try {
            JSONObject req = new JSONObject();
            req.put(AlixDefine.action, AlixDefine.actionUpdate);

            JSONObject data = new JSONObject();
            data.put(AlixDefine.platform, "android");
            data.put(AlixDefine.VERSION, versionName);
            data.put(AlixDefine.partner, "");

            req.put(AlixDefine.data, data);

            objResp = sendRequest(req.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return objResp;
    }

    /**
     * 发送json数据
     *
     * @param content json
     * @return JSONObject
     */
    public JSONObject sendRequest(final String content) {

        NetworkManager nM = new NetworkManager(mContext);
        JSONObject jsonResponse = null;
        try {
            String response;
            synchronized (mContext) {
                response = nM.SendAndWaitResponse(content, AlipayConstant.server_url);
            }
            jsonResponse = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonResponse != null)
            BaseHelper.log(TAG, jsonResponse.toString());
        return jsonResponse;

    }

    /**
     * 动态下载apk
     *
     * @param context  上下文环境
     * @param strurl   下载地址
     * @param filename 文件名称
     * @return boolean
     */
    public boolean retrieveApkFromNet(Context context, String strurl,
                                      String filename) {
        boolean bRet = false;

        try {
            NetworkManager nM = new NetworkManager(this.mContext);
            bRet = nM.urlDownloadToFile(context, strurl, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bRet;
    }

    void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 此处接收安装检测结果
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case AlixDefine.RQF_INSTALL_CHECK: {
                        closeProgress();
                        String cachePath = (String) msg.obj;
                        showInstallConfirmDialog(mContext, cachePath);
                    }
                    break;
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
