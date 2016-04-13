package zty.sdk.activity;

import zty.sdk.alipay.AlixDefine;
import zty.sdk.alipay.BaseHelper;
import zty.sdk.alipay.ResultChecker;
import zty.sdk.fragment.BaseFragment;
import zty.sdk.fragment.MzMoneyChooseFrag;
import zty.sdk.fragment.MzMoneyChooseFrag.onAmountChangeListener;
import zty.sdk.fragment.MzPaywayChooseFrag;
import zty.sdk.fragment.PayChoicesFrag;
import zty.sdk.fragment.PayChoicesFrag.onPayitemClickedListener;
import zty.sdk.fragment.PayChoicesFrag.onShowOtherPayListener;
import zty.sdk.fragment.YeePayFrag;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.model.MzPayInfo;
import zty.sdk.model.OnlyPayInfo;
import zty.sdk.utils.Helper;
import zty.sdk.utils.MetricUtil;
import zty.sdk.utils.PayManager;
import zty.sdk.utils.StringUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends FragmentActivity implements
		OnClickListener, onPayitemClickedListener, onAmountChangeListener,
		onShowOtherPayListener {

	/**
	 * 直接用M币支付成功
	 */
	public final static int NOTIFY_MZ_PAY_SCC = 110;

	/**
	 * 专属币支付成功
	 */
	public final static int NOTIFY_OL_PAY_SCC = 115;

	/**
	 * 购买M币成功
	 */
	public final static int NOTIFY_MZ_SCC = 120;

	public final static String PAY_SCC = "SUCCESS";
	public final static String PAY_FAIL = "FAIL";

	private GameSDK sdk;
	private ImageView back, close;
	private FragmentManager fManager;
	private Activity payActivity;
	private BaseFragment mCurrentFragment = null;
	private TextView gNameTV, amountTV, usNameTV, mzleftTV;
	private String gameNameStr, amountStr;
	private String mzchargeStr, chargeAmountStr;
	/**
	 * 记录银联和微信支付方式，两者在onActivityResult中要用到
	 */
	private String currentPayWay = "";
	/**
	 * 标记退出支付界面，用来停止轮询线程
	 */
	private boolean pexitSign = false;
	private Handler alipayHandler = new Handler() {

		public void handleMessage(Message msg) {
			try {
				String result = (String) msg.obj;
				if (result == null)
					return;
				switch (msg.what) {
				case AlixDefine.RQF_PAY: {
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = result.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = result.indexOf("};memo=");
						tradeStatus = result.substring(imemoStart, imemoEnd);
						// 先验签通知
						ResultChecker resultChecker = new ResultChecker(result);
						int retVal = resultChecker.checkSign();
						// 验签失败
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(payActivity, "提示", "校验失败",
									android.R.drawable.ic_dialog_alert, null);
						} else {// 验签成功。验签成功后再判断交易状态码
							if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
								showPayResultDialog("", PAY_SCC);
							} else
								showPayResultDialog("支付宝支付失败!", PAY_FAIL);
						}

					} catch (Exception e) {
						Log.e(Constants.TAG, e.getMessage());
						showPayResultDialog("支付宝支付失败!", PAY_FAIL);
					}
				}
					break;
				}
			} catch (Exception e) {
				Log.e(Constants.TAG, e.getMessage());
			}
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOTIFY_MZ_PAY_SCC:
				MzPayInfo info = (MzPayInfo) msg.obj;
				int cost = info.getCost();
				sdk.mzbalance -= cost;
				showPayResultDialog("", PAY_SCC);
				break;
			case NOTIFY_MZ_SCC: {
				sdk.makeToast("您的M币充值已到账");
			}
				break;
			case NOTIFY_OL_PAY_SCC: {
				OnlyPayInfo olinfo = (OnlyPayInfo) msg.obj;
				int olcost = olinfo.getCost();
				sdk.olbalance -= olcost;
				sdk.makeToast("已扣除" + olcost + "专属币");
				PaymentActivity.this.finish();
			}
				break;
			default:
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int layout = Helper.getLayoutId(this, "activity_pay_port");
		WindowManager.LayoutParams params = getWindow().getAttributes();
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			params.width = MetricUtil.getDip(this, 320);
		}
		getWindow().setAttributes(params);
		setContentView(layout);

		initView();
		initData();
		showView();

	}

	@Override
	protected void onResume() {
		pexitSign = true;
		checkAccountBalance(PaymentActivity.this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		pexitSign = false;
		super.onPause();
	}

	private void showView() {
		PayChoicesFrag frag = new PayChoicesFrag();
		startFragment(frag);
		gNameTV.setText(gameNameStr);
		amountTV.setText(amountStr);
	}

	private void initData() {
		sdk = GameSDK.getOkInstance();
		if (sdk == null) {
			return;
		}
		gameNameStr = sdk.gameName;
		amountStr = "充值金额：" + sdk.requestAmount + "元";
		fManager = getSupportFragmentManager();
		payActivity = PaymentActivity.this;
	}

	private void initView() {
		back = (ImageView) findViewById(Helper.getResId(this, "back"));
		close = (ImageView) findViewById(Helper.getResId(this, "close"));
		gNameTV = (TextView) findViewById(Helper.getResId(this, "pay_gname_tv"));
		amountTV = (TextView) findViewById(Helper.getResId(this, "pay_amount_tv"));
		usNameTV = (TextView) findViewById(Helper.getResId(this, "pay_usname_tv"));
		mzleftTV = (TextView) findViewById(Helper.getResId(this, "pay_mzleft_tv"));

		back.setOnClickListener(this);
		close.setOnClickListener(this);
	}

	/**
	 * 访问服务器获取M币金额，大于客户端数量则提示充值成功
	 * 
	 * @param paymentActivity
	 */
	public void checkAccountBalance(PaymentActivity paymentActivity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int index = 0;
				int time = 0;
				int times[] = { 20, 40, 120, 600 };
				while (pexitSign == true) {
					if ((time % times[index]) == 0) {
						int mzbalance1 = PayManager.getNewBalance(sdk);
						if (mzbalance1 > sdk.mzbalance) {
							pexitSign = false;
							sdk.mzbalance = mzbalance1;
							// 界面修改
							Message msg = Message.obtain();
							msg.what = NOTIFY_MZ_SCC;
							handler.sendMessage(msg);
						}
						time = 0;
						index++;
						if (pexitSign == false || index > times.length - 1) {
							break;
						}
					}
					try {
						Thread.sleep(500);
						time++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pexitSign = false;
			}
		}).start();
	}
	

	public void startFragment(BaseFragment frag) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		mCurrentFragment = frag;
		ft.replace(Helper.getResId(this, "pay_container"), frag);
		ft.commit();
	}

	public static interface PKeyListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (currentPayWay.equals(PayManager.PAYWAY_WECHAT)) {// 微信的支付提示
			if (data == null) {
				return;
			}
			
			String respCode = data.getExtras().getString("resultCode");
			StringBuilder temp = new StringBuilder();
			
	        if (!TextUtils.isEmpty(respCode) 
	        		&& respCode.equalsIgnoreCase("success")){
	            //标示支付成功
	        	temp.append("交易状态:成功");
				showPayResultDialog(temp.toString(), PAY_SCC);
	            
	        }else{ 
	        	//其他状态NOPAY状态：取消支付，未支付等状态
	        	temp.append("交易状态:未支付");
				showPayResultDialog(temp.toString(), PAY_FAIL);
				
	        }
	        
		}

		if (currentPayWay.equals(PayManager.PAYWAY_UNION)) {
			if (data == null) {
				return;
			}
			String str = data.getExtras().getString("pay_result");
			if (str.equalsIgnoreCase("SUCCESS")) {
				showPayResultDialog("", PAY_SCC);
			} else if (str.equalsIgnoreCase("FAIL")) {
				showPayResultDialog("银联支付失败！", PAY_FAIL);
			} else if (str.equalsIgnoreCase("CANCEL")) {
				showPayResultDialog("银联取消支付！", PAY_FAIL);
			}
		}

	}

	@Override
	public void onClick(View v) {
		int tempId = v.getId();
		if (tempId == Helper.getResId(this, "close")) {
			// 提示退出对话框，
			showExitDialog();
		} else if (tempId == Helper.getResId(this, "back")) {
			goback();
		}

	}

	@Override
	public void onBackPressed() {
		goback();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mCurrentFragment instanceof PKeyListener) {
			return ((PKeyListener) mCurrentFragment).onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void goback() {
		if (mCurrentFragment instanceof PayChoicesFrag) {
			// 提示退出对话框，
			showExitDialog();
		} else if (mCurrentFragment instanceof YeePayFrag) {
			if (sdk.isMzCharge) {
				// 如果在易宝M币充值界面，需回到M币充值方式选择界面MzPaywayChooseFrag
				startFragment(new MzPaywayChooseFrag());
			} else {
				// 如果在其他二级支付界面，需回到一级支付界面
				YeePayFrag frag = (YeePayFrag) mCurrentFragment;
				if (frag.pop != null && frag.pop.isShowing()) {
					frag.pop.dismiss();
				}
				PayChoicesFrag payChoiceFrag = new PayChoicesFrag();
				// 发送2过去，表示仍显示所有支付方式
				payChoiceFrag.handler.sendEmptyMessage(2);
				startFragment(payChoiceFrag);
			}
		} else if (mCurrentFragment instanceof MzMoneyChooseFrag) {
			PayChoicesFrag payChoiceFrag = new PayChoicesFrag();
			// 发送2过去，表示仍显示所有支付方式
			payChoiceFrag.handler.sendEmptyMessage(2);
			startFragment(payChoiceFrag);
			usNameTV.setVisibility(View.GONE);
			mzleftTV.setVisibility(View.GONE);
			gNameTV.setText(gameNameStr);
			
			amountTV.setText(amountStr);
			
		} else if (mCurrentFragment instanceof MzPaywayChooseFrag) {
			startFragment(new MzMoneyChooseFrag());
		}
		
	}

	@Override
	public void onAmountChoose(String amount) {
		chargeAmountStr = "充值金额：" + amount + "元";
		amountTV.setText(chargeAmountStr);
	}

	@Override
	public void onPayitemClicked(boolean isMzCharge, String payway, Object obj) {
		currentPayWay = payway;
		sdk.isMzCharge = isMzCharge;
		//根据支付方式，修改布局界面。如果是横屏情况下的易宝卡类支付，需要先调整布局
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			if (payway.equals(PayManager.PAYWAY_YEE_MOBILECARD)||payway.equals(PayManager.PAYWAY_YEE_GAMECARD)) {
				//充值卡与点卡需要在这里重置界面
				reSetContentView(sdk.isMzCharge);
			}
			
		}
		if (payway.equals(PayManager.PAYWAY_YEE_MOBILECARD)) {
			// 手机卡充值支付,替换当前fragment
			YeePayFrag yeePayFrag = new YeePayFrag();
			Message msg = Message.obtain();
			if (obj != null) {
				msg.obj = obj;
			}
			msg.what = 1;
			yeePayFrag.handler.sendMessage(msg);
			startFragment(yeePayFrag);
		} else if (payway.equals(PayManager.PAYWAY_YEE_GAMECARD)) {
			// 点卡充值支付
			YeePayFrag yeePayFrag = new YeePayFrag();
			Message msg = Message.obtain();
			if (obj != null) {
				msg.obj = obj;
			}
			msg.what = 2;
			yeePayFrag.handler.sendMessage(msg);
			startFragment(yeePayFrag);
		} else if (payway.equals(PayManager.PAYWAY_MZ)) {
			// M币支付
			if (sdk.requestAmount > sdk.mzbalance) {// 如果订单所需M币大余
				// 用户所剩下的M币
				// 需要进行M币充值
				// 显示M币充值对话框
				showNegDialog(getString(Helper.getResStr(payActivity,
						"shop_mzpay_dialog_str")), payway);
			} else {// 直接用剩余M币充值
				showMzPayNoteDialog("本次支付将扣除您" + sdk.requestAmount + "M币。",
						payway);
			}
		} else if (payway.equals(PayManager.PAYWAY_ONLY)) {
			// 专属币支付
			if (sdk.requestAmount > sdk.olbalance) {// 专属币不足
				showNegDialog(getString(Helper.getResStr(payActivity,
						"shop_onlypay_dialog_str")), payway);
			} else {
				showMzPayNoteDialog("本次支付将扣除您" + sdk.requestAmount + "专属币。",
						payway);
			}
		} else {
			Handler mhandler = null;
			if (payway.equals(PayManager.PAYWAY_ALI)) {
				mhandler = alipayHandler;
			} else {
				mhandler = handler;
			}
			PayManager.pay(sdk.isMzCharge, payway, sdk, payActivity, mhandler, obj);
		}
	}

	private void showMzPayNoteDialog(String content, final String payway) {
		final Dialog dialog = new Dialog(this, Helper.getResStyle(payActivity, "CustomProgressDialog"));
		dialog.setContentView(Helper.getLayoutId(this, "dialog_mzol_neg"));
		ImageView closeIV = (ImageView) dialog
				.findViewById(Helper.getResId(this, "dialog_mzol_close_iv"));
		
		Button negBt = (Button) dialog.findViewById(Helper.getResId(this, "dialog_mzolpay_neg_bt"));
		Button posBt = (Button) dialog.findViewById(Helper.getResId(this, "dialog_mzolpay_pos_bt"));
		TextView contentTv = (TextView) dialog
				.findViewById(Helper.getResId(this, "dialog_mzol_content2_tv"));
		negBt.setText("其他方式");
		posBt.setText("确认支付");
		contentTv.setText(content);
		View.OnClickListener negListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		};
		closeIV.setOnClickListener(negListener);
		negBt.setOnClickListener(negListener);
		posBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (payway.equals(PayManager.PAYWAY_MZ)) {
					// 执行M币消费
					PayManager.mzpay(sdk, payActivity, handler);
				} else if (payway.equals(PayManager.PAYWAY_ONLY)) {
					// 执行专属币消费
					PayManager.onlypay(sdk, payActivity, handler);
				}

			}
		});

		dialog.show();
	}

	/**
	 * 显示拇指币/专属比不足时的支付对话框，
	 * 
	 * @param content
	 * @param payway
	 */
	private void showNegDialog(String content, final String payway) {
		final Dialog dialog = new Dialog(this, Helper.getResStyle(payActivity, "CustomProgressDialog"));
		dialog.setContentView(Helper.getLayoutId(this, "dialog_mzol_neg"));
		ImageView closeIV = (ImageView) dialog
				.findViewById(Helper.getResId(this, "dialog_mzol_close_iv"));
		Button negBt = (Button) dialog.findViewById(Helper.getResId(this, "dialog_mzolpay_neg_bt"));
		Button posBt = (Button) dialog.findViewById(Helper.getResId(this, "dialog_mzolpay_pos_bt"));
		TextView contentTv = (TextView) dialog
				.findViewById(Helper.getResId(this, "dialog_mzol_content2_tv"));

		if (payway.equals(PayManager.PAYWAY_ONLY)) {
			posBt.setVisibility(View.GONE);
		} else if (payway.equals(PayManager.PAYWAY_MZ)) {
			posBt.setVisibility(View.VISIBLE);
		}

		contentTv.setText(content);
		View.OnClickListener negListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		};
		closeIV.setOnClickListener(negListener);
		negBt.setOnClickListener(negListener);
		posBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (payway.equals(PayManager.PAYWAY_MZ)) {
					// 切换M币充值界面
					dialog.dismiss();
					//也需要重置界面
					reSetContentView(true);
					
					startFragment(new MzMoneyChooseFrag());
				} else if (payway.equals(PayManager.PAYWAY_ONLY)) {
					// 切换专属币充值界面,现在不做
					dialog.dismiss();
				}

			}
		});

		dialog.show();
	}

	/**
	 * 只在主动退出支付界面时调用
	 */
	private void showExitDialog() {
		final Dialog dialog = new Dialog(payActivity, Helper.getResStyle(payActivity, "CustomProgressDialog"));
		dialog.setContentView(Helper.getLayoutId(payActivity, "dialog_mzol_neg"));
		ImageView closeIV = (ImageView) dialog
				.findViewById(Helper.getResId(payActivity, "dialog_mzol_close_iv"));
		
		Button negBt = (Button) dialog.findViewById(Helper.getResId(payActivity, "dialog_mzolpay_neg_bt"));
		Button posBt = (Button) dialog.findViewById(Helper.getResId(payActivity, "dialog_mzolpay_pos_bt"));
		TextView contentTv = (TextView) dialog
				.findViewById(Helper.getResId(payActivity, "dialog_mzol_content2_tv"));

		contentTv.setText("确认退出，返回游戏？");
		negBt.setTextColor(Color.parseColor("#686868"));
		negBt.setText("确认退出");
		negBt.setBackgroundDrawable(getResources().getDrawable(
				Helper.getResDraw(payActivity, "yeepay_grid_item_normal_bg")));

		posBt.setText("继续支付");
		View.OnClickListener negListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				sdk.isMzCharge = false;
				checkNotify(PAY_FAIL);
				payActivity.finish();
			}
		};
		View.OnClickListener posListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		};
		closeIV.setOnClickListener(posListener);
		negBt.setOnClickListener(negListener);
		posBt.setOnClickListener(posListener);

		dialog.show();

	}

	/**
	 * 充值失败或取消时的对话框
	 */
	private void showPayResultDialog(String content, final String result) {
		final Dialog dialog = new Dialog(this, Helper.getResStyle(payActivity, "CustomProgressDialog"));
		dialog.setContentView(Helper.getLayoutId(this, "dialog_payresult"));

		ImageView closeIV = (ImageView) dialog
				.findViewById(Helper.getResId(this, "dialog_pay_close_iv"));
		ImageView resultIcon = (ImageView) dialog
				.findViewById(Helper.getResId(this, "dialog_pay_resulticon_iv"));
		Button negBt = (Button) dialog.findViewById(Helper.getResId(this, "dialog_pay_neg_bt"));
		Button posBt = (Button) dialog.findViewById(Helper.getResId(this, "dialog_pay_pos_bt"));
		TextView contentTv = (TextView) dialog
				.findViewById(Helper.getResId(this, "dialog_pay_content_tv"));
		TextView titleTv = (TextView) dialog
				.findViewById(Helper.getResId(this, "dialog_pay_title_tv"));
		int drawId = -1;
		String title = "支付成功";
		if (result.equals(PAY_SCC)) {
			drawId = Helper.getResDraw(payActivity, "pay_succ_icon");
			content = "祝您游戏愉快！";
			posBt.setVisibility(View.GONE);
		} else {
			drawId = Helper.getResDraw(payActivity, "pay_fail_icon");
			contentTv.setTextColor(Color.RED);
			title = "下单失败";
		}

		titleTv.setText(title);
		
		resultIcon.setBackgroundDrawable(payActivity.getResources()
				.getDrawable(drawId));
		if (content.length() > 10) {
			contentTv.setTextSize(12f);
		}
		contentTv.setText(content);

		View.OnClickListener posListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				//同样要判断是否为拇指币充值
				onShowOtherPay();
			}
		};

		closeIV.setOnClickListener(posListener);
		posBt.setOnClickListener(posListener);
		negBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				//根据支付结果判定是否通知cp的回调接口
				checkNotify(result);
				payActivity.finish();

			}
		});

		dialog.show();
	}
	@Deprecated
	private void checkNotify(String result) {
		if(sdk.getGameSDKPaymentListener()!=null){
			if (result.equals(PAY_SCC)) {
				int amount;
				if (sdk.isMzCharge) {
					amount = sdk.mzcharge;
				}else{
					amount = sdk.requestAmount;
				}
				sdk.getGameSDKPaymentListener().onPayFinished(amount);
			} else {
				sdk.getGameSDKPaymentListener().onPayCancelled();
			}
		}
	}
	@Override
	public void onShowOtherPay() {

		reSetContentView(sdk.isMzCharge);
		if (!sdk.isMzCharge) {
			PayChoicesFrag frag = new PayChoicesFrag();
			frag.handler.sendEmptyMessage(2);
			startFragment(frag);
		}else{
			startFragment(new MzPaywayChooseFrag());
		}
		
		
	}

	public void reSetContentView(boolean isMzCharge) {
		int layout = Helper.getLayoutId(this, "activity_pay_land");
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			layout = Helper.getLayoutId(this, "activity_pay_port");
		}
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);
		setContentView(layout);
		initView();
		initData();
		
		if(isMzCharge){
			//充值拇指币时,需要多显示两个信息，账号与拇指币余额
			usNameTV.setText(Html.fromHtml("账号："
					+ "<font color=\"red\">" + sdk.account.getUsn()
					+ "</front>"));
			mzleftTV.setText(Html.fromHtml("余额："
					+ "<font color=\"blue\">" + sdk.mzbalance
					+ "M币</front>"));
			usNameTV.setVisibility(View.VISIBLE);
			mzleftTV.setVisibility(View.VISIBLE);
			if (StringUtil.isEmpty(mzchargeStr)) {
				mzchargeStr = "M币充值";
			}
			if (StringUtil.isEmpty(chargeAmountStr)) {
				chargeAmountStr = "充值金额：20元";
			}
			gNameTV.setText(mzchargeStr);
			amountTV.setText(chargeAmountStr);
		}else{
			//充值游戏时，不需要做更多事情
//			usNameTV.setVisibility(View.GONE);
//			mzleftTV.setVisibility(View.GONE);
			gNameTV.setText(gameNameStr);
			amountTV.setText(amountStr);
		}
		
	}

}
