package zty.sdk.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.model.DeviceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class DeviceInfoUtil {

	public static final String ZTY_PACKAGE_ID = "1";// SHOUMENG_PACKAGE_ID//lsl

	public static boolean is_network(Context context) {
		boolean ret = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();// 获取网络的连接情况
			if (networkInfo != null) {
				ret = true;
			}

		}
		return ret;
	}

	public static String getGprsIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	public static String getWifiIpAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		int ipAddress = info.getIpAddress();

		return intToIp(ipAddress);
	}

	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	public static String getOutTradeNo(int count) {
		SimpleDateFormat format = new SimpleDateFormat("ssmm");// HHddMM
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();

		int ran = r.nextInt();
		if (ran < 0) {
			ran = 0 - ran;
		}
		key += ran;

		key = key.substring(0, count);
		// key = "010217153010087";//lsl
		// Log.d(TAG, "outTradeNo: " + key);
		return key;
	}

	public static String getIMEI(String head)// getIMEI
	{
		String ret = "";
		ret = head + getOutTradeNo(15 - head.length());// 9//"863590"
		return ret;
	}

	public static DeviceInfo getDeviceInfo(Context context) {

		if (context == null)
			return null;

		try {

			DeviceInfo info = new DeviceInfo();

			// lsl
			info.setPackageId(ZTY_PACKAGE_ID);// info.setPackageId(MetaDataUtil.getInt(context,SHOUMENG_PACKAGE_ID,0));
			info.setPlatform(1); // 1 for Android

			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = (wifiMgr == null ? null : wifiMgr
					.getConnectionInfo());
			if (wifiInfo != null) {
				info.setMac(wifiInfo.getMacAddress());
			}

			String ip = "";

			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager != null) {
				if (GameSDK.getOkInstance().IsZG.equals("1")) {
					System.out.println("这是智光的专属包体，看看设备号是多少:");
					String imei = getIMEI("863590");
					info.setImei(imei);
					// telephonyManager.get
					System.out.println(imei);
					System.out.println("这是智光的专属包体，看看设备号是多少");
				} else {
					info.setImei(telephonyManager.getDeviceId());
				}

				info.setImsi(getIMSI(context));
				switch (telephonyManager.getNetworkType()) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_IDEN:
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					info.setNetworkType(2);
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_UMTS:
					info.setNetworkType(3);
					break;
				default:
					info.setNetworkType(5);
					break;
				}
				String proxyHost = android.net.Proxy.getDefaultHost();
				if (proxyHost != null)
					info.setNetworkType(4); // Wap上网
			}

			info.setModel(Build.MODEL);
			info.setOsVersion(Build.VERSION.SDK_INT);

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo networkInfo = connectivityManager
						.getActiveNetworkInfo();// 获取网络的连接情况
				if (networkInfo != null) {
					if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						info.setNetworkType(1);
					}
				}

			}

			if (info.getNetworkType() != 1) {
				ip = getGprsIpAddress();
			} else if (info.getNetworkType() == 1) {
				ip = getWifiIpAddress(context);
			}

			info.setIp(ip);

			WindowManager windowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			if (windowManager != null) {
				Display display = windowManager.getDefaultDisplay();
				info.setScreenWidth(display.getWidth());
				info.setScreenHeight(display.getHeight());
			}

			return info;

		} catch (Exception ex) {
			return null;
		}

	}

	public static String CMCC = "cmcc";// 移动
	public static String UNION = "unicom";// 联通
	public static String CT = "ct";// 电信

	/**
	 * 通手机卡 获取网络类型
	 * 
	 * @param imsi
	 * @return
	 */
	public static String getSIMType(String imsi)// mmpay_tcpay
	{
		int i = -1001;
		String cardType = "";
		String token = "_";

		String str = "";
		if ((imsi != null) && imsi.length() > 5) {
			str = imsi.substring(0, 5);
		}
		if ((str.equals("46000")) || (str.equals("46002"))
				|| (str.equals("46007"))) {
			cardType = CMCC;
		} else if (str.equals("46001")) {
			cardType = UNION;

		} else if (str.equals("46003") || str.equals("46005")
				|| str.equals("46011"))// "46003", "46005", "46011"
		{
			cardType = CT;

		}

		return cardType;

	}

	public static String getNetworkType(Context context) {
		String netType = CT;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					Util_G.debug_i(Constants.TAG1, "networkType is wifi!!");
					netType = CT;
				}else {
					String imsi = getIMSI(context);
					netType = getSIMType(imsi);
					Util_G.debug_i(Constants.TAG1, "networkType is :" + netType);
				}
			}
		} 
		return netType;
	}
}
