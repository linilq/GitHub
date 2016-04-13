package zty.sdk.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.alipay.Rsa;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.http.AlipayOrderInfoHttpCb;
import zty.sdk.http.HttpRequest;
import zty.sdk.http.MzPayHttpCb;
import zty.sdk.http.OnlyPayHttpCb;
import zty.sdk.http.TenpayOrderInfoHttpCb;
import zty.sdk.http.UnionpayOrderInfoHttpCb;
import zty.sdk.http.WeChatOrderInfoHttpCb;
import zty.sdk.http.YeepayCardResultHttpCb;
import zty.sdk.model.AlipayOrderInfo;
import zty.sdk.model.MzPayInfo;
import zty.sdk.model.OnlyPayInfo;
import zty.sdk.model.PayObject;
import zty.sdk.model.TenpayOrderInfo;
import zty.sdk.model.UnionpayOrderInfo;
import zty.sdk.model.WeiXinOrderInfo;
import zty.sdk.model.YeepayCardResult;
import zty.sdk.paeser.AlipayOrderInfoParser;
import zty.sdk.paeser.MzpayInfoPaser;
import zty.sdk.paeser.OnlypayInfoPaser;
import zty.sdk.paeser.TenpayOrderInfoParser;
import zty.sdk.paeser.UnionpayOrderInfoParser;
import zty.sdk.paeser.WeiXinOrderInfoParser;
import zty.sdk.paeser.YeepayCardResultParser;
import android.app.Activity;
import android.os.Handler;

public class PayManager {
	// { "alipay", "tenpay", "unionpay", "yeemo", "yeegc", "mzpay",
	// "onlypay", "wxpay" };
	/**
	 * 阿里支付
	 */
	public static String PAYWAY_ALI = "alipay";
	/**
	 * 财付通支付
	 */
	public static String PAYWAY_TEN = "tenpay";
	/**
	 * 银联支付
	 */
	public static String PAYWAY_UNION = "unionpay";
	/**
	 * 微信支付
	 */
	public static String PAYWAY_WECHAT = "wxpay";
	
	/**
	 * 中国移动话费卡支付
	 */
	public static String PAYWAY_CHINA_MOBILE = "china_mobile";
	/**
	 * 中国电信话费卡支付
	 */
	public static String PAYWAY_CHINA_TELECOM = "china_telecom";
	/**
	 * 中国联通话费卡支付
	 */
	public static String PAYWAY_CHINA_UNICOM = "china_unicom";
	/**
	 * 骏网一卡通支付
	 */
	public static String PAYWAY_YEE_JCARD = "jcard";
	/**
	 * 纵游一卡通支付
	 */
	public static String PAYWAY_YEE_ZYCARD = "zycard";
	/**
	 * 32卡支付
	 */
	public static String PAYWAY_YEE_TSCARD = "tscard";
	/**
	 * 拇指币支付
	 */
	public static String PAYWAY_MZ = "mzpay";
	/**
	 * 专属币支付
	 */
	public static String PAYWAY_ONLY = "onlypay";

	/**
	 * 易宝游戏点卡支付
	 */
	public static String PAYWAY_YEE_GAMECARD = "yeegc";
	/**
	 * 易宝手机点卡
	 */
	public static String PAYWAY_YEE_MOBILECARD = "yeemb";

	private static String[] allPayWays = { PayManager.PAYWAY_ALI,
			PayManager.PAYWAY_TEN, PayManager.PAYWAY_UNION,
			PayManager.PAYWAY_YEE_MOBILECARD, PayManager.PAYWAY_YEE_GAMECARD,
			PayManager.PAYWAY_MZ, PayManager.PAYWAY_ONLY,
			PayManager.PAYWAY_WECHAT };
	private static String[] allPayNames = { "支付宝", "财付通", "银行卡", "手机充值卡",
			"游戏点卡", "拇指币", "专属币", "微信支付", };

	private static String[] mobileCardWays = { PayManager.PAYWAY_CHINA_MOBILE,
			PayManager.PAYWAY_CHINA_UNICOM, PayManager.PAYWAY_CHINA_TELECOM };
	private static String[] mobileCardNames = { "中国移动", "中国联通", "中国电信" };

	private static String[] gameCardWays = { PayManager.PAYWAY_YEE_JCARD,
			PayManager.PAYWAY_YEE_ZYCARD, PayManager.PAYWAY_YEE_TSCARD };
	private static String[] gameCardNames = { "骏网一卡通", "纵游一卡通", "32卡" };

	/**
	 * 
	 * @param isMzCharge
	 *            true为充值拇指币；false为正常充值
	 * @param payway
	 *            支付方式
	 * @param sdk
	 * @param activity
	 * @param handler
	 *            当是支付宝，易宝支付时才需要传，通常为null
	 * @param object
	 *            当是易宝支付时才会传入非null的object;是一个JSONObject
	 */
	public static void pay(boolean isMzCharge, String payway, GameSDK sdk,
			Activity activity, Handler handler, Object object) {
		//默认是拇指币充值地址
		String api = Constants.SERVER_URL + "/mzorder";
		
		JSONObject payRequest = new JSONObject();

		try {
			// 这里只有当时易宝支付时才会传入非null的object，并且它是一个JSONObject
			// 通过支付方式判断也可以，不过很玛法，就这样简单判断了
			if (object != null && object instanceof JSONObject) {
				payRequest = (JSONObject) object;
			}

			if (isMzCharge) {
				
				payRequest.put("amount", sdk.mzcharge);
				/*if(Constants.DEBUG == 1)
					payRequest.put("amount", 1);*/
				
				payRequest.put("ratio", 1);
				payRequest.put("username", sdk.account.getUsn());
				payRequest.put("coin_name", "拇指币");
				payRequest.put("thirPayId", "");
				payRequest.put("chargeWay", payway);
				if(payRequest.get("chargeWay").equals(PAYWAY_WECHAT))
					payRequest.put("chargeWay", "wftwxpay");
				payRequest.put("gameId", sdk.gameId);
				payRequest.put("deviceId", sdk.deviceId);
				payRequest.put("packetId", sdk.packetId);
			} else {
				
				payRequest.put("total_fee", sdk.requestAmount);
				/*if(Constants.DEBUG == 1)
					payRequest.put("total_fee", 1);*/
				payRequest.put("ratio", sdk.ratio);
				payRequest.put("coin_name", sdk.coinName);
				payRequest.put("cp_order_id", sdk.cpOrderId);
				payRequest.put("user_id", sdk.account.getUsn());
				payRequest.put("payway", payway);
				if(payRequest.get("payway").equals(PAYWAY_WECHAT))
					payRequest.put("payway", "wftwxpay");
				
				payRequest.put("game_id", sdk.gameId);
				payRequest.put("device_id", sdk.deviceId);
				payRequest.put("packet_id", sdk.packetId);
			}
			
			
			payRequest.put("level", sdk.level);
			
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);
			
			
			
		} catch (Exception e) {

		}

		Util_G.debug_i(Constants.TAG1, "PayRequest is : "+payRequest.toString());
		if (payway.equals(PAYWAY_ALI)) {
			if (!isMzCharge) {
				api = Constants.SERVER_URL + "/alipay_sign";
			}

			HttpRequest<AlipayOrderInfo> request = new HttpRequest<AlipayOrderInfo>(
					activity, new AlipayOrderInfoParser(),
					new AlipayOrderInfoHttpCb(handler, activity), true);
			request.execute(api, payRequest.toString());
		} else if (payway.equals(PAYWAY_WECHAT)) {
			if (!isMzCharge) {
				api = Constants.SERVER_URL + "/wftwxpay_sign";
			}
			HttpRequest<WeiXinOrderInfo> request = new HttpRequest<WeiXinOrderInfo>(
					activity, new WeiXinOrderInfoParser(),
					new WeChatOrderInfoHttpCb(activity), true);
			request.execute(api, payRequest.toString());
		} else if (payway.equals(PAYWAY_UNION)) {
			if (!isMzCharge) {
				api = Constants.SERVER_URL + "/unionpay_sign";
			}
			HttpRequest<UnionpayOrderInfo> request = new HttpRequest<UnionpayOrderInfo>(
					activity, new UnionpayOrderInfoParser(),
					new UnionpayOrderInfoHttpCb(activity), true);
			request.execute(api, payRequest.toString());
		} else if (payway.equals(PAYWAY_TEN)) {
			if (!isMzCharge) {
				api = Constants.SERVER_URL + "/tenpay_sign";
			}
			HttpRequest<TenpayOrderInfo> request = new HttpRequest<TenpayOrderInfo>(
					activity, new TenpayOrderInfoParser(),
					new TenpayOrderInfoHttpCb(activity), true);
			request.execute(api, payRequest.toString());
		} else if (payway.equals(PAYWAY_YEE_JCARD)
				|| payway.equals(PAYWAY_YEE_ZYCARD)
				|| payway.equals(PAYWAY_YEE_TSCARD)
				|| payway.equals(PAYWAY_CHINA_MOBILE)
				|| payway.equals(PAYWAY_CHINA_UNICOM)
				|| payway.equals(PAYWAY_CHINA_TELECOM)) {
			if (!isMzCharge) {
				api = Constants.SERVER_URL + "/yeepay_sign";
			}
			HttpRequest<YeepayCardResult> request = new HttpRequest<YeepayCardResult>(
					activity, new YeepayCardResultParser(),
					new YeepayCardResultHttpCb(activity, handler,
							sdk.requestAmount), true);
			request.execute(api, payRequest.toString());
		}

	}

	/**
	 * 支付宝 支付
	 */
	public static void alipay(GameSDK sdk, Activity activity,
			Handler alipayHandler) {
		String api = Constants.SERVER_URL + "/alipay_sign";
		HttpRequest<AlipayOrderInfo> request = new HttpRequest<AlipayOrderInfo>(
				activity, new AlipayOrderInfoParser(),
				new AlipayOrderInfoHttpCb(alipayHandler, activity), true);
		try {

			JSONObject payRequest = new JSONObject();

			payRequest.put("total_fee", sdk.requestAmount);
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);
			payRequest.put("coin_name", sdk.coinName);
			payRequest.put("cp_order_id", sdk.cpOrderId);

			payRequest.put("payway", PayManager.PAYWAY_ALI);

			payRequest.put("device_id", sdk.deviceId);
			payRequest.put("packet_id", sdk.packetId);
			payRequest.put("level", sdk.level);
			payRequest.put("game_id", sdk.gameId);
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("user_id", sdk.account.getUsn());
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);
			request.execute(api, payRequest.toString());
		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}
	}

	/**
	 * 微信
	 */
	public static void weChatPay(GameSDK sdk, Activity activity) {

		String api = Constants.SERVER_URL + "/wxpay_sign";
		HttpRequest<WeiXinOrderInfo> request = new HttpRequest<WeiXinOrderInfo>(
				activity, new WeiXinOrderInfoParser(),
				new WeChatOrderInfoHttpCb(activity), true);
		try {
			JSONObject payRequest = new JSONObject();

			payRequest.put("total_fee", sdk.requestAmount);
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);
			payRequest.put("coin_name", sdk.coinName);
			payRequest.put("cp_order_id", sdk.cpOrderId);

			payRequest.put("payway", "wxpay");

			payRequest.put("device_id", sdk.deviceId);
			payRequest.put("packet_id", sdk.packetId);
			payRequest.put("level", sdk.level);
			payRequest.put("game_id", sdk.gameId);
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("user_id", sdk.account.getUsn());
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);

			request.execute(api, payRequest.toString());
		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}
	}

	/**
	 * 银联支付
	 * 
	 * @param amount
	 */
	public static void unionpay(GameSDK sdk, Activity activity) {
		String api = Constants.SERVER_URL + "/unionpay_sign";
		HttpRequest<UnionpayOrderInfo> request = new HttpRequest<UnionpayOrderInfo>(
				activity, new UnionpayOrderInfoParser(),
				new UnionpayOrderInfoHttpCb(activity), true);
		try {
			JSONObject payRequest = new JSONObject();

			payRequest.put("cp_order_id", sdk.cpOrderId);
			payRequest.put("total_fee", sdk.requestAmount);
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);
			payRequest.put("coin_name", sdk.coinName);

			payRequest.put("payway", PayManager.PAYWAY_UNION);

			payRequest.put("device_id", sdk.deviceId);
			payRequest.put("level", sdk.level);
			payRequest.put("packet_id", sdk.packetId);
			payRequest.put("game_id", sdk.gameId);
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("user_id", sdk.account.getUsn());
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);

			request.execute(api, payRequest.toString());
		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}
	}

	/**
	 * 财付通支付
	 */
	public static void tenpay(GameSDK sdk, Activity activity) {

		String api = Constants.SERVER_URL + "/tenpay_sign";
		HttpRequest<TenpayOrderInfo> request = new HttpRequest<TenpayOrderInfo>(
				activity, new TenpayOrderInfoParser(),
				new TenpayOrderInfoHttpCb(activity), true);
		try {
			JSONObject payRequest = new JSONObject();

			payRequest.put("cp_order_id", sdk.cpOrderId);// "20151020160348-1075-5392402273");//
			payRequest.put("total_fee", sdk.requestAmount);// "30");//
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);// "0");//
			payRequest.put("coin_name", sdk.coinName);// "金币");//

			payRequest.put("payway", "tenpay");

			payRequest.put("device_id", sdk.deviceId);// "867689028783464");//
			payRequest.put("packet_id", sdk.packetId);// "100330001");//
			payRequest.put("level", sdk.level);// "7");//
			payRequest.put("game_id", sdk.gameId);// "330");//
			payRequest.put("game_server_id", sdk.serverId);// "2057");//
			payRequest.put("user_id", sdk.account.getUsn());// "ewanceshi");//
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);

			request.execute(api, payRequest.toString());
		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}
	}

	public static void yeePay(GameSDK sdk, Activity activity, Handler handler,
			JSONObject payRequest) {

		String api = Constants.SERVER_URL + "/yeepay_sign";
		HttpRequest<YeepayCardResult> request = new HttpRequest<YeepayCardResult>(
				activity,
				new YeepayCardResultParser(),
				new YeepayCardResultHttpCb(activity, handler, sdk.requestAmount),
				true);
		try {
			payRequest.put("cp_order_id", sdk.cpOrderId);
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);
			payRequest.put("coin_name", sdk.coinName);

			payRequest.put("device_id", sdk.deviceId);
			payRequest.put("packet_id", sdk.packetId);
			payRequest.put("game_id", sdk.gameId);
			payRequest.put("level", sdk.level);
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("user_id", sdk.account.getUsn());
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);
			request.execute(api, payRequest.toString());
		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}
	}

	public static void mzpay(GameSDK sdk, Activity activity, Handler handler) {
		String api = Constants.SERVER_URL + "/mzpay";

		HttpRequest<MzPayInfo> request = new HttpRequest<MzPayInfo>(activity,
				new MzpayInfoPaser(), new MzPayHttpCb(handler, activity), true);

		try {

			JSONObject payRequest = new JSONObject();
			payRequest.put("device_id", sdk.deviceId);
			payRequest.put("packet_id", sdk.packetId);

			payRequest.put("level", sdk.level);
			payRequest.put("game_id", sdk.gameId);
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("cp_order_id", sdk.cpOrderId);
			payRequest.put("user_id", sdk.account.getUsn());
			payRequest.put("total_fee", sdk.requestAmount);
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);
			payRequest.put("payway", "mzpay");

			payRequest.put("coin_name", sdk.coinName);
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);
			String psw = sdk.account.getPsd();
			String content = "";
			content = payRequest.toString();
			String str = content + "&" + psw;
			String sign = "";
			sign = Rsa.getMD5(str.getBytes("utf-8"));

			JSONObject payRequest1 = new JSONObject();
			payRequest1.put("content", payRequest);
			payRequest1.put("sign", sign);
			request.execute(api, payRequest1.toString());

		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}

	}

	/**
	 * 处理专属币支付 2015-05-26
	 * 
	 * @param onlyAmount
	 */
	public static void onlypay(GameSDK sdk, Activity activity, Handler handler) {
		String api = Constants.SERVER_URL + "/onlypay";

		HttpRequest<OnlyPayInfo> request = new HttpRequest<OnlyPayInfo>(
				activity, new OnlypayInfoPaser(), new OnlyPayHttpCb(handler,
						activity), true);

		try {

			JSONObject payRequest = new JSONObject();
			payRequest.put("device_id", sdk.deviceId);
			payRequest.put("packet_id", sdk.packetId);
			payRequest.put("payway", "onlypay");
			payRequest.put("level", sdk.level);
			payRequest.put("game_id", sdk.gameId);
			payRequest.put("game_server_id", sdk.serverId);
			payRequest.put("cp_order_id", sdk.cpOrderId);
			payRequest.put("user_id", sdk.account.getUsn());
			payRequest.put("total_fee", sdk.requestAmount);
			payRequest.put("order_amount", sdk.requestAmount);
			payRequest.put("ratio", sdk.ratio);
			payRequest.put("coin_name", sdk.coinName);
			payRequest.put("tradeID", "");
			payRequest.put("ver", Constants.GAME_SDK_VERSION);

			String psw = sdk.account.getPsd();
			String content = "";

			content = payRequest.toString();

			String str = content + "&" + psw;
			String sign = "";

			Util_G.debug_i(Constants.TAG1, "签名字符串："+str);
			sign = Rsa.getMD5(str.getBytes("utf-8"));

			JSONObject payRequest1 = new JSONObject();
			payRequest1.put("content", payRequest);
			payRequest1.put("sign", sign);
			request.execute(api, payRequest1.toString());

		} catch (Exception ex) {
			sdk.makeToast("支付信息提交错误！");
		}
	}

	/**
	 * 获取拇指账户最新余额
	 * 
	 * @param uname
	 * @return
	 */
	public static int getNewBalance(GameSDK sdk) {
		String uname = sdk.account.getUsn();
		DefaultHttpClient postclient = new DefaultHttpClient();
		String url = Constants.SERVER_URL + "/mzbalance";
		HttpPost post = new HttpPost(url);
		JSONObject json = new JSONObject();
		JSONObject retjson = null;
		try {
			json.put("username", uname);
			json.put("gameid", sdk.gameId);
			StringEntity entity = new StringEntity(json.toString(), "utf-8");// 解决中文乱码问题
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse ret = postclient.execute(post);
			if (ret.getStatusLine().getStatusCode() == 200) {
				HttpEntity retEntity = ret.getEntity();
				String content = EntityUtils.toString(retEntity);
				retjson = new JSONObject(content);
				String b = retjson.getString("userbalance");
				int i = Integer.valueOf(b);
				return i;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 初始化所有手机充值卡支付
	 * 
	 * @param sdk
	 * @return
	 */
	public static ArrayList<PayObject> initMBPays(GameSDK sdk) {
		ArrayList<PayObject> finalmbPays = new ArrayList<PayObject>();
		for (int i = 3; i < 6; i++) {
			// 这三位分析确定手机充值卡支付
			if (sdk.paywaysign.get(i) == 1) {
				PayObject pobj = new PayObject();
				String temppayway = mobileCardWays[i - 3];
				String temppayName = mobileCardNames[i - 3];
				pobj.setPayName(temppayName);
				pobj.setPayWay(temppayway);
				finalmbPays.add(pobj);
			}
		}
		return finalmbPays;
	}

	/**
	 * 初始化所有游戏点卡支付
	 * 
	 * @param sdk
	 * @return
	 */
	public static ArrayList<PayObject> initGCPays(GameSDK sdk) {
		ArrayList<PayObject> finalgcPays = new ArrayList<PayObject>();
		for (int i = 6; i < 9; i++) {
			// 这三位分析确定游戏点卡支付
			if (sdk.paywaysign.get(i) == 1) {
				PayObject pobj = new PayObject();
				String temppayway = gameCardWays[i - 6];
				String temppayName = gameCardNames[i - 6];
				pobj.setPayName(temppayName);
				pobj.setPayWay(temppayway);
				finalgcPays.add(pobj);
			}
		}
		return finalgcPays;
	}

	/**
	 * 若有历史支付方式则初始化历史，否则初始化支付宝支付
	 * 
	 * @param sdk
	 * @return
	 */
	public static ArrayList<PayObject> initFirstFinalPays(GameSDK sdk) {

		String lastpway = sdk.account.getPreferpayway();
		Util_G.debug_i(Constants.TAG1, "lastpway:" + lastpway);
		ArrayList<PayObject> finalPays = new ArrayList<PayObject>();

		if (StringUtil.isEmpty(lastpway)) {
			// 初始化支付宝支付
			PayObject pobj = new PayObject();
			String temppayway = "alipay";
			String temppayName = "";
			for (int i = 0; i < allPayWays.length; i++) {
				if (temppayway.equals(allPayWays[i])) {
					temppayName = allPayNames[i];
					break;
				}

			}
			pobj.setPayName(temppayName);
			pobj.setPayWay(temppayway);
			finalPays.add(pobj);
		} else {
			// 初始化上次支付
			PayObject pobj = new PayObject();
			String temppayName = "";
			for (int i = 0; i < allPayWays.length; i++) {
				if (lastpway.equals(allPayWays[i]))
					temppayName = allPayNames[i];

			}
			pobj.setPayName(temppayName);
			pobj.setPayWay(lastpway);
			finalPays.add(0, pobj);

		}

		return finalPays;

	}

	/**
	 * 根据情况初始化支付宝，微信，银联，财付通，拇指币，专属币支付方式
	 * 
	 * @param sdk
	 * @param finalmbPays
	 * @param finalgcPays
	 */
	public static ArrayList<PayObject> initFinalPayways(GameSDK sdk) {
		ArrayList<PayObject> finalPays = new ArrayList<PayObject>();
		for (int i = 0; i < 3; i++) {
			// 前三位分析确定支付宝，财付通/银联
			if (sdk.paywaysign.get(i) == 1 && i < allPayNames.length) {
				PayObject pobj = new PayObject();
				String temppayway = allPayWays[i];
				String temppayName = allPayNames[i];
				pobj.setPayName(temppayName);
				pobj.setPayWay(temppayway);
				finalPays.add(pobj);

			}
		}

		// 判断加入微信支付
		if (sdk.paywaysign.get(9) == 1) {
			PayObject pobj = new PayObject();
			String temppayway = allPayWays[7];
			String temppayName = allPayNames[7];
			pobj.setPayName(temppayName);
			pobj.setPayWay(temppayway);

			// 要确保它放在支付宝之后
			if (finalPays.get(0).getPayWay().equals(allPayWays[0])) {
				finalPays.add(1, pobj);
			} else {
				finalPays.add(0, pobj);
			}

		}

		// 加入拇指币支付
		{
			PayObject pobj = new PayObject();
			String temppayway = allPayWays[5];
			String temppayName = allPayNames[5];
			pobj.setPayName(temppayName);
			pobj.setPayWay(temppayway);
			finalPays.add(0, pobj);
		}

		// 加入专属比支付
		if (sdk.olbalance != -1) {
			PayObject pobj = new PayObject();
			String temppayway = allPayWays[6];
			String temppayName = allPayNames[6];
			pobj.setPayName(temppayName);
			pobj.setPayWay(temppayway);
			finalPays.add(1, pobj);
		}

		return finalPays;

	}
}
