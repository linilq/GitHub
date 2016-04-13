package zty.sdk.game;

public class Constants {
	// 网游
	
//	public static final String GAME_SDK_VERSION = "3.01.1";// 
//	public static final String GAME_SDK_VERSION = "3.01.3";// 接口优化
//	public static final String GAME_SDK_VERSION = "3.01.4";// 专属币BUG 修复、老用户登陆支持、充值拇指币bug修复
//	public static final String GAME_SDK_VERSION = "3.01.5";// 第一界面修改,动态获取资源ID
//	public static final String GAME_SDK_VERSION = "3.01.6";// 账号登陆顺序管理bug修复
	public static final String GAME_SDK_VERSION = "3.01.7";// 微信支付修改为威富通
	
	public static final String STAND_ALONE_MK = "2";
	public static final int DEBUG = 0;
	

	public static String SERVER_URL = "http://gm.91muzhi.com:8080/sdk";//"http://192.168.0.123:8080/sdk";//"http://183.232.69.61:8080";// "http://sa.91muzhi.com:8091/sdk/%s";//广州
	
	public static String OSERVER_URL = "http://gm.91muzhi.com:8080/sdk";//"http://192.168.0.123:8080/sdk";//"http://gm.91muzhi.com:8090/sdk";

	public static final String ON = "1";

	public static final String TAG = "GameSDK";
	public static final String DEVICE_ID = "device_id";
	public static final String URL = "url";
	public static final String DisCon = "discon";
	public static final String DisCon2 = "discon2";
	public static final String DisUrl = "disurl";// 登录公告
	public static final String MK = "MK";
	public static final String MKUrl = "MKUrl";

	public static final String LOGIN_ACCOUNT = "login_account";
	public static final String PASSWORD = "password";

	// /////////**********2015-03-25******////////////////////
	public static final String BINDING_STATUS = "binding_status";
	public static final String TAG1 = "LINILQTEST";// 2015-03-25
	public static final String NOTICE_STATUS = "notice_status";

	public static final String PAYWAY = "payway";
	// /////////**********2015-03-25******////////////////////
	// ////////**********2015-11-11*******/////////////
	public static final String ACTION_BIND = "bind";
	public static final String ACTION_UNBIND = "unbind";
	// ///////********2015-11-11*******////////////
	public static String oldDataPath = "/sdcard/mzyw.data";
	public static String newDataPath = "/sdcard/mzywnew.data";
	public static final String AMOUNT = "amount";
	public static final String MAX_AMOUNT = "max_amount";

	public static int ERROR_CODE_NET = -2;
	public static int ERROR_CODE_SYS = -1;
	/**
	 * 纵游一卡通
	 */
	public static String ZYCARD_PAY = "zycard";
	/**
	 * 骏网一卡通
	 */
	public static String JWCARD_PAY = "jcard";
	/**
	 * 32卡
	 */
	public static String TSCARD_PAY = "tscard";

	/**
	 * 退出当前的activeTy
	 */
	public static final String BROADCAST_COMMOND_EXIT = "broadcast_commond_exit";

	/**
	 * 用于实例化fragment后 传递参数的通知
	 */
	public static final int FRAGMENT_TRANSMIT_MSG = 1000;
}
