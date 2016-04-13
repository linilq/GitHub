/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package zty.sdk.alipay;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

/**
 * 网络连接工具类
 */
public class NetworkManager {

    static final String TAG = "NetworkManager";

    private int connectTimeout = 30 * 1000;
    private int readTimeout = 30 * 1000;

    Proxy mProxy = null;
    Context mContext;

    public NetworkManager(Context context) {
        this.mContext = context;
        setDefaultHostnameVerifier();
    }

    private void detectProxy() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null) {
                final InetSocketAddress sa = new InetSocketAddress(proxyHost,
                        port);
                mProxy = new Proxy(Proxy.Type.HTTP, sa);
            }
        }
    }

    private void setDefaultHostnameVerifier() {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    /**
     * 发送和接收数据
     *
     * @param strReqData 请求数据
     * @param strUrl     请求地址
     * @return String
     */
    public String SendAndWaitResponse(String strReqData, String strUrl) {

        detectProxy();

        String response = null;
        ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("requestData", strReqData));

        HttpURLConnection httpConnect = null;
        UrlEncodedFormEntity p_entity;
        try {

            p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
            URL url = new URL(strUrl);

            if (mProxy != null) {
                httpConnect = (HttpURLConnection) url.openConnection(mProxy);
            } else {
                httpConnect = (HttpURLConnection) url.openConnection();
            }
            httpConnect.setConnectTimeout(connectTimeout);
            httpConnect.setReadTimeout(readTimeout);
            httpConnect.setDoOutput(true);
            httpConnect.addRequestProperty("Content-type",
                    "application/x-www-form-urlencoded;charset=utf-8");

            httpConnect.connect();

            OutputStream os = httpConnect.getOutputStream();
            p_entity.writeTo(os);
            os.flush();

            InputStream content = httpConnect.getInputStream();
            response = BaseHelper.convertStreamToString(content);
            BaseHelper.log(TAG, "response " + response);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpConnect != null) httpConnect.disconnect();
        }

        return response;

    }

    /**
     * 下载文件
     *
     * @param context 上下文环境
     * @param strurl  下载地址
     * @param path    下载路径
     * @return boolean
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean urlDownloadToFile(Context context, String strurl, String path) {

        boolean result = false;

        detectProxy();

        try {

            URL url = new URL(strurl);
            HttpURLConnection conn = null;
            if (mProxy != null) {
                conn = (HttpURLConnection) url.openConnection(mProxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoInput(true);

            conn.connect();
            InputStream is = conn.getInputStream();

            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            byte[] temp = new byte[1024];
            int i = 0;
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

}
