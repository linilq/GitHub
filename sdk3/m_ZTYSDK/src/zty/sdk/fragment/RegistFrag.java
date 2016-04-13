package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.listener.RegisterListener;
import zty.sdk.model.NativeAccountInfor;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.Helper;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.Util_G;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegistFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private EditText usnEt, psdEt;
	private ImageView back, close, showPsd,delete;
	private Button regist_bt;
	private TextView regist_quick_tv, toLogin_tv;
	/**
	 * 标记从哪里来的，1代表新玩家第一次进入,2代表玩家从登陆进入
	 */
	private int comeFrom;
	private String usn = "";
	private String psd = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_regist"), container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {

	}

	public void initView() {
		usnEt = (EditText) findViewById(Helper.getResId(activity, "regist_name_edx"));
		psdEt = (EditText) findViewById(Helper.getResId(activity, "regist_psd_edx"));
		regist_bt = (Button) findViewById(Helper.getResId(activity, "regist_bt"));
		regist_quick_tv = (TextView) findViewById(Helper.getResId(activity, "regist_quick_tv"));
		toLogin_tv = (TextView) findViewById(Helper.getResId(activity, "regist_tologin_tv"));
		back = (ImageView) findViewById(Helper.getResId(activity, "back"));
		close = (ImageView) findViewById(Helper.getResId(activity, "close"));
		showPsd = (ImageView) findViewById(Helper.getResId(activity, "regist_showpsd"));
		delete = (ImageView) findViewById(Helper.getResId(activity, "regist_delete_iv"));

		regist_bt.setOnClickListener(this);
		regist_quick_tv.setOnClickListener(this);
		toLogin_tv.setOnClickListener(this);
		back.setOnClickListener(this);
		close.setOnClickListener(this);
		showPsd.setOnClickListener(this);
		delete.setOnClickListener(this);

		close.setVisibility(View.GONE);
		if (comeFrom == 1) {
			// 新玩家第一次进入不显示此按键
			
			toLogin_tv.setVisibility(View.VISIBLE);
		} else if (comeFrom == 2) {
			back.setVisibility(View.VISIBLE);
			// 从登陆界面进入不显示此按键
			close.setVisibility(View.GONE);
			toLogin_tv.setVisibility(View.GONE);
		}
		
		usnEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Util_G.debug_i(Constants.TAG1, "beforeTextChanged");
				if (StringUtil.isEmpty(usnEt.getText().toString())) {
					delete.setVisibility(View.GONE);
				} else {
					delete.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				Util_G.debug_i(Constants.TAG1, "afterTextChanged");
				if (StringUtil.isEmpty(usnEt.getText().toString())) {
					delete.setVisibility(View.GONE);
				} else {
					delete.setVisibility(View.VISIBLE);
				}
			}
		});
		psdEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (StringUtil.isEmpty(psdEt.getText().toString())) {
					showPsd.setVisibility(View.GONE);
				} else {
					showPsd.setVisibility(View.VISIBLE);
				}
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtil.isEmpty(psdEt.getText().toString())) {
					showPsd.setVisibility(View.GONE);
				} else {
					showPsd.setVisibility(View.VISIBLE);
				}
				
			}
		});

	}

	@Override
	public void onClick(View v) {
		int temId = v.getId();

		if (temId == Helper.getResId(activity, "regist_bt")) {
			// 执行注册
			sdk.makeToast("执行注册");
			beginNormalRegist();
		} else if (temId == Helper.getResId(activity, "regist_quick_tv")) {
			// 快速注册
			sdk.makeToast("执行注册");
			beginQuickRegist();
		} else if (temId == Helper.getResId(activity, "regist_tologin_tv")) {
			// 去登陆界面，（从登陆界面进入不显示此按键）
			LoginFrag loginFrag = new LoginFrag();
			loginFrag.handler.sendEmptyMessage(1);
			startFragment(loginFrag);
		} else if (temId == Helper.getResId(activity, "back")) {
			// 返回上一级
			goback();
		} else if (temId == Helper.getResId(activity, "regist_showpsd")) {
			changeInputTransMethod(psdEt);
		}else if (temId == Helper.getResId(activity, "regist_delete_iv")) {
			psdEt.setText("");
			usnEt.setText("");
			psdEt.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			
		}

	}

	public void changeInputTransMethod(EditText et) {
		TransformationMethod md = et.getTransformationMethod();
		if (md instanceof HideReturnsTransformationMethod) {
			et.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}else if (md instanceof PasswordTransformationMethod) {
			et.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		}
	}
	/**
	 * 当注册界面是老玩家由登陆界面进入时，comeFrom=2
	 */
	public void goback() {
		if (comeFrom==1) {
			NewerFragment newerFragment = new NewerFragment();
			startFragment(newerFragment);
		}else{
			LoginFrag loginFrag = new LoginFrag();
			loginFrag.handler.sendEmptyMessage(2);
			startFragment(loginFrag);
		}
		
	}

	private void beginQuickRegist() {
		usn = "m" + GameSDK.getNo(9);
		psd = GameSDK.getNo(6);
		doRegist(true);
	}

	private void beginNormalRegist() {
		usn = usnEt.getText().toString().trim();
		psd = psdEt.getText().toString().trim();
		if (StringUtil.isEmpty(usn) || StringUtil.isEmpty(psd)) {
			sdk.makeToast(getResources().getString(
					Helper.getResStr(activity, "regist_commit_notice1")));
		} else if (psd.trim().length() < 6) {
			sdk.makeToast(getResources().getString(
					Helper.getResStr(activity, "regist_commit_notice2")));
		} else {
			// 开始注册，显示进度
			doRegist(false);
		}

	}

	/**
	 * 执行注册
	 * @param isAuto  false表示主动注册，true表示一键注册
	 */
	public void doRegist(boolean isAuto) {
//		DialogUtil.showProgressDialog(
//				activity,
//				getResources().getString(
//						Helper.getResStr(activity, "regist_promit_str02")), 0);
		sdk.register(usn, psd, new RegisterListener() {

			@Override
			public void onRegisterSuccess(UserInfo userInfo) {
				// 注册成功，存储账号信息以及绑定状态
//				DialogUtil.closeProgressDialog();
				
				NativeAccountInfor account = new NativeAccountInfor();
				account.setUsn(userInfo.getLoginAccount());
				account.setPsd(psd);
				account.setBstatus(userInfo.getB_status());
				sdk.saveLoginAccount(account);
				
				sdk.notifyLoginSuccess(userInfo.getLoginAccount(),
						userInfo.getUserId(), userInfo.getSign());
				activity.finish();
			}

			@Override
			public void onFailure(int errorCode, String errorMessage) {
//				DialogUtil.closeProgressDialog();
				sdk.makeToast(errorMessage);
			}
		}, activity,isAuto);
	}

	@Override
	public boolean handleMessage(Message msg) {
		comeFrom = msg.what;
		return super.handleMessage(msg);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goback();
			break;
		}
		return false;
	}
}
