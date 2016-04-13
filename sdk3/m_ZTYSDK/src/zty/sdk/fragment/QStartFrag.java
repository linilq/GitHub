package zty.sdk.fragment;

import java.util.ArrayList;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.game.Constants;
import zty.sdk.listener.LoginListener;
import zty.sdk.model.NativeAccountInfor;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.DialogUtil;
import zty.sdk.utils.DialogUtil.DialogCallBack;
import zty.sdk.utils.Helper;
import zty.sdk.utils.MetricUtil;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.Util_G;
import zty.sdk.views.MyListView;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class QStartFrag extends BaseFragment implements OnClickListener, LKeyListener {

	private ImageView back, close, more, bind, changPsd, switchUsn;
	private Button qstart;
	private TextView name_et;
	private LinearLayout usnEtLayout;
	private String bindStatus, notifiedStatus;
	private PopupWindow pop;
	private int ORDER_AUTOLOGIN = 100;
	/**
	 * 当有绑定提示时不执行自动登录，置为false
	 */
	private boolean autoLogin = true;
	private String deleteAccount ="";

	@Override
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			 Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_quickstart"), container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		showView();
	}

	private void showView() {
		bindStatus = sdk.account.getBstatus();
		notifiedStatus = sdk.account.getNstatus();
		name_et.setText(sdk.account.getUsn());
		if (bindStatus==null) {
			bindStatus = "false";
		}
		if (bindStatus.equals("false") && ((StringUtil.isEmpty(notifiedStatus) || !notifiedStatus.equals("noticed")))) {
			DialogCallBack[] callbacks = getCallBacks();
			autoLogin = false;
			DialogUtil.showNormalDialog(activity, getResources().getString(Helper.getResStr(activity, "bind_dialog_notice_str")), callbacks);
		} else if (bindStatus.equals("true")) {
			bind.setImageResource(Helper.getResDraw(activity, "qstart_binded_icon"));
		}
		// 显示公告
		if (sdk.isFirstIn) {
			showNotice(sdk.noturl);
			sdk.isFirstIn = false;
		}
		
	}

	/**
	 * 如果有公告则显示公告，公告关闭后自动登录； 如果没有公告，直接自动登录
	 * 
	 * @param url
	 */
	public void showNotice(String url) {
		if (!StringUtil.isEmpty(sdk.noturl)) {
			DialogUtil.showNoticeDialog(activity, url, "拇指通行证", new DialogCallBack() {
				@Override
				public void callBack() {
					beginLogin();
				}
			});
		} else {
			beginLogin();
		}
	}

	public void beginLogin() {
		if (autoLogin) {
			Util_G.debug_i(Constants.TAG1, "要执行自动登录咯，对话框！！");
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
						autoLogin = false;
					}

				}
			});
			handler.sendEmptyMessageDelayed(ORDER_AUTOLOGIN, 2000);
		}

	}

	/**
	 * 生成绑定的两个回调
	 * 
	 * @return
	 */
	private DialogCallBack[] getCallBacks() {
		DialogCallBack[] callbacks = { null, null };
		DialogCallBack positiveCb = new DialogCallBack() {

			@Override
			public void callBack() {
				// 进入绑定账号
				sdk.onNstatusChange("noticed");
				BindFrag bindFrag = new BindFrag();
				bindFrag.handler.sendEmptyMessage(1);
				startFragment(bindFrag);

			}
		};
		DialogCallBack negtiveCb = new DialogCallBack() {

			@Override
			public void callBack() {
				sdk.onNstatusChange("noticed");

			}
		};
		callbacks[0] = positiveCb;
		callbacks[1] = negtiveCb;
		return callbacks;
	}


	private void initView() {
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		more = findViewById(Helper.getResId(activity, "qstart_more_account"));
		bind = findViewById(Helper.getResId(activity, "qstart_bind_iv"));
		changPsd = findViewById(Helper.getResId(activity, "qstart_cpsd_iv"));
		switchUsn = findViewById(Helper.getResId(activity, "qstart_susn_iv"));
		qstart = findViewById(Helper.getResId(activity, "qstart_login_bt"));
		name_et = findViewById(Helper.getResId(activity, "qstart_name_tv"));
		usnEtLayout = findViewById(Helper.getResId(activity, "login_name_layout"));

		back.setVisibility(View.GONE);
		close.setOnClickListener(this);
		usnEtLayout.setOnClickListener(this);
		bind.setOnClickListener(this);
		changPsd.setOnClickListener(this);
		switchUsn.setOnClickListener(this);
		qstart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == Helper.getResId(activity, "close")) {
			// 退出登录
			activity.finish();
		} else if (id == Helper.getResId(activity, "login_name_layout")) {
			// 展示所有登陆过的账号
			toogleMoreBt();

		} else if (id == Helper.getResId(activity, "qstart_bind_iv")) {
			bindStatus = sdk.account.getBstatus();
			if (StringUtil.isEmpty(bindStatus) || bindStatus.equals("false")) {
				// 进入绑定账号
				BindFrag bindFrag = new BindFrag();
				bindFrag.handler.sendEmptyMessage(1);
				startFragment(bindFrag);
			} else if (bindStatus.equals("true")) {
				// 已经绑定过界面
				BindedFrag bindedFrag = new BindedFrag();
				startFragment(bindedFrag);
			}

		} else if (id == Helper.getResId(activity, "qstart_cpsd_iv")) {
			// 进入密码修改
			startFragment(new ChangePsdFrag());

		} else if (id == Helper.getResId(activity, "qstart_susn_iv")) {
			// 进入登陆界面，用户切换账号
			LoginFrag loginFrag = new LoginFrag();
			loginFrag.handler.sendEmptyMessage(2);
			startFragment(loginFrag);
		} else if (id == Helper.getResId(activity, "qstart_login_bt")) {
			// 登陆进入游戏,手动登入也要先声明autoLogin
			autoLogin = true;
			beginLogin();
		}

	}

	private void toogleMoreBt() {
		RotateAnimation anim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setStartOffset(50);
		anim.setDuration(100);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (pop != null && pop.isShowing()) {
					pop.dismiss();
					more.setImageDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "qstart_more_btn1")));
				} else {
					showMoreAccount();
					more.setImageDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "qstart_more_btn")));
				}
				more.clearAnimation();
			}
		});
		more.startAnimation(anim);
	}

	/**
	 * 登陆
	 * 
	 * @param showpro
	 *            是否显示默认进度条
	 */
	public void doLogin(boolean showpro) {
		
		sdk.login(sdk.account.getUsn(), sdk.account.getPsd(), new LoginListener() {

			@Override
			public void onLoginSuccess(UserInfo userInfo) {
				DialogUtil.closeProgressDialog();
				sdk.onBstatusChange(userInfo.getB_status(), userInfo.getPnum());
				sdk.notifyLoginSuccess(userInfo.getLoginAccount(), userInfo.getUserId(), userInfo.getSign());
				sdk.setInGameSign(true);
				sdk.setLoginedSign(true);
				sdk.afdf();
				activity.finish();

			}

			@Override
			public void onFailure(int errorCode, String errorMessage) {
				DialogUtil.closeProgressDialog();
				sdk.makeToast(errorMessage);
			}
		}, activity, showpro);
	}

	
	private void showMoreAccount() {
		if (pop == null) {
			MyListView contentView = (MyListView) View.inflate(activity, Helper.getLayoutId(activity, "more_content"), null);
			contentView.setAdapter(new MyArrayAdapter());
			pop = new PopupWindow(contentView, MetricUtil.getDip(activity, 300f), LayoutParams.WRAP_CONTENT, true);

		}
		pop.setBackgroundDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "more_account_bg")));
		pop.setOutsideTouchable(true);
		
		pop.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				more.setImageDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "qstart_more_btn1")));
				
			}
		});
		pop.showAsDropDown(usnEtLayout, 0, MetricUtil.getDip(activity, -12.5f));
		pop.update();
		more.setImageDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "qstart_more_btn")));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			activity.finish();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == ORDER_AUTOLOGIN && autoLogin) {
			doLogin(false);
		}
		return super.handleMessage(msg);
	}

	public void setBindIcon(){
		if (sdk.account.getBstatus().equals("true")) {
			bind.setImageDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "qstart_binded_icon")));
		}else{
			bind.setImageDrawable(activity.getResources().getDrawable(Helper.getResDraw(activity, "qstart_bind_icon")));
		}
	}
	
	private class MyArrayAdapter extends BaseAdapter {

		private ArrayList<NativeAccountInfor> maccountList = new ArrayList<NativeAccountInfor>();

		public MyArrayAdapter() {
			updateData();

		}

		/**
		 * 1、该界面中所有导致账号状态变化的操作都要调用， 2、必须在sdk内存账号修改完毕后调用
		 */
		public void updateData() {
			maccountList.clear();
			maccountList = sdk.accountList;
			Util_G.debug_i(Constants.TAG1, "maccountList：" + maccountList.toString());

		}

		@Override
		public int getCount() {
			return maccountList.size();
		}

		@Override
		public Object getItem(int position) {
			return maccountList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(Helper.getLayoutId(activity, "more_content_item"), parent, false);
			}
			TextView usnTV = (TextView) convertView.findViewById(Helper.getResId(activity, "content_tv"));
			ImageView deleteIV = (ImageView) convertView.findViewById(Helper.getResId(activity, "delete_content"));
			String tempUsn = maccountList.get(position).getUsn();
			usnTV.setText(tempUsn);

			usnTV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String usn = maccountList.get(position).getUsn();
					sdk.onAccountChoose(usn);
					updateData();
					name_et.setText(usn);
					toogleMoreBt();
					setBindIcon();
				}
			});
			deleteIV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteAccount = maccountList.get(position).getUsn();
					DialogUtil.DialogCallBack[] callBacks = getDeleteCallBacks();
					
					DialogUtil.showNormalDialog(activity, "您确定要删除账号："+deleteAccount+"吗?", callBacks);
					
				}
			});
			return convertView;
		}

		protected DialogCallBack[] getDeleteCallBacks() {
			DialogUtil.DialogCallBack callBacks[] = {null,null};
			
			callBacks[0] = new DialogUtil.DialogCallBack(){

				@Override
				public void callBack() {
					sdk.deleteAccount(deleteAccount);
					updateData();
					pop.dismiss();
					deleteAccount = null;
					if (sdk.account!=null) {
						name_et.setText(sdk.account.getUsn());
						setBindIcon();
					}else{
						//账号删除完后自动跳转
						LoginFrag frag = new LoginFrag();
						frag.handler.sendEmptyMessage(1);
						startFragment(frag);
					}
					
				}
				
			};
			
			callBacks[1] = new DialogUtil.DialogCallBack(){

				@Override
				public void callBack() {
				
				}
				
			};
			
			return callBacks;
		}

	}
}
