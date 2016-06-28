package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.listener.IdentifyCodeListener;
import zty.sdk.listener.LoginListener;
import zty.sdk.listener.RequestCodeListener;
import zty.sdk.model.IdentifyCode;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.DialogUtil;
import zty.sdk.utils.DialogUtil.DialogCallBack;
import zty.sdk.utils.Helper;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.Util_G;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BindFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private ImageView back, close;
	private Button getCode, submit;
	private EditText pnumEt, codeEt;
	private TextView unbindText;
	private LinearLayout bind_phnum_layout;
	public static final int ORDER_CountTime = 10;
	public static final int NOTIFY_finish = 10;
	public IdentifyCode icode;
	private String phnum;
	/**
	 * 标记是否成功执行过获取验证码
	 */
	private boolean doneGetCode = false;
	/**
	 * 短信等待时间，会在每次请求时都预设为120秒
	 */
	private int time;
	/**
	 * 标记来源，1表示来自快速登陆执行绑定；2表示来自已绑定界面，执行解绑操作
	 */
	private int comeFrom;

	private static int _LOGIN = 1;
	private static int _BACK = 2;
	private static int _RESET = 3;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_bind"),
				container, false);

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {

	}

	private void initView() {
		Util_G.debug_i("LINILQTEST", "comeFrom：" + comeFrom);
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		getCode = findViewById(Helper.getResId(activity, "bind_getcode_bt"));
		submit = findViewById(Helper.getResId(activity, "bind_submit_bt"));
		pnumEt = findViewById(Helper.getResId(activity, "bind_phnum_et"));
		codeEt = findViewById(Helper.getResId(activity, "bind_code_et"));
		bind_phnum_layout = findViewById(Helper.getResId(activity,
				"bind_phnum_layout"));
		unbindText = findViewById(Helper.getResId(activity,
				"bind_unbind_notice_tv"));

		close.setVisibility(View.GONE);
		back.setOnClickListener(this);
		getCode.setOnClickListener(this);
		submit.setOnClickListener(this);

		if (comeFrom == 1) {
			// 来自快速登陆界面，执行绑定
			bind_phnum_layout.setVisibility(View.VISIBLE);
			unbindText.setVisibility(View.GONE);
			submit.setBackgroundDrawable(getResources().getDrawable(
					Helper.getResDraw(activity, "bind_submit_btn")));
		} else if (comeFrom == 2) {
			bind_phnum_layout.setVisibility(View.GONE);
			unbindText.setVisibility(View.VISIBLE);
			submit.setBackgroundDrawable(getResources().getDrawable(
					Helper.getResDraw(activity, "findpsd_submit_btn")));

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goback();
			break;

		default:
			break;
		}
		return false;
	}

	public void goback() {
		boolean hasMSG = handler.hasMessages(ORDER_CountTime);
		if(hasMSG)
			handler.removeMessages(ORDER_CountTime);
		
		if (comeFrom == 1) {
			QStartFrag qStartFrag = new QStartFrag();
			startFragment(qStartFrag);
		} else if (comeFrom == 2) {
			BindedFrag bindedFrag = new BindedFrag();
			startFragment(bindedFrag);
		}

	}

	@Override
	public void onClick(View v) {
		int tempId = v.getId();
		if (tempId == Helper.getResId(activity, "bind_getcode_bt")) {
			// 获取验证码
			if (comeFrom == 1) {
				phnum = pnumEt.getText().toString().trim();
				requestCode(Constants.ACTION_BIND);
			} else if (comeFrom == 2) {
				phnum = sdk.account.getPnum();
				requestCode(Constants.ACTION_UNBIND);
			}

		} else if (tempId == Helper.getResId(activity, "bind_submit_bt")) {
			// 提交验证码
			if (doneGetCode) {
				String code = codeEt.getText().toString().trim();
				if (StringUtil.isEmpty(code) || code.length() != 6) {
					sdk.makeToast(getResources().getString(
							Helper.getResStr(activity, "bind_promit_03")));
				} else
					submitCode(code);
			} else
				sdk.makeToast(getResources().getString(
						Helper.getResStr(activity, "bind_commit_error_str")));

		} else if (tempId == Helper.getResId(activity, "back")) {
			goback();
		}

	}

	private void requestCode(String action) {
		Util_G.debug_i("LINILQTEST", "获取验证码");
		time = 120;// 先预设等待时间为120秒
		if (!StringUtil.isEmpty(phnum) && phnum.length() == 11) {
			getCode.setBackgroundDrawable(activity.getResources().getDrawable(
					Helper.getResDraw(activity, "bind_getcode_bg3")));
			getCode.setClickable(false);
			sdk.getIdenticode(sdk.account.getUsn(), phnum,
					sdk.account.getPsd(), action, new RequestCodeListener() {

						@Override
						public void onRequestCodeSuccess(IdentifyCode code) {
							// 获取验证码成功后开始计时
							handler.sendEmptyMessageDelayed(ORDER_CountTime,
									1000);
							sdk.makeToast(activity.getResources().getString(
									Helper.getResStr(activity,
											"bind_verc_commit_succ_str")));
							doneGetCode = true;
							icode = code;
						}

						@Override
						public void onRequestCodeFailure(String errorCode,
								String Message) {
							sdk.makeToast(Message);
							sendOrderNext(_RESET);
						}

					}, activity);
		} else {
			sdk.makeToast(getResources().getString(
					Helper.getResStr(activity, "bind_pnum_error_str")));
		}

	}

	public void submitCode(String code) {
		icode.setCode(code);
		if (comeFrom == 1) {// 绑定
			sdk.doIdentifyCode(icode, Constants.ACTION_BIND,
					new IdentifyCodeListener() {

						@Override
						public void onIdentifyCodeSuccess(IdentifyCode obj) {
							// 绑定成功之后
							// 1.修改账号状态
							sdk.onBstatusChange("true", phnum);
							// 2.停止计时，并且登陆
							sendOrderNext(_LOGIN);
						}

						@Override
						public void onIdentifyCodeFailure(String errorCode,
								String Message) {
							sdk.makeToast(Message);
							// 让计时停止，并且修改验证码获取状态，方便用户重新获取验证码
							sendOrderNext(_RESET);
						}
					},activity);
		} else if (comeFrom == 2) {// 解除绑定
			sdk.doIdentifyCode(icode, Constants.ACTION_UNBIND,
					new IdentifyCodeListener() {

						@Override
						public void onIdentifyCodeSuccess(IdentifyCode obj) {
							// 解除绑定之后
							// 1.修改账号状态
							sdk.onBstatusChange("false", "");
							// 2.停止计时
							sendOrderNext(_BACK);

						}

						@Override
						public void onIdentifyCodeFailure(String errorCode,
								String Message) {
							sdk.makeToast(Message);
							sendOrderNext(_RESET);
						}
					},activity);
		}
	}

	/**
	 * 1、停止计时，重置状态
	 * 2、重置验证码获取按钮，
	 * 3、执行next命令；
	 * 4、停止倒计时
	 * 
	 * @param next
	 */
	public void sendOrderNext(int next) {
		doneGetCode = false;
		time = 0;
		enableGetCodeBt();
		excuteOrder(next);
		
		handler.removeMessages(ORDER_CountTime);

		
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what < 3) {
			comeFrom = msg.what;
		}
		switch (msg.what) {
		case ORDER_CountTime:
			Util_G.debug_i("LINILQTEST", "倒计时" + time);
			if (time > 0) {
				time--;
				getCode.setTextSize(12f);
				String timeStr = String.format(
						getResources().getString(
								Helper.getResStr(activity,
										"bind_getcode_bt_str2")), time);
				getCode.setText(timeStr);
				handler.sendEmptyMessageDelayed(ORDER_CountTime, 1000);
			} else {
				// 当time=0时，表明用户在获取验证码之后一直未输入
				sdk.makeToast(getResources().getString(
						Helper.getResStr(activity,
								"bind_getcode_bt_str3")));
				enableGetCodeBt();
			}

			break;

		default:
			break;
		}
		return false;
	}

	public void enableGetCodeBt() {
		getCode.setTextSize(14f);
		getCode.setBackgroundDrawable(activity.getResources().getDrawable(
				Helper.getResDraw(activity, "bind_getcode_bg")));
		getCode.setText(activity.getResources().getString(
				Helper.getResStr(activity, "bind_getcode_bt_str")));
		getCode.setClickable(true);
	}

	/**
	 * msg.obj=1则继续登录账号; msg.obj=2则会去向快速登录; msg.obj=3哪也不去
	 * 
	 * @param msg
	 */
	public void excuteOrder(int next) {
		if (next == _LOGIN) {
			doLogin();
		} else if (next == _BACK) {
			DialogCallBack callback = new DialogCallBack() {

				@Override
				public void callBack() {
					// 进入快速登陆界面
					QStartFrag qStartFrag = new QStartFrag();
					startFragment(qStartFrag);
				}
			};
			DialogUtil.showNormalDialog(
					activity,
					activity.getResources().getString(
							Helper.getResStr(activity,
									"unbindsuc_dialog_notice_str")),
					new DialogCallBack[] { callback });

		} else if (next == _RESET) {

		}

	}

	public void doLogin() {
		// 3.执行登陆
		sdk.login(sdk.account.getUsn(), sdk.account.getPsd(),
				new LoginListener() {

					@Override
					public void onLoginSuccess(UserInfo userInfo) {

						// 此时不再需要改变状态，绑定成功的时候已经修改过
						sdk.notifyLoginSuccess(userInfo.getLoginAccount(),
								userInfo.getUserId(), userInfo.getSign());
						activity.finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMessage) {
						sdk.makeToast(errorMessage + "...请重新登陆。");
						LoginFrag loginFrag = new LoginFrag();
						loginFrag.handler.sendEmptyMessage(2);
						startFragment(loginFrag);
					}
				}, activity, true);

	}
}
