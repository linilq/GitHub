package zty.sdk.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.activity.LoginActivity;
import zty.sdk.activity.PaymentActivity;
import zty.sdk.db.NativeAccountDao;
import zty.sdk.http.ActivateHttpCb;
import zty.sdk.http.FindPswHttpCb;
import zty.sdk.http.HttpRequest;
import zty.sdk.http.IdenCodeHttpCb;
import zty.sdk.http.InitializeHttpCb;
import zty.sdk.http.LoginHttpCb;
import zty.sdk.http.RegisterHttpCb;
import zty.sdk.http.ReqICodeHttpCb;
import zty.sdk.http.UserAccountReqHttpCb;
import zty.sdk.listener.ActivateListener;
import zty.sdk.listener.ExitListener;
import zty.sdk.listener.ExitQuitListener;
import zty.sdk.listener.FindPswListener;
import zty.sdk.listener.GameSDKLoginListener;
import zty.sdk.listener.GameSDKPaymentListener;
import zty.sdk.listener.IdentifyCodeListener;
import zty.sdk.listener.InitializeListener;
import zty.sdk.listener.LoginListener;
import zty.sdk.listener.RegisterListener;
import zty.sdk.listener.RequestCodeListener;
import zty.sdk.listener.UserAccountListener;
import zty.sdk.model.ActivateResult;
import zty.sdk.model.DeviceInfo;
import zty.sdk.model.IdentifyCode;
import zty.sdk.model.InitializeResult;
import zty.sdk.model.NativeAccountInfor;
import zty.sdk.model.PayPreMessage;
import zty.sdk.model.UserInfo;
import zty.sdk.paeser.ActivateResultParser;
import zty.sdk.paeser.FindPswPaser;
import zty.sdk.paeser.IdentifyCodeParser;
import zty.sdk.paeser.InitializeResultParser;
import zty.sdk.paeser.PayPreMsgPaser;
import zty.sdk.paeser.UserInfoParser;
import zty.sdk.utils.DeviceInfoUtil;
import zty.sdk.utils.DialogUtil;
import zty.sdk.utils.DialogUtil.DialogCallBack;
import zty.sdk.utils.Helper;
import zty.sdk.utils.LocalStorage;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.TcardStorage;
import zty.sdk.utils.Util_G;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

/**
 * SDK主类，实现初始化、激活、注册、修改密码、账号绑定等
 * 
 * @author Administrator
 * 
 */
public class GameSDK implements InitializeListener, ActivateListener {

	private static GameSDK instance;

	private final static Lock lock = new ReentrantLock();

	private Context context;
	private GameSDKLoginListener gameSDKLoginListener;
	
	@Deprecated
	private GameSDKPaymentListener gameSDKPaymentListener;

	

	/**
	 * 记录登入时间，最后计算总在线时间，每次登陆回传保证在线时长精确
	 */
	public int afdft;
	/**
	 * 记录上次计算时间，每次计算，每两分钟上传一次在线时长 初始值afdft = afdft01
	 */
	private int afdft01;
	/**
	 * inGameSign=true表示sdk退出，进入游戏界面，开启在线时长线程; inGameSign=false表示离开游戏界面
	 * 初始化为false
	 */
	private boolean inGameSign = false;
	/**
	 * loginedSign=true表示登陆过，可以开启在线时长线程; loginedSign=false没有登录过
	 * 为了记录准确记录在线时长，需要inGameSign与loginedSign并用
	 * 初始化为false
	 */
	private boolean loginedSign = false;
	

	/**
	 * 标识app是否安装
	 */
	private boolean appInstalled = false;
	/**
	 * 标识是第一次进入sdk，显示广告，自动登录
	 */
	public boolean isFirstIn = true;
	/**
	 * onlineTimeCounterRunning为了避免在线时长统计线程跑多个而加入此标记
	 */
	private boolean onlineTimeCounterRunning = false;
	public boolean isMzCharge = false;
	private static boolean initalized = false;
	public String deviceId;
	public String packetId = "1";// lsl
	public String gameId;
	public String gameName;
	public String debug = "";

	/* activate回传数据begin */
	private String registerUrl;
	private String changePassworkUrl;
	private String loginUrl;
	private String reqCodeUrl;// 2015-03-26 获取验证码地址
	private String identiCodeUrl;// 2015-03-26 验证验证码地址
	private String alipayWapUrl;
	private String payways;
	private String afdf;
	public String IsZG;// zgbao
	public String dipcon;// 登录跑马灯内容
	public String dipcon2;// 支付跑马灯内容
	public String dipurl;// 支付公告
	public String noturl;// 登录公告
	public String exiturl;
	/* activate回传数据end */

	/**
	 * paymentActivity中的 支付类型数组一一对应
	 */
	public List<Integer> paywaysign = new ArrayList<Integer>();// 2015-07-07加入支付方式标记

	/* 支付数据begin */
	public int requestAmount = 0;// 钱单位 一般为元
	public int mzcharge = 0;// 拇指币充值金额
	public int ratio = 1;// 钱和道具的比列
	public String coinName = "";// 道具单位
	public int level = 0;// 等级
	public String serverId = "";
	public String cpOrderId = "";
	/* 支付数据end */

	/* 进入支付界面前向服务器获取的参数 */
	public int mzbalance = 0;// 拇指币余额
	public int olbalance = 0;// 专属B
	public String mobilePayStr = "";
	public String unicomPayStr = "";
	public String telecomPayStr = "";
	public String jcardPayStr = "";
	public String zycardPayStr = "";
	public String tscardPayStr = "";
	/* 进入支付界面前向服务器获取的参数 */

	/**
	 * 是否直接进入登陆界面，由initSDK传入
	 */
	public boolean showLogin;
	/**
	 * 退出接口回调
	 */
	public ExitListener mexitListener;
	/**
	 * 退出接口取消回调
	 */
	public ExitQuitListener mexitquitListener;
	/**
	 * 预留参数，由initSDK传入
	 */
	public Object mcallbackData;

	public UserInfo userInfo;
	public HttpRequest mRequest = null;

	private HttpRequest<ActivateResult> mActivateRequest = null;
	HttpRequest<InitializeResult> mInitializeRequest = null;

	private GameSDK(Context context) {
		this.context = context;
	}

	public static GameSDK getInstance(Context context) {

		try {
			lock.lock();
			if (!initalized) {
				instance = new GameSDK(context);
				initalized = true;
			}
			return instance;
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 只能在初始化之后调用，不会自行创建实例
	 * @return instance 初始化好的实例
	 */
	public static GameSDK getOkInstance() {
		return instance;
	}

	/**
	 * 只能在初始化之后调用
	 */
	public static void onResume() {
		instance.onResumeSelf();
	}
	
	private void onResumeSelf() {
		inGameSign = true;
		if (loginedSign&&inGameSign) {
			afdf();
		}
	}

	/**
	 * 只能在初始化之后调用
	 */
	public static void onPause() {
		instance.onPauseSelf();
	}
	
	private void onPauseSelf() {
		inGameSign = false;
		onlineTimeCounterRunning = false;
	}

	/**
	 * 网游SDK初始化接口
	 * 
	 * @param activity
	 *            游戏主类
	 * @param gameSDKLoginListener sdk登录回调
	 */
	public static void initSDK(Activity activity,
			GameSDKLoginListener gameSDKLoginListener) {

		initSDK(activity, gameSDKLoginListener, true);
	}

	/**
	 * @param activity 游戏主类
	 * 
	 * @param gameSDKLoginListener sdk登录回调
	 * 
	 * @param bShowLogin 是否直接显示登陆界面true：显示；false：不显示，此时登陆需要手动调用GameSDK.Login()
	 * 
	 */
	public static void initSDK(Activity activity,
			GameSDKLoginListener gameSDKLoginListener, boolean bShowLogin) {

		if (gameSDKLoginListener == null)
			throw new IllegalArgumentException(
					"GameSDKLoginListener can not be null!");

		Toast.makeText(activity, "初始化开始", 0).show();

		getInstance(activity);
		
		instance.appInstalled = checkAppInstalled();
		Util_G.debug_i(Constants.TAG1, "app installed :"+ instance.appInstalled);
		
		instance.gameSDKLoginListener = gameSDKLoginListener;
		instance.showLogin = bShowLogin;

		// **************记录进入时间******************//
		instance.afdft = (int) (System.currentTimeMillis() / 1000);
		instance.afdft01 = instance.afdft;

		// *************获取跑马灯与登陆公告地址（上次保存）******************//
		LocalStorage storage = LocalStorage.getInstance(activity);
		String discon = storage.getString(Constants.DisCon);
		instance.dipcon = discon;
		instance.noturl = storage.getString(Constants.DisUrl);

		// *************获取关键参数如gameId、packetId，Debug**********************//
		ApplicationInfo appInfo;
		try {
			appInfo = activity.getPackageManager().getApplicationInfo(
					activity.getPackageName(), PackageManager.GET_META_DATA);

			instance.gameName = (String) appInfo.metaData.get("ZTY_GAME_NAME");
			
			Object obj = appInfo.metaData.get("ZTY_DEBUG");
			Log.v(Constants.TAG1, "appInfo.metaData.get(ZTY_DEBUG) is null? "+(obj==null));
			instance.debug = "";
			Integer value;
			if(obj!=null){
				value = Integer.parseInt(obj.toString()) ;
				if (value != null)
					instance.debug = value.toString();
			}

			String ZTY_GAME_ID = Helper.getCofigValue(activity, "/mzyw.xml",
					"ZTY_GAME_ID");
			instance.gameId = "";
			if (!StringUtil.isEmpty(ZTY_GAME_ID)) {
				instance.gameId = ZTY_GAME_ID;
			}
			String ZTY_PACKET_ID = Helper.getCofigValue(activity, "/mzyw.xml",
					"ZTY_PACKET_ID");
			instance.packetId = "";
			if (!StringUtil.isEmpty(ZTY_PACKET_ID)) {
				instance.packetId = ZTY_PACKET_ID;
			}

			Integer value1 = (Integer) appInfo.metaData.get("ZTY_ZG");
			instance.IsZG = "";
			if (value1 != null) {
				instance.IsZG = value1.toString();
			}

			if (StringUtil.isEmpty(instance.packetId)) {
				value = (Integer) appInfo.metaData.get("ZTY_PACKET_ID");
				if (value > 0)
					instance.packetId = value.toString();//
			}
			if (StringUtil.isEmpty(instance.gameId)) {
				Integer gameid = (Integer) appInfo.metaData.get("ZTY_GAME_ID");
				instance.gameId = gameid.toString();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Util_G.debug_i(Constants.TAG1, "initSDK error");
		}
		Util_G.debug_i("test", "initSDK");

		// 初始化SDK//
		instance.initialize();

	}

	public static String getNo(int leng) {
		String ret = "";
		int[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		int result = 0;
		for (int i = 0; i < leng; i++)
			result = result * 10 + array[i];
		ret = String.format("%d", result);
		return ret;
	}

	public boolean isInitalized() {
		return initalized;
	}

	public void initialize() {
		init(this);
	}

	private void init(InitializeListener initializeListener) {

		Util_G.debug_i(Constants.TAG1, "***************init*******************");//
		Util_G.debug_i("test", "init,参数包括packetid：" + instance.packetId
				+ "  gameid：" + instance.gameId);//

		LocalStorage storage = LocalStorage.getInstance(context);
		String deviceId = storage.getString(Constants.DEVICE_ID);
		String URL = storage.getString(Constants.URL);
		if (URL.length() > 1) {
			Constants.SERVER_URL = URL;
			Util_G.debug_i("test", "url=" + URL);
		}

		if (StringUtil.isEmpty(deviceId)) {

			DeviceInfo info = DeviceInfoUtil.getDeviceInfo(context);
			info.setPackageId(packetId);
			String api = Constants.SERVER_URL + "/init";

			HttpRequest<InitializeResult> request = new HttpRequest<InitializeResult>(
					context, new InitializeResultParser(),
					new InitializeHttpCb(initializeListener,context), false);
			mInitializeRequest = request;
			request.execute(api, info.toJSON());

		} else {
			InitializeResult result = new InitializeResult(deviceId);
			initializeListener.onInitSuccess(result);
		}

	}

	@Override
	public void onInitSuccess(InitializeResult initializeResult) {
		GameSDK.this.deviceId = initializeResult.deviceId;
		activate();
	}

	public void activate() {
		activate(this);
	}

	private void activate(ActivateListener activateListener) {

		Util_G.debug_i(Constants.TAG1,
				"***************activate*******************");
		String api = Constants.SERVER_URL + "/activate";

		String netType = DeviceInfoUtil.getNetworkType(context);

		HttpRequest<ActivateResult> request = new HttpRequest<ActivateResult>(
				context, new ActivateResultParser(), new ActivateHttpCb(
						activateListener), true);
		mActivateRequest = request;
		JSONObject user = new JSONObject();
		try {
			user.put("device_id", deviceId);
			user.put("packet_id", packetId);
			user.put("game_id", gameId);
			user.put("net_type", netType);
			user.put("ver", Constants.GAME_SDK_VERSION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		request.execute(api, user.toString());

	}

	@Override
	public void onActivateSuccess(ActivateResult activateResult) {
		String serverUrl = activateResult.getServerUrl();
		GameSDK.this.changePassworkUrl = serverUrl
				+ activateResult.getChangePasswordUrl();
		GameSDK.this.registerUrl = serverUrl + activateResult.getRegisterUrl();
		GameSDK.this.loginUrl = serverUrl + activateResult.getLoginUrl();
		GameSDK.this.alipayWapUrl = serverUrl
				+ activateResult.getAlipayWapUrl();
		GameSDK.this.payways = activateResult.getPayways();
		GameSDK.this.afdf = serverUrl + activateResult.getAdfd();
		GameSDK.this.dipcon = activateResult.getDipcon();
		// **********2015-03-27************************//
		GameSDK.this.reqCodeUrl = serverUrl + activateResult.getIdenti1();
		GameSDK.this.identiCodeUrl = serverUrl + activateResult.getIdenti2();
		// **************2015-03-27*****************//
		String payways = activateResult.getPaywaysign();
		for (int i = 0; i < payways.length(); i++) {
			paywaysign.add(Integer.parseInt(payways.charAt(i) + ""));
		}
		LocalStorage storage = LocalStorage.getInstance(context);

		storage.putString(Constants.URL, activateResult.getServerUrl());

		Util_G.debug_i("test", "newserverUrl=" + activateResult.getServerUrl());
		storage.putString(Constants.DisCon, activateResult.getDipcon());
		storage.putString(Constants.DisUrl, activateResult.getNoturl());

		instance.dipcon2 = activateResult.getDipcon2();
		instance.dipurl = activateResult.getDipurl();
		instance.noturl = activateResult.getNoturl();
		instance.exiturl = activateResult.getExiturl();
		GameSDK.this.initalized = true;
		Log.i(Constants.TAG1, "初始化initialized:  "+initalized);
		if (showLogin) {
			Intent intent = new Intent(context, LoginActivity.class);
			context.startActivity(intent);// lsl
		}else{
			makeToast("初始化完毕！");
		}
		
	}
	
	/**
	 * 
	 * @param serverId  1 - 游戏服ID，1服为1，2服为2……
	 * @param serverName 2 - 游戏服名称
	 * @param palyerName 3 - 玩家角色名称
	 * @param roleLevel 4 - 玩家级别
	 */
	public static void afdf2Self(String serverId, String serverName, String palyerName, int roleLevel) {
		Util_G.debug_i(Constants.TAG1, "升级信息");
		instance.afdf2(serverId,
			       serverName,
			       palyerName,
			       roleLevel
			      ); 
	}
	
	private void afdf2(String str1, String str2, String str3, int a) {
		LocalStorage storage = LocalStorage.getInstance(context);
		storage.putString("adff3", String.valueOf(a));// 三级别
		storage.putString("adff4", str1);// 服ID
		storage.putString("adff5", str2);// 服名字
		storage.putString("adff6", str3);// 角色名
	}

	public void afdf() {
		if (!initalized || onlineTimeCounterRunning) {
			return;
		}
		onlineTimeCounterRunning = true;
		final LocalStorage storage = LocalStorage.getInstance(context);
		new Thread(new Runnable() {

			@Override
			public void run() {
				int tick = 0;
				while (inGameSign == true) {
					Util_G.debug_i(Constants.TAG, "afdf is running");
					try {
						Thread.sleep(1000);
						tick++;
					} catch (Exception ex) {

					}
					Util_G.debug_i(Constants.TAG, "afdf 循环"+tick);
					if ((tick % 120) == 0) {//
						int afd = 0;
						afd = (int) (System.currentTimeMillis() / 1000);
						storage.putString("adff2",
								String.valueOf(afd - instance.afdft01));
						instance.afdft01 = afd;
						// 2015-05-10
						Util_G.debug_i(Constants.TAG, "两分钟到，上报在线时长");
						afdf03(storage);
					}

				}
				Util_G.debug_i(Constants.TAG, "afdf 结束循环");

			}
		}).start();

	}

	/**
	 * 即时上报在线时长与等级
	 * @param storage
	 */
	private void afdf03(LocalStorage storage) {
		int adff2 = 0;
		int adff3 = 0;
		String content = "0";
		String adff4 = "";
		String adff5 = "";
		String adff6 = "";
		String adff0 = account.getUsn();
		content = storage.getString("adff2", "0");
		adff2 = Integer.valueOf(content);

		content = storage.getString("adff3", "0");
		adff3 = Integer.valueOf(content);

		adff4 = storage.getString("adff4", "");
		adff5 = storage.getString("adff5", "");
		adff6 = storage.getString("adff6", "");
		try {
			JSONObject user = new JSONObject();
			user.put("device_id", deviceId);
			user.put("packet_id", packetId);
			user.put("game_id", gameId);
			user.put("login_account", adff0);
			user.put("server_id", adff4);
			user.put("adff5", adff5);
			user.put("adff6", adff6);
			user.put("adff2", adff2);
			user.put("adff3", adff3);
			user.put("ver", Constants.GAME_SDK_VERSION);

			// 创建默认的httpClient实例.
			DefaultHttpClient postClient = new DefaultHttpClient();
			// 创建httppost
			String url = Constants.SERVER_URL + "/afdf";
			HttpPost httppost = new HttpPost(url);
			StringEntity entity = new StringEntity(user.toString(), "utf-8");// 解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			httppost.setEntity(entity);
			Util_G.debug_i(Constants.TAG1,
					"***************afdf3()*******************");
			postClient.execute(httppost);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 上报在线时长与等级，登录完成时
	 */
	public void afdf3() {
		Util_G.debug_i(Constants.TAG, "afdf");
		if (!initalized) {
			makeToast("初始化失败，请退出程序后再试");
			return;
		}

		String adff0 = account.getUsn();

		try {
			LocalStorage storage = LocalStorage.getInstance(context);
			int adff2 = 0;
			int adff3 = 0;
			String content = "0";
			String adff4 = "";
			String adff5 = "";
			String adff6 = "";

			content = storage.getString("adff2", "0");// crypto.decrypt(deviceId,content);
			adff2 = Integer.valueOf(content);

			content = storage.getString("adff3", "0");// crypto.decrypt(deviceId,content);
			adff3 = Integer.valueOf(content);

			adff4 = storage.getString("adff4", "");
			adff5 = storage.getString("adff5", "");
			adff6 = storage.getString("adff6", "");

			JSONObject user = new JSONObject();
			user.put("device_id", deviceId);
			user.put("packet_id", packetId);
			user.put("game_id", gameId);
			user.put("login_account", adff0);
			user.put("server_id", adff4);
			user.put("adff5", adff5);
			user.put("adff6", adff6);

			user.put("adff2", adff2);
			user.put("adff3", adff3);

			user.put("ver", Constants.GAME_SDK_VERSION);

			HttpRequest<UserInfo> request = new HttpRequest<UserInfo>(
					context.getApplicationContext(), null, null, false);
			request.execute(afdf, user.toString());

		} catch (Exception ex) {
		}

	}

	/**
	 * 游戏退出接口；
	 * 必须在初始化之后调用
	 * @param activity  游戏主类
	 * @param exitcallback 退出回调
	 * @param exitQuitcallback
	 * @param callbackData 回传参数，sdk不作处理，直接在ExitListener中返回
	 */
	public static void afdfOut(Activity activity, ExitListener exitcallback,ExitQuitListener exitQuitcallback,
			Object callbackData) {
		instance.afdf(activity,exitcallback,exitQuitcallback,callbackData);
	}
	/**
	 * 游戏退出接口
	 * 
	 * @param activity
	 * @param listener
	 * @param callbackData
	 */
	private void afdf(Activity activity, ExitListener listener,
			ExitQuitListener exitQuitListener,Object callbackData) {
		Util_G.debug_i(Constants.TAG, "afdf");
		if (!initalized) {
			if (listener != null) {
				listener.onExit(callbackData);
			}
			makeToast("初始化失败，请退出程序后再试");
			return;
		}
		
		if (activity == null) {
			return;
		}
		mexitListener = listener;
		mexitquitListener = exitQuitListener;
		mcallbackData = callbackData;
		String noturl = instance.exiturl;

		DialogUtil.showExitDialog1(activity, noturl, new DialogCallBack() {

			@Override
			public void callBack() {
				mexitListener.onExit(mcallbackData);

			}
		},new DialogCallBack() {
			
			@Override
			public void callBack() {
				mexitquitListener.onExitQuit(mcallbackData);
				
			}
		});

	}

	/**
	 * 当未初始化时，判断是否正处于初始化中
	 * 
	 * @return ret true表示初始化中
	 */
	public boolean isInitializing() {
		boolean ret = false;
		if (mInitializeRequest != null) {
			if (mInitializeRequest.isRunning())
				ret = true;
			else if (mActivateRequest != null) {
				if (mActivateRequest.isRunning())
					ret = true;
			}
		}
		return ret;
	}

	/**
	 * 登陆账号
	 * @param loginAccount
	 * @param password
	 * @param listener
	 * @param context
	 * @param showpro
	 */
	public void login(String loginAccount, String password,
			LoginListener listener, Context context, boolean showpro) {

		if (!initalized) {
			if (isInitializing())
				return;
			else
				initialize();
		}

		try {

			JSONObject user = new JSONObject();
			user.put("device_id", deviceId);
			user.put("packet_id", packetId);
			user.put("game_id", gameId);
			user.put("login_account", loginAccount);
			user.put("password", password);
			user.put("ver", Constants.GAME_SDK_VERSION);

			HttpRequest<UserInfo> request = new HttpRequest<UserInfo>(context,
					new UserInfoParser(), new LoginHttpCb(listener), showpro);
			mRequest = request;
			request.execute(loginUrl, user.toString());

		} catch (Exception ex) {
			makeToast("登录失败，请稍后再试");
		}

	}

	/**
	 * 注册账号
	 * @param loginAccount
	 * @param password
	 * @param registerlistener
	 * @param context
	 * @param isAuto
	 */
	public void register(String loginAccount, String password,
			RegisterListener registerlistener, Context context, boolean isAuto) {

		if (!initalized) {
			makeToast("初始化失败，请退出程序后再试");
			return;
		}

		try {

			JSONObject user = new JSONObject();
			user.put("device_id", deviceId);
			user.put("packet_id", packetId);
			user.put("game_id", gameId);
			user.put("login_account", loginAccount);
			user.put("password", password);
			user.put("ver", Constants.GAME_SDK_VERSION);
			user.put("isAuto", isAuto);
			HttpRequest<UserInfo> request = new HttpRequest<UserInfo>(context,
					new UserInfoParser(), new RegisterHttpCb(registerlistener),
					true);
			mRequest = request;
			request.execute(registerUrl, user.toString());

		} catch (Exception ex) {
			makeToast("注册失败，请稍后再试");
		}

	}

	/**
	 * 网游支付接口
	 * 
	 * @param activity
	 *            游戏环境变量
	 * @param serverId
	 *            服务器ID
	 * @param level
	 *            等级
	 * @param serverName
	 *            服务器名称
	 * @param Accountname
	 *            角色名称
	 * @param cpOrderId
	 *            订单号
	 * @param requestAmount
	 *            支付金额（元）
	 * @param ratio
	 *            兑换比例
	 * @param coinName
	 *            道具名称
	 */
	public static void startPay(Activity activity, String serverId, int mlevel,
			String serverName, String Accountname, String cpOrderId,
			int requestAmount, int ratio, String coinName) {
		Util_G.debug_i(Constants.TAG1, "开始支付");
		startPay(activity, serverId, mlevel, serverName, Accountname,
				cpOrderId, null, requestAmount, ratio, coinName,null);
	}
	
	@Deprecated 
	/**
	 * 
	 * @param activity
	 * @param serverId
	 * @param mlevel
	 * @param serverName
	 * @param Accountname
	 * @param cpOrderId
	 * @param requestAmount
	 * @param ratio
	 * @param coinName
	 * @param payListener
	 */
	public static void startPay(Activity activity, String serverId, int mlevel,
			String serverName, String Accountname, String cpOrderId,
			int requestAmount, int ratio, String coinName,GameSDKPaymentListener payListener) {
		Util_G.debug_i(Constants.TAG1, "开始支付");
		startPay(activity, serverId, mlevel, serverName, Accountname,
				cpOrderId, null, requestAmount, ratio, coinName,payListener);
	}

	/**
	 * 网游支付内部接口
	 * 
	 * @param activity
	 *            游戏环境变量
	 * @param serverId
	 *            服务器ID
	 * @param level
	 *            等级
	 * @param serverName
	 *            服务器名称
	 * @param Accountname
	 *            角色名称
	 * @param cpOrderId
	 *            订单号
	 * @param payways
	 *            支付方式 可设为null
	 * @param requestAmount
	 *            支付金额（元）
	 * @param ratio
	 *            兑换比例
	 * @param coinName
	 *            道具名称
	 */
	private static void startPay(Activity activity, String serverId,
			int mlevel, String serverName, String Accountname,
			String cpOrderId, String payways, int requestAmount, int ratio,
			String coinName,GameSDKPaymentListener payListener) {

		if (!instance.initalized) {// 激活成功 变为true
			instance.makeToast("初始化失败，请退出程序后再试");
			return;
		}

		if (instance.userInfo == null || instance.userInfo.getResult() != 1) {// 登录后获取到用户信息
			instance.makeToast("用户未登录");
			return;
		}

		String payway = instance.payways;
		if ((payways != null)) {
			payway = payways;
		}
		instance.gameSDKPaymentListener = payListener;
		// /降等级存入本地 到时候更新到激活表中2015-05-11

		LocalStorage storage = LocalStorage.getInstance(activity);
		storage.putString("adff3", mlevel + "");

		// /////
		Intent intent = new Intent(activity, PaymentActivity.class);
		intent.putExtra("device_id", instance.deviceId);
		intent.putExtra("alipay_wap_url", instance.alipayWapUrl);
		intent.putExtra("packet_id", instance.packetId);
		intent.putExtra("payways", payway);
		intent.putExtra("game_id", instance.gameId);
		intent.putExtra("gameName", instance.gameName);
		intent.putExtra("dipcon2", instance.dipcon2);
		intent.putExtra("dipurl", instance.dipurl);
		intent.putExtra("serverName", serverName);
		intent.putExtra("Accountname", Accountname);
		/* add by twl start 2015/8/10 */
		instance.requestAmount = requestAmount;
		instance.ratio = ratio;
		instance.coinName = coinName;
		instance.level = mlevel;
		instance.serverId = serverId;
		instance.cpOrderId = cpOrderId;
		/* add by twl end 2015/8/10 */
		if (cpOrderId == null) {
			cpOrderId = "";//
		}
		intent.putExtra("user_info", instance.userInfo);

		Util_G.debug_i(Constants.TAG1, "准备打开支付界面");
		instance.goToPayActivity(activity, intent);// 等获取拇指币成功之后
													// 才执行startActivity跳转到PaymentActivity界
	}

	private void goToPayActivity(final Activity mactivity, Intent intent) {
		Util_G.debug_i(Constants.TAG1, "进入支付界面-----");

		try {
			JSONObject json = new JSONObject();
			String username = getOkInstance().userInfo.getLoginAccount();
			json.put("username", username);
			json.put("gameid", instance.gameId);

			HttpRequest<PayPreMessage> request = new HttpRequest<PayPreMessage>(
					mactivity, new PayPreMsgPaser(), new UserAccountReqHttpCb(
							new UserAccountListener(mactivity, intent)), true);

			Util_G.debug_i(Constants.TAG1, "发送内容：" + json.toString());
			String mzBalanceurl = Constants.SERVER_URL + "/mzbalance";
			request.execute(mzBalanceurl, json.toString());
		} catch (Exception ex) {
			Util_G.debug_i(Constants.TAG1, ex.getMessage());
			Util_G.debug_i(Constants.TAG1, ex.toString());
			Util_G.debug_i(Constants.TAG1, "请求余额过程出问题");
		}

	}

	public void makeToast(String message) {
		Toast.makeText(context.getApplicationContext(), message,
				Toast.LENGTH_SHORT).show();//
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {

		Util_G.debug_i("test", "失败恢复url=" + Constants.OSERVER_URL);
		LocalStorage storage = LocalStorage.getInstance(context);
		storage.putString(Constants.URL, Constants.OSERVER_URL);
		makeToast("系统错误" + errorMessage);

	}

	public void notifyLoginSuccess(String username, int userId, String sign) {
		if (gameSDKLoginListener != null) {
			gameSDKLoginListener.onLoginSucess(username, userId, sign);
		}
	}

	public void notifyLoginCanceled() {
		if (gameSDKLoginListener != null) {
			gameSDKLoginListener.onLoginCancelled();
		}
	}
	
	/**
	 * 停止网络任务
	 */
	public void stopProgress() {
		if (mRequest != null) {
			mRequest.cancelTask();
			mRequest = null;
		}
		if (mActivateRequest != null) {
			mActivateRequest.cancelTask();
			mActivateRequest = null;
		}
		if (mInitializeRequest != null) {
			mInitializeRequest.cancelTask();
			mInitializeRequest = null;
		}

	}



	/**
	 * 修改密码
	 * @param loginAccount
	 * @param password
	 * @param newpassword
	 * @param registerListener
	 */
	public void changePassword(String loginAccount, String password,
			String newpassword, RegisterListener registerListener) {

		if (!initalized) {
			makeToast("初始化失败，请退出程序后再试！");
			return;
		}

		try {

			JSONObject user = new JSONObject();
			user.put("device_id", deviceId);
			user.put("packet_id", packetId);
			user.put("game_id", gameId);
			user.put("login_account", loginAccount);
			user.put("password", password);
			user.put("newpassword", newpassword);
			user.put("ver", Constants.GAME_SDK_VERSION);
			HttpRequest<UserInfo> request = new HttpRequest<UserInfo>(context,
					new UserInfoParser(), new RegisterHttpCb(registerListener),
					true);
			request.execute(changePassworkUrl, user.toString());

		} catch (Exception ex) {
			makeToast("修改失败，请稍后再试！");
		}

	}

	/**
	 * 执行获取验证码的网络请求
	 * 
	 * @param username
	 * @param phoneNum
	 * @param requestCodeCallback
	 */
	public void getIdenticode(String username, String phoneNum, String psd,
			String action, RequestCodeListener listener, Context context) {
		if (!initalized) {
			makeToast("初始化失败，请退出程序后再试");
			return;
		}

		try {
			JSONObject json = new JSONObject();
			json.put("userName", username);
			json.put("phoneNum", phoneNum);// userName=**&
			json.put("psd", psd);
			json.put("action", action);
			json.put("ver", Constants.GAME_SDK_VERSION);
			HttpRequest<IdentifyCode> request = new HttpRequest<IdentifyCode>(
					context, new IdentifyCodeParser(), new ReqICodeHttpCb(
							listener), true);

			request.execute(reqCodeUrl, json.toString());

		} catch (Exception ex) {
			makeToast("获取失败，请稍后再试");
		}
	}

	/**
	 * 执行验证码验证的网络请求
	 * 
	 * @param retCode
	 * @param identifyCodeCallback
	 */
	public void doIdentifyCode(IdentifyCode retCode, String action,
			IdentifyCodeListener listener,Context context) {
		if (!initalized) {
			makeToast("初始化失败，请退出程序后再试");
			return;
		}

		try {
			JSONObject json = new JSONObject();
			json.put("addDate", retCode.getAddDate());
			json.put("addTime", retCode.getAddTime());
			json.put("identiCode", retCode.getCode());
			json.put("action", action);
			HttpRequest<IdentifyCode> request = new HttpRequest<IdentifyCode>(
					context, new IdentifyCodeParser(), new IdenCodeHttpCb(
							listener), true);
			Util_G.debug_i(Constants.TAG1, "发送内容：" + json.toString());
			request.execute(identiCodeUrl, json.toString());

		} catch (Exception ex) {
			makeToast("doIdentifyCode()失败");
		}
	}

	/**
	 * 通过绑定的手机号找回密码
	 * 
	 * @param username
	 * @param findPswActivity
	 */
	public void sendFindpswReq(String phoneNum, FindPswListener listener,Context context) {
		if (!initalized) {
			makeToast("初始化失败，请退出程序后再试");
			return;
		}

		try {
			JSONObject json = new JSONObject();
			json.put("phoneNum", phoneNum);
			HttpRequest<String> request = new HttpRequest<String>(context,
					new FindPswPaser(), new FindPswHttpCb(listener), true);

			Util_G.debug_i(Constants.TAG1, "发送内容：" + json.toString());
			String findpswurl = Constants.SERVER_URL + "/FindPsw";
			request.execute(findpswurl, json.toString());
		} catch (Exception ex) {
			makeToast("获取失败，请稍后再试");
		}

	}

	/**
	 * 当前活动账号
	 */
	public NativeAccountInfor account = null;
	/**
	 * 当前账号列表
	 */
	public ArrayList<NativeAccountInfor> accountList = new ArrayList<NativeAccountInfor>();

	/**
	 * 初始化所有账号以及链表list,当账号发生变更时必须调用;
	 * 
	 */
	public void initallAccount() {
		// 1.先从数据库中读取账号信息
		NativeAccountDao dao = new NativeAccountDao(context);
		accountList.clear();
		accountList = dao.getAll();

		// 2.数据库中没有数据，从T卡读，T卡先读新文件，新文件不存在，读取老文件
		if (accountList.size() == 0) {
			accountList = TcardStorage.getAccounts();
			// 3.在从文件读取账号信息的情况下，把得到的结果先存入数据库中
			if (accountList.size() > 0) {
				for (NativeAccountInfor item : accountList) {
					dao.insert(item);
				}
			}
		}
		// 4.将最新的数据重新写入T卡中
		TcardStorage.writeDataCache(accountList);

		Util_G.debug_i(Constants.TAG1, "initallAccount更新accountList："
				+ accountList.toString());
		initLoginAccount();
	}

	/**
	 * 保存登陆账号相关信息,仅在登陆界面被调用
	 * 
	 * @param account
	 */
	public void saveLoginAccount(NativeAccountInfor maccount) {
		int i = 0;
		if (accountList.size() > 0) {
			for (NativeAccountInfor item : accountList) {
				if (!maccount.getUsn().equals(item.getUsn())) {
					i++;
				}
			}
			if (i == accountList.size()) {
				// 仅当新账号登陆时才保存
				add(maccount);
			}
		} else {
			add(maccount);
		}
		Util_G.debug_i(Constants.TAG1, "saveLoginAccount更新accountList："
				+ accountList.toString());
		initallAccount();
	}

	/**
	 * 保存内存，数据库，以及本地
	 * 
	 * @param maccount
	 */
	private void add(NativeAccountInfor maccount) {
		account = maccount;
		accountList.add(accountList.size(), maccount);
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.inserAndUpdateAll(account);

	}

	/**
	 * 当选中usn时，需要更新其它所有账号的index，同时内存中的account要改变，accountList也要改变
	 * 
	 * @param usn
	 */
	public void onAccountChoose(String usn) {
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.onAddUpdateIndexnum(usn);
		Util_G.debug_i(Constants.TAG1, "onAccountChoose更新账号：");
		initallAccount();
	}

	/**
	 * 快速登录界面登陆后会获取到绑定状态，此时需要直接修改账号绑定状态
	 * 
	 * @param status
	 */
	public void onBstatusChange(String status, String pum) {
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.updateBStatus(account.getUsn(), status, pum);
		Util_G.debug_i(Constants.TAG1, "onBstatusChange更新账号：");
		initallAccount();
	}

	/**
	 * 绑定提示框选择了取消，或者绑定界面主动退出之后，都不再提示用户绑定
	 * 
	 * @param status
	 */
	public void onNstatusChange(String status) {
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.updateNStatus(account.getUsn(), status);
		Util_G.debug_i(Constants.TAG1, "onNstatusChange更新账号：");
		initallAccount();
	}

	/**
	 * 删除某个账号，内存/数据库/本地
	 * 
	 * @param usn
	 */
	public void deleteAccount(String usn) {
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.delete(usn);
		TcardStorage.writeDataCache(dao.getAll());
		initallAccount();
	}

	/**
	 * 最近一次登陆的账号，每次账号该表都必须调用
	 */
	public void initLoginAccount() {
		if (accountList.size() > 0) {
			for (NativeAccountInfor nativeAccountInfor : accountList) {
				if (nativeAccountInfor.getIndexnum() == 1) {
					account = nativeAccountInfor;
					Util_G.debug_i(Constants.TAG1, "initLoginAccount登陆账号更新成功："
							+ account.toString());
				}
			}
		} else {
			account = null;
		}
	}

	/**
	 * 当密码修改时，调用
	 * 
	 * @param newPsd
	 */
	public void onPsdChange(String newPsd) {
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.updatePsd(account.getUsn(), newPsd);
		Util_G.debug_i(Constants.TAG1, "onPsdChange更新账号：");
		initallAccount();
	}

	/**
	 * 获取默认上一次支付的方式；若玩家没有支付过，则返回空字符串
	 * 
	 * @return
	 */
	public String getSavedPayway() {
		String ret = "";
		ret = account.getPreferpayway();
		if (StringUtil.isEmpty(ret)) {
			ret = "";
		}
		return ret;
	}

	/**
	 * 保存本次支付方式
	 * 
	 * @param payway
	 */
	public void savePayway(String payway) {
		NativeAccountDao dao = new NativeAccountDao(context);
		dao.updatePayway(account.getUsn(), payway);
		initallAccount();
	}

	public static String getSetting(Activity activity, String name) {
		String value = "";
		LocalStorage storage = LocalStorage.getInstance(activity);
		value = storage.getString(name);
		return value;

	}
	
	public static void Login() {
		if(!initalized||instance==null){
			return ;
		}
		
		instance.LoginNow();
		
	}
	
	public void LoginNow() {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);// lsl
	}
	
	/**
	 * 检查app是否安装
	 */
	private static boolean checkAppInstalled() {
		boolean installed = false;
		List<PackageInfo> packagesInfo = instance.context.getPackageManager().getInstalledPackages(0);
		for (PackageInfo packageInfo : packagesInfo) {
			if (packageInfo.packageName.equals("com.mzyw.center")) {
				installed = true;
			}
		}
		
		return installed;
	}
	
	public boolean isAppInstalled() {
		return appInstalled;
	}
	
	public void setInGameSign(boolean inGameSign) {
		this.inGameSign = inGameSign;
	}
	public void setLoginedSign(boolean loginedSign) {
		this.loginedSign = loginedSign;
	}
	@Deprecated
	public GameSDKPaymentListener getGameSDKPaymentListener() {
		return gameSDKPaymentListener;
	}
}
