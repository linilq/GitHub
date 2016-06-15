package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.game.Constants;
import zty.sdk.listener.LoginListener;
import zty.sdk.model.NativeAccountInfor;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.DialogUtil;
import zty.sdk.utils.DialogUtil.DialogCallBack;
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

/**
 * 登陆界面
 * 
 * @author Administrator
 * 
 */
public class LoginFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private ImageView back, close, showPsd, delete;
	private EditText usn_et, psd_et;
	private Button login_bt;
	private TextView fpsd_tv, regist_tv;
	private String usnStr, psdStr;
	private boolean doLogin = true;
	private static int ORDER_AUTOLOGIN = 100;
	/**
	 * 标记从那个界面过来的，1代表新玩家从三选项界面过来， 2代表从快速登陆界面 或 从修改密码完成后自动进入
	 */
	private int comeFrom;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(Helper.getLayoutId(activity, "f_login"), container, false);
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
		showView();
	}

	private void showView() {
		if (comeFrom == 2) {
			if(sdk.account!=null){
				usn_et.setText(sdk.account.getUsn());
				psd_et.setText(sdk.account.getPsd());
			}
		}

	}

	private void initData() {

	}

	private void initView() {
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		delete = findViewById(Helper.getResId(activity, "login_delete_iv"));
		usn_et = findViewById(Helper.getResId(activity, "login_name_et"));
		psd_et = findViewById(Helper.getResId(activity, "login_psd_et"));
		login_bt = findViewById(Helper.getResId(activity, "login_bt"));
		fpsd_tv = findViewById(Helper.getResId(activity, "login_fpsd_tv"));
		regist_tv = findViewById(Helper.getResId(activity, "login_regist_tv"));
		showPsd = findViewById(Helper.getResId(activity, "login_showpsd_iv"));

		if (comeFrom == 1) {
			regist_tv.setVisibility(View.GONE);
		} else if (comeFrom == 2) {
			regist_tv.setVisibility(View.VISIBLE);
		}

		back.setOnClickListener(this);
		close.setVisibility(View.GONE);
		usn_et.setOnClickListener(this);
		psd_et.setOnClickListener(this);
		login_bt.setOnClickListener(this);
		fpsd_tv.setOnClickListener(this);
		regist_tv.setOnClickListener(this);
		showPsd.setOnClickListener(this);
		delete.setOnClickListener(this);

		usn_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Util_G.debug_i(Constants.TAG1, "beforeTextChanged");
				if (StringUtil.isEmpty(usn_et.getText().toString())) {
					delete.setVisibility(View.GONE);
				} else {
					delete.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				Util_G.debug_i(Constants.TAG1, "afterTextChanged");
				if (StringUtil.isEmpty(usn_et.getText().toString())) {
					delete.setVisibility(View.GONE);
				} else {
					delete.setVisibility(View.VISIBLE);
				}
			}
		});
		psd_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (StringUtil.isEmpty(psd_et.getText().toString())) {
					showPsd.setVisibility(View.GONE);
				} else {
					showPsd.setVisibility(View.VISIBLE);
				}
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtil.isEmpty(psd_et.getText().toString())) {
					showPsd.setVisibility(View.GONE);
				} else {
					showPsd.setVisibility(View.VISIBLE);
				}
				
			}
		});

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == Helper.getResId(activity, "back")) {
			goback();
		} else if (id == Helper.getResId(activity, "login_bt")) {
			// 执行登陆
			beginLogin();
		} else if (id == Helper.getResId(activity, "login_fpsd_tv")) {
			// 进入密码找回
			startFragment(new FindPsdFrag());
		} else if (id == Helper.getResId(activity, "login_regist_tv")) {
			if (comeFrom == 2) {
				// 从快速登陆界面过来的
				RegistFrag registFrag = new RegistFrag();
				registFrag.handler.sendEmptyMessage(2);
				startFragment(registFrag);
			}
		} else if (id == Helper.getResId(activity, "login_showpsd_iv")) {
			// 显隐密码
			changeInputTransMethod(psd_et);

		} else if (id == Helper.getResId(activity, "login_delete_iv")) {
			psd_et.setText("");
			usn_et.setText("");
			psd_et.setTransformationMethod(PasswordTransformationMethod
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
	
	private void beginLogin() {
		doLogin = true;
		usnStr = usn_et.getText().toString().trim();
		psdStr = psd_et.getText().toString().trim();

		if (StringUtil.isEmpty(usnStr) || StringUtil.isEmpty(psdStr)) {
			sdk.makeToast(getResources().getString(
					Helper.getResStr(activity, "login_commit_notice1")));
		} else {
			DialogUtil.showProgressDialog(activity, "登录", 0, new DialogCallBack() {

				@Override
				public void callBack() {
					DialogUtil.closeProgressDialog();
					if (sdk.mRequest != null) {
						// 2秒后，当登录任务创建时取消任务
						sdk.mRequest.cancelTask();
						sdk.mRequest = null;
					} else {
						// 2秒内，任务可以不执行
						doLogin = false;
					}

				}
			});
			handler.sendEmptyMessageDelayed(ORDER_AUTOLOGIN, 2000);
		}

	}

	public void goback() {
		if (comeFrom == 1) {
			// 新玩家从注册界面进来
			NewerFragment newerFrag = new NewerFragment();
			startFragment(newerFrag);
		} else if (comeFrom == 2) {
			// 从快速登陆界面过来的
			QStartFrag qStartFrag = new QStartFrag();
			startFragment(qStartFrag);
		}else if (comeFrom == 3) {
			// 从找回密码界面过来的
			if(sdk.account!=null){
				QStartFrag qStartFrag = new QStartFrag();
				startFragment(qStartFrag);
			}else{
				NewerFragment newerFrag = new NewerFragment();
				startFragment(newerFrag);
			}
			
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == ORDER_AUTOLOGIN && doLogin) {
			sdk.login(usnStr, psdStr, new LoginListener() {

				@Override
				public void onLoginSuccess(UserInfo userInfo) {
					DialogUtil.closeProgressDialog();
					NativeAccountInfor account = new NativeAccountInfor();
					account.setBstatus(userInfo.getB_status());
					account.setUsn(usnStr);
					account.setPsd(psdStr);
					account.setPnum(userInfo.getPnum());
					sdk.saveLoginAccount(account);
					activity.finish();
					sdk.notifyLoginSuccess(usnStr, userInfo.getUserId(),
							userInfo.getSign());
				}

				@Override
				public void onFailure(int errorCode, String errorMessage) {
					DialogUtil.closeProgressDialog();
					sdk.makeToast(errorMessage);
				}
			}, activity, false);
		}else if(msg.what<100){
			comeFrom = msg.what;
		}
		return super.handleMessage(msg);
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
}
