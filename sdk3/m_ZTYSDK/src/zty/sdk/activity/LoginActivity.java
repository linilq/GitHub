package zty.sdk.activity;

import zty.sdk.fragment.BaseFragment;
import zty.sdk.fragment.NewerFragment;
import zty.sdk.fragment.QStartFrag;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.utils.Helper;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.Util_G;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
/**
 * 登陆activity；初始化界面以及账号数据，控制显示公告
 * @author Administrator
 *
 */

public class LoginActivity extends FragmentActivity {

	public BaseFragment mCurrentFragment = null;
	private FragmentManager fManager;
	private GameSDK sdk;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Log.v(Constants.TAG1, "LoginActivity");
		setContentView(Helper.getLayoutId(this, "login_regist"));
		
		initView();
		initData();
		showView();
	}

	private void showView() {
		boolean isNew = isNewer();
		if (isNew) {
			// 进入三选项界面；(不显示公告，不显示绑定提示由fragment去控制)
			NewerFragment newerFrag = new NewerFragment();
			// 不存在账号信息，进入注册界面不需要有返回按钮
			startFrag(newerFrag);
		} else {
			// 进入快速登陆界面;(显示公告，绑定提示由fragment去控制)
			QStartFrag qStartFrag = new QStartFrag();
			startFrag(qStartFrag);
		}

	}

	/**
	 * 判断是否存在账号信息
	 * 
	 * @return
	 */
	private boolean isNewer() {
		boolean isnewer = false;// 测试时为true//正式为false
		if (sdk.account==null) {
			isnewer = true;
		}else{
			String username = sdk.account.getUsn();
			if (StringUtil.isEmpty(username)) {
				isnewer = true;
			}
		}
		
		return isnewer;
	}

	public static interface LKeyListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mCurrentFragment instanceof LKeyListener) {
			return ((LKeyListener) mCurrentFragment).onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void startFrag(BaseFragment frag) {
		fManager.beginTransaction().replace(Helper.getResId(this, "container"), frag).commit();
		mCurrentFragment = frag;
	}

	private void initData() {
		fManager = getSupportFragmentManager();
		sdk = GameSDK.getOkInstance();
		if (sdk == null) {
			Log.i(Constants.TAG1, "GameSDK.getInstance() = "+sdk);
		}
//		Log.i(Constants.TAG1, "GameSDK.getInstance().debug = "+sdk.debug);
		Util_G.debug_i(Constants.TAG1, "初始化账号：");
		sdk.initallAccount();
		if (sdk == null) {
			return;
		}
	}

	private void initView() {

	}

	

}
