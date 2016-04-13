package zty.sdk.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.alipay.Base64;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.model.NativeAccountInfor;

public class TcardStorage {

	/**
	 * 保存账户数据到T卡
	 * 
	 * @param list
	 */
	public static void writeDataCache(ArrayList<NativeAccountInfor> list) {
		JSONArray jArray = getJsonArray(list);

		final String data = jArray.toString();
		Util_G.debug_i(Constants.TAG1, "存入sd卡的数据是：" + data);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
				String path = Constants.newDataPath;
				File file = new File(path);
				BufferedWriter bufw = null;
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fileWritter = new FileWriter(file);
					bufw = new BufferedWriter(fileWritter);
					bufw.write(data);
					bufw.close();
				} catch (Exception e) {
				}

//			}
//		}).start();
	}

	public static JSONArray getJsonArray(ArrayList<NativeAccountInfor> list) {
		JSONArray jArray = new JSONArray();
		if (list==null||list.size()==0) {
			return jArray;
		}
		for (NativeAccountInfor item : list) {
			JSONObject json = new JSONObject();
			try {
				json.put("indexnum", item.getIndexnum());
				json.put("usn", item.getUsn());
				json.put("psd", item.getPsd());
				json.put("bstatus", item.getBstatus());
				json.put("nstatus", item.getNstatus());
				String pnum = item.getPnum();
				// 需要加密
				pnum = Base64.encode((pnum+GameSDK.getOkInstance().packetId).getBytes());
				json.put("pnum", pnum);
				json.put("preferpayway", item.getPreferpayway());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jArray.put(json);
		}
		return jArray;
	}

	/**
	 * 读取本地存储的账户信息，若果文件不存在，读取老版本信息；若文件存在但是为空，就不再读取老版本文件
	 * @return
	 */
	public static ArrayList<NativeAccountInfor> getAccounts() {
		ArrayList<NativeAccountInfor> list = new ArrayList<NativeAccountInfor>();
		
		String ret = readAccountsStr();
		if (StringUtil.isEmpty(ret)) {
			return list;
		}
		try {
			JSONArray jsonArray = new JSONArray(ret);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				NativeAccountInfor item = new NativeAccountInfor(json);
				list.add(item);
			}
		} catch (JSONException e) {
			//兼容老版本
			XmlStorage xmlStorage = new XmlStorage(Constants.oldDataPath);
			NativeAccountInfor item = new NativeAccountInfor();
			item.setIndexnum(1);
			item.setUsn(xmlStorage.getString(Constants.LOGIN_ACCOUNT, ""));
			item.setPsd( xmlStorage.getString(Constants.PASSWORD, ""));
			item.setBstatus(xmlStorage.getString(Constants.BINDING_STATUS, ""));
			item.setNstatus(xmlStorage.getString(Constants.NOTICE_STATUS, ""));
			//同时获取用户上次支付方式
			XmlStorage localSharedPreferences = new XmlStorage(Constants.oldDataPath);
			String preferPayway = localSharedPreferences.getString(Constants.PAYWAY, "");
			item.setPreferpayway(preferPayway);
			list.add(item);
			Util_G.debug_i(Constants.TAG1, "读取老版本账号："+list.toString());
		}
		return list;
	}

	/**
	 * 从T卡中读取账号信息
	 * @return
	 */
	public static String readAccountsStr() {
		String ret = "";
		File file = new File(Constants.newDataPath);
		if (file.exists()) {
			ret = readFile(file);
		}else{
			file = new File(Constants.oldDataPath);
			if (file.exists())
				ret = readFile(file);
		}
		return ret;
	}

	public static String readFile(File file) {
		String temp;
		String ret = "";
		StringBuffer buf = new StringBuffer();
		BufferedReader bufr;
		try {
			bufr = new BufferedReader(new FileReader(file));
			while ((temp = bufr.readLine()) != null) {
				buf.append(temp);
			}
			ret = buf.toString();
			bufr.close();
			buf = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return ret;
	}
}
