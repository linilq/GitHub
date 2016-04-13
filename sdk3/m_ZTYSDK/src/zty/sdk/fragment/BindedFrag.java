package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.utils.Helper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BindedFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private ImageView back, close;
	private Button unbind;
	private TextView bindedPnumTV;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_binded"), container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
		showView();
	}

	private void showView() {
		// 由sdk获取当前账号手机号，然后显示出来；
		String pnum = sdk.account.getPnum();
		pnum = pnum.replace(pnum.substring(3, 7), "****");
		bindedPnumTV.setText("已绑定手机："+pnum);

	}

	private void initData() {

	}

	private void initView() {
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		unbind = findViewById(Helper.getResId(activity, "binded_unbind_bt"));
		bindedPnumTV = findViewById(Helper.getResId(activity, "binded_bindpnum_tv"));

		close.setVisibility(View.GONE);
		back.setOnClickListener(this);
		unbind.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int temoId = v.getId();
		if (temoId == Helper.getResId(activity, "back")) {
			goback();
		} else if (temoId == Helper.getResId(activity, "binded_unbind_bt")) {
			// 进入绑定界面，不显示手机号码输入框，执行取消绑定
			BindFrag bindFrag = new BindFrag();
			bindFrag.handler.sendEmptyMessage(2);
			startFragment(bindFrag);
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

	private void goback() {
		QStartFrag qStartFrag = new QStartFrag();
		startFragment(qStartFrag);
	}

}
