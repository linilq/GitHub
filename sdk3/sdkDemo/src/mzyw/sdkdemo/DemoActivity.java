package mzyw.sdkdemo;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.listener.ExitListener;
import zty.sdk.listener.ExitQuitListener;
import zty.sdk.listener.GameSDKLoginListener;
import zty.sdk.utils.Util_G;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DemoActivity extends Activity {

	private Activity activity = this;
	private boolean bShowLogin = false;
	private Button login ;
	private Button levelUp;
	private Button pay;
	int k = 99;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		
		initView();
		initSDK();
	}
	
	@Override
	protected void onResume() {
		Log.i(Constants.TAG, "***************onResume");
		GameSDK.onResume();
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		Log.i(Constants.TAG, "onPause****************");
		GameSDK.onPause();
		super.onPause();
		
	}

	@Override
	public void onBackPressed() {

		//游戏退出回调
		ExitListener exitListener = new ExitListener() {
			@Override
			public void onExit(Object para) {
				// 游戏作清理退出工作
				System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		};
		//游戏退出取消，若游戏在退出时点击取消，可实现该接口恢复游戏，若不需要可不做处理
		ExitQuitListener exitQuitListener = new ExitQuitListener(){

			@Override
			public void onExitQuit(Object param) {
				// 游戏恢复工作
				Toast.makeText(activity, "游戏恢复", Toast.LENGTH_SHORT).show();
			}
			
		};
		GameSDK.afdfOut(activity, exitListener,exitQuitListener,null);
		
	}

	private void initView() {
		login = (Button) findViewById(R.id.bt_login);
		levelUp = (Button) findViewById(R.id.bt_level);
		pay = (Button) findViewById(R.id.bt_pay);
		
		disableOtherTwoFunctionBT();
		if (bShowLogin) {
			disableLoginBt();
		}
		
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
				
			}
		});
		levelUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				levelUp();
				
			}
		});
		pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pay();
				
			}
		});
	
	}
	
	private void initSDK() {
		GameSDK.initSDK(activity, new GameSDKLoginListener() {

			@Override
			public void onLoginSucess(String username, int userId, String sign) {
				Toast.makeText(activity, "登录成功", 0).show();
				disableLoginBt();
				enableOtherTwoFunctionBT();
				Util_G.debug_i(Constants.TAG1, sign);
			}

			@Override
			public void onLoginCancelled() {
				Toast.makeText(activity, "登录失败", 0).show();

			}
		}, bShowLogin);

	}
	
	private void login() {
		GameSDK.Login();
	}
	
	protected void levelUp() {
		String serverId = "1";
		int roleLevel = 21;
		String serverName = "雪之痕";
		String palyerName = "赵子龙";
		GameSDK.afdf2Self(serverId,//1 - 游戏服ID，1服为1，2服为2……
		       serverName,//2 - 游戏服名称
		       palyerName,//3 - 玩家角色名称
		       roleLevel//4 - 玩家级别
		      ); 
		
	}
	
	private void pay() {
		// 参数
					// 1 - activity
					// 2 - 游戏服ID，1服为1，2服为2……
					// 3 - 角色等级
					// 4 - 游戏服名称
					// 5 - 玩家角色名称
					// 6 - 游戏厂商的订单号
					// 7 - 支付金额
					// 8 - 游戏的兑换比例 填10就是1人民币兑换10个游戏  套餐时比例传1，（作为后台数据存储，不在sdk中显示）
					// 9 - 游戏币名称“元宝”或“金币”、套餐名称 如 “月卡” 等
					
		String serverId = "1";
		int roleLevel = 21;
		String serverName = "雪之痕";
		String palyerName = "赵子龙";
		String cpOderId = "m2016010500";
		int money = 1;
		int ratio = 10;
		String goods = "金币";
		
		GameSDK.startPay(activity, serverId, roleLevel,serverName, palyerName, cpOderId+k, money, ratio, goods);
		k++;
		/**
		 * 
		 强烈建议不适用此方法，因财付通，易宝支付没有回调，GameSDKPaymentListener不会被调用，不适合做任何逻辑处理，仅在
		 支付宝、银联、微信得以稳定回调。若使用此方法做必要的逻辑处理，建议游戏不要支持财付通、易宝支付，可与拇指平台运营同事沟通
		GameSDK.startPay(activity, serverId, roleLevel,serverName, palyerName, cpOderId, money, ratio, goods,
				new GameSDKPaymentListener() {
					@Override
					public void onPayFinished(int amount) {
						String str = String.format("支付%d元成功", amount);
						Toast.makeText(activity,str,0).show();
					}

					@Override
					public void onPayCancelled() {
						Toast.makeText(activity,"支付取消",0).show();
					}
				});
				*/
	}
	
	public void disableLoginBt() {
		login.setClickable(false);
		login.setTextColor(Color.GRAY);
	}
	
	protected void enableOtherTwoFunctionBT() {
		levelUp.setClickable(true);
		pay.setClickable(true);
		pay.setEnabled(true);
		levelUp.setEnabled(true);
		levelUp.setTextColor(Color.BLACK);
		pay.setTextColor(Color.BLACK);
	}
	
	protected void disableOtherTwoFunctionBT() {
		levelUp.setClickable(false);
		pay.setClickable(false);
		pay.setEnabled(false);
		levelUp.setEnabled(false);
		levelUp.setTextColor(Color.GRAY);
		pay.setTextColor(Color.GRAY);
		
	}
}
