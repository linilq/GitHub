package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.listener.RegisterListener;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.Helper;
import zty.sdk.utils.StringUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class ChangePsdFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private ImageView back, close, shownew, shownewconfirm;
	private EditText newPsdEt, newPsdConfirmEt;
	private Button submit_bt;
	private String newPsd, newPsdConfirm;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_changpsd"), container, false);
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
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		newPsdEt = findViewById(Helper.getResId(activity, "changpsd_psd_edx"));
		newPsdConfirmEt = findViewById(Helper.getResId(activity, "changpsd_psdconfirm_edx"));
		submit_bt = findViewById(Helper.getResId(activity, "changpsd_submit_bt"));
		shownew = findViewById(Helper.getResId(activity, "changpsd_showpsd1"));
		shownewconfirm = findViewById(Helper.getResId(activity, "changpsd_showpsd2"));

		shownew.setOnClickListener(this);
		shownewconfirm.setOnClickListener(this);
		submit_bt.setOnClickListener(this);
		back.setOnClickListener(this);
		close.setVisibility(View.GONE);
		
		newPsdEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (StringUtil.isEmpty(newPsdEt.getText().toString())) {
					shownew.setVisibility(View.GONE);
				} else {
					shownew.setVisibility(View.VISIBLE);
				}
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtil.isEmpty(newPsdEt.getText().toString())) {
					shownew.setVisibility(View.GONE);
				} else {
					shownew.setVisibility(View.VISIBLE);
				}
				
			}
		});
		newPsdConfirmEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (StringUtil.isEmpty(newPsdConfirmEt.getText().toString())) {
					shownewconfirm.setVisibility(View.GONE);
				} else {
					shownewconfirm.setVisibility(View.VISIBLE);
				}
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtil.isEmpty(newPsdConfirmEt.getText().toString())) {
					shownewconfirm.setVisibility(View.GONE);
				} else {
					shownewconfirm.setVisibility(View.VISIBLE);
				}
				
			}
		});
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

	@Override
	public void onClick(View v) {
		int tempId = v.getId();
		if (tempId == Helper.getResId(activity, "changpsd_submit_bt")) {
			newPsd = newPsdEt.getText().toString().trim();
			newPsdConfirm = newPsdConfirmEt.getText().toString().trim();
			doChangePsd(newPsd, newPsdConfirm);
		} else if (tempId == Helper.getResId(activity, "back")) {
			goback();
		} else if (tempId == Helper.getResId(activity, "changpsd_showpsd1")) {
			changeInputTransMethod(newPsdEt);
			
		} else if (tempId == Helper.getResId(activity, "changpsd_showpsd2")) {
			changeInputTransMethod(newPsdConfirmEt);
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

	private void goback() {
		QStartFrag qStartFrag = new QStartFrag();
		startFragment(qStartFrag);

	}

	private void doChangePsd(final String newpsd, String newpsdConfirm) {
		if (!StringUtil.isEmpty(newpsd) && !StringUtil.isEmpty(newpsdConfirm)
				&& newpsd.equals(newpsdConfirm)) {
			sdk.changePassword(sdk.account.getUsn(), sdk.account.getPsd(),
					newpsd, new RegisterListener() {

						@Override
						public void onRegisterSuccess(UserInfo userInfo) {
							sdk.makeToast(getResources().getString(
									Helper.getResStr(activity, "changepsd_psd_suc_str")));
							sdk.onPsdChange(newpsd);
							// 存储新账号密码 并进入登陆界面
							LoginFrag loginFrag = new LoginFrag();
							loginFrag.handler.sendEmptyMessage(2);
							startFragment(loginFrag);
						}

						@Override
						public void onFailure(int errorCode, String errorMessage) {
							sdk.makeToast(errorMessage);
						}
					});
		} else {
			sdk.makeToast(getResources().getString(
					Helper.getResStr(activity, "changepsd_psd_input_str")));
		}

	}
}
