package zty.sdk.fragment;

import zty.sdk.game.GameSDK;
import zty.sdk.listener.RegisterListener;
import zty.sdk.model.NativeAccountInfor;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.Helper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NewerFragment extends BaseFragment {
	private ImageView back, close, qregistIV, registIV, loginIV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_newer"), container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();

	}

	private void initView() {
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		qregistIV = findViewById(Helper.getResId(activity, "f_newer_qregist"));
		registIV = findViewById(Helper.getResId(activity, "f_newer_regist"));
		loginIV = findViewById(Helper.getResId(activity, "f_newer_login"));

		back.setVisibility(View.GONE);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
		qregistIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				quickRegist();

			}
		});

		registIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goRegist();

			}
		});
		
		loginIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goLogin();
				
			}
		});

	}

	protected void goLogin() {
		LoginFrag loginFrag = new LoginFrag();
		loginFrag.handler.sendEmptyMessage(1);
		startFragment(loginFrag);
		
	}

	protected void goRegist() {
		// 进入注册界面；(不显示公告，不显示绑定提示由fragment去控制)
		RegistFrag registFrag = new RegistFrag();
		// 不存在账号信息，进入注册界面不需要有返回按钮
		registFrag.handler.sendEmptyMessage(1);
		startFragment(registFrag);

	}

	protected void quickRegist() {
		String usn = "m" + GameSDK.getNo(9);
		final String psd = GameSDK.getNo(6);
		sdk.register(usn, psd, new RegisterListener() {

			@Override
			public void onRegisterSuccess(UserInfo userInfo) {
				// 注册成功，存储账号信息以及绑定状态
				// DialogUtil.closeProgressDialog();

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
				// DialogUtil.closeProgressDialog();
				sdk.makeToast(errorMessage);
			}
		}, activity, true);

	}
}
