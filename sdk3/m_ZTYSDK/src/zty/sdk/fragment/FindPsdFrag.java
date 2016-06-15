package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.listener.FindPswListener;
import zty.sdk.utils.Helper;
import zty.sdk.utils.StringUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FindPsdFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private ImageView back, close;
	private EditText pnumEt;
	private Button submit_bt;
	private TextView cs_tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_findpsd"), container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		pnumEt = findViewById(Helper.getResId(activity, "findpsd_pnum_et"));
		submit_bt = findViewById(Helper.getResId(activity, "findpsd_submit_bt"));
		cs_tv = findViewById(Helper.getResId(activity, "findpsd_cs_tv"));

		close.setVisibility(View.GONE);
		back.setOnClickListener(this);
		submit_bt.setOnClickListener(this);
		cs_tv.setOnClickListener(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goBack();
			break;

		default:
			break;
		}
		return false;
	}

	private void goBack() {
		LoginFrag loginFrag = new LoginFrag();
		loginFrag.handler.sendEmptyMessage(3);
		startFragment(loginFrag);

	}

	@Override
	public void onClick(View v) {
		int tempId = v.getId();
		if (tempId == Helper.getResId(activity, "back")) {
			goBack();
		} else if (tempId == Helper.getResId(activity, "findpsd_submit_bt")) {
			// 找回密码提交，先在本地验证他的手机号码是不是正确的
			String pNum = pnumEt.getText().toString().trim();
			if (StringUtil.isEmpty(pNum) || pNum.length() != 11) {
				sdk.makeToast(activity.getResources().getString(Helper.getResStr(activity, "bind_pnum_error_str")));
			}else{
				sdk.sendFindpswReq(pNum, new FindPswListener() {
					
					@Override
					public void onFindPswSuccess(String msg) {
						sdk.makeToast(activity.getResources().getString(Helper.getResStr(activity, "login_find_psd_suc_str")));
						goBack();
					}
					
					@Override
					public void onFindPswFailure(String Message) {
						sdk.makeToast(activity.getResources().getString(Helper.getResStr(activity, "login_find_psd_fail_str")));
						
					}
				},activity);
			}
		} else if (tempId == Helper.getResId(activity, "findpsd_cs_tv")) {
			startFragment(new ContactusFrag());
		}

	}
}
