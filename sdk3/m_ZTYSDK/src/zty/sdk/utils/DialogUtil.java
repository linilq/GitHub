package zty.sdk.utils;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class DialogUtil {

	public static PopupWindow progress_popupWindow;
	public static Dialog promotionDialog;
	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};
	/**
	 * 简单的进度框 由于上面的progress_popupWindow的层会被 Dialog盖住 所以在弹框上 弹progress必须
	 * 使用优先级更高的CustomProgressDialog
	 */
	public static CustomProgressDialog progressDialog;
	private static CustomNoticeDialog noticeDialog;
	private static CustomNormalDialog normalDialog;
	private static CustomExitDialog exitDialog;
	private static CustomExitDialog1 exitDialog1;

	/**
	 * 网络访问进度条显示
	 * 
	 * @param context
	 * @param content
	 *            进度提示内容，为null时，为普通进度条；不为null时，为登陆进度条，可以停止
	 * @param duration
	 */
	public static void showProgressDialog(Context context, String content,
			long duration, DialogCallBack callback) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		progressDialog = new CustomProgressDialog(context, content, callback);
		boolean cancelable = false;
		if (!StringUtil.isEmpty(content)) {
			cancelable = true;
		}
		progressDialog.setCancelable(cancelable);
		if (duration > 0) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					closeProgressDialog();
				}
			}, duration);
		}
		progressDialog.show();

	}

	/**
	 * 显示公告对话框
	 * 
	 * @param context
	 * @param content
	 * @param title
	 * @return
	 */
	public static Dialog showNoticeDialog(Context context, String content,
			String title, DialogCallBack callback) {
		if (noticeDialog != null && noticeDialog.isShowing()) {
			noticeDialog.dismiss();
			noticeDialog = null;
		}
		noticeDialog = new CustomNoticeDialog(context, content, title, callback);
		noticeDialog.setCancelable(false);
		noticeDialog.show();

		Window dialogWindow = noticeDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = MetricUtil.dip2px(context, 320);
		noticeDialog.getWindow().setAttributes(lp);

		return null;

	}

	/**
	 * 显示普通提示对话框
	 * 
	 * @param context
	 * @param content
	 * @param title
	 * @param callBacks
	 * @return
	 */
	public static void showNormalDialog(Context context, String content,
			DialogCallBack[] callBacks) {
		if (normalDialog != null && normalDialog.isShowing()) {
			normalDialog.dismiss();
			normalDialog = null;
		}
		normalDialog = new CustomNormalDialog(context, content, callBacks);
		normalDialog.setCancelable(false);
		normalDialog.show();

		Window dialogWindow = normalDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = MetricUtil.dip2px(context, 320);
		normalDialog.getWindow().setAttributes(lp);

	}

	public static void showExitDialog(Context context, String url,
			DialogCallBack callBack) {
		if (exitDialog != null && exitDialog.isShowing()) {
			exitDialog.dismiss();
			exitDialog = null;
		}
		exitDialog = new CustomExitDialog(context, url, callBack);
		exitDialog.setCancelable(false);
		exitDialog.show();

		Window dialogWindow = exitDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = MetricUtil.dip2px(context, 320);
		exitDialog.getWindow().setAttributes(lp);
	}

	public static void showExitDialog1(Context context, String url,
			DialogCallBack... callbacks) {
		if (exitDialog1 != null && exitDialog1.isShowing()) {
			exitDialog1.dismiss();
			exitDialog1 = null;
		}
		exitDialog1 = new CustomExitDialog1(context, url, callbacks);
		exitDialog1.setCancelable(false);
		exitDialog1.show();

		Window dialogWindow = exitDialog1.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = MetricUtil.dip2px(context, 320);
		exitDialog1.getWindow().setAttributes(lp);
	}

	public static void closeProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public static void closeNormalDialog() {
		if (normalDialog != null && normalDialog.isShowing()) {
			normalDialog.dismiss();
			normalDialog = null;
		}
	}

	public static class CustomExitDialog1 extends Dialog {
		public CustomExitDialog1(Context context, String url,
				DialogCallBack... cbacks) {
			this(context, Helper.getResStyle(context, "mzProgressDialog"),
					url, cbacks);
		}

		private DialogCallBack[] callbacks;

		public void setDialogCallBack(DialogCallBack... cbacks) {
			callbacks = cbacks;
		}

		public CustomExitDialog1(final Context context, int theme, String url,
				DialogCallBack... cbacks) {
			super(context, theme);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(
					Helper.getLayoutId(context, "dialog_notice4"), null);
			this.setContentView(view);

			ImageView closeIv = (ImageView) view.findViewById(Helper.getResId(
					context, "dialog_exit_close_iv"));
			ImageView activityIv = (ImageView) view.findViewById(Helper
					.getResId(context, "dialog_exit_activity_iv"));
			ImageView giftIv = (ImageView) view.findViewById(Helper.getResId(
					context, "dialog_exit_gift_iv"));
			ImageView quitIv = (ImageView) view.findViewById(Helper.getResId(
					context, "dialog_exit_mbt_iv"));
			setDialogCallBack(cbacks);

			activityIv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					/*
					 * if (GameSDK.getInstance().appInstalled) {
					 * 
					 * } else
					 */{
						String url = "http://www.91muzhi.com/muzhiplat/activity";
						goBroser(context, url);
					}
				}
			});
			giftIv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					/*
					 * if (GameSDK.getOkInstance().isAppInstalled()) {
					 * ComponentName componentName = new ComponentName(
					 * "com.mzyw.center", "com.mzyw.center.GameInfoActivity");
					 * Intent intent = new Intent();
					 * intent.setComponent(componentName);
					 * intent.setAction("zty.sdk"); intent.putExtra("contUrl",
					 * "http://www.91muzhi.com/muzhiplat/api/app/detail/0?mzappId="
					 * + GameSDK.getOkInstance().gameId);
					 * intent.putExtra("aimgift", true);
					 * context.startActivity(intent); } else
					 */{
						String url = "http://www.91muzhi.com/muzhiplat/mobile/sole/0?mzappId="
								+ GameSDK.getOkInstance().gameId;
						goBroser(context, url);
					}

				}
			});

			quitIv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 2015-05-09
					GameSDK.getOkInstance().setInGameSign(false);
					LocalStorage storage = LocalStorage.getInstance(context);

					Util_G.debug_i(Constants.TAG, "afdf2");
					// 游戏退出时也保存一个在线时长，下次登录就上传，这个是最准确的
					int afd = 0;
					afd = (int) (System.currentTimeMillis() / 1000);
					storage.putString("adff2",
							String.valueOf(afd - GameSDK.getOkInstance().afdft));
					dismiss();
					if (callbacks[0] != null) {
						callbacks[0].callBack();
					}

				}
			});
			View.OnClickListener neg = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					GameSDK.getOkInstance().setInGameSign(true);
					if (callbacks[1] != null) {
						callbacks[1].callBack();
					}
					dismiss();
				}
			};
			closeIv.setOnClickListener(neg);
		}

		protected void goBroser(Context context, String urlStr) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri url = Uri.parse(urlStr);
			intent.setData(url);
			context.startActivity(intent);

		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			GameSDK.getOkInstance().setInGameSign(true);
			if (callbacks[1] != null) {
				callbacks[1].callBack();
			}
			dismiss();
		}
	}

	/**
	 * 游戏退出对话框
	 * 
	 * @author Administrator
	 * 
	 */
	public static class CustomExitDialog extends Dialog {
		public CustomExitDialog(Context context, String url,
				DialogCallBack cback) {

			this(context, Helper.getResStyle(context, "mzProgressDialog"),
					url, cback);
		}

		private DialogCallBack positiveCallback;

		public void setDialogCallBack(DialogCallBack cback) {
			positiveCallback = cback;
		}

		public CustomExitDialog(Context context, int theme, String url,
				DialogCallBack cback) {
			super(context, theme);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(
					Helper.getLayoutId(context, "dialog_notice3"), null);
			this.setContentView(view);

			TextView title = (TextView) findViewById(Helper.getResId(context,
					"logo_title"));
			TextView content = (TextView) findViewById(Helper.getResId(context,
					"dialog_exit_content_tv"));
			Button confirm = (Button) findViewById(Helper.getResId(context,
					"dialog_exit_confirm_bt"));
			Button quit = (Button) findViewById(Helper.getResId(context,
					"dialog_exit_quit_bt"));
			WebView webView = (WebView) findViewById(Helper.getResId(context,
					"dialog_exit_web"));
			setDialogCallBack(cback);

			title.setBackgroundColor(Color.TRANSPARENT);
			title.setText("退出游戏");
			content.setText("游戏即将退出...");

			if (StringUtil.isEmpty(url)) {
				webView.setVisibility(View.GONE);
				content.setVisibility(View.VISIBLE);
			} else {
				webView.getSettings().setJavaScriptEnabled(true);
				webView.getSettings().setLoadsImagesAutomatically(true);
				webView.getSettings().setLoadWithOverviewMode(true);
				Util_G.debug_i("LINILQTEST", "退出公告加载地址：" + url);
				webView.loadUrl(url);
				webView.setVisibility(View.VISIBLE);
				content.setVisibility(View.GONE);
			}

			confirm.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					if (positiveCallback != null) {
						positiveCallback.callBack();
					}

				}
			});
			quit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			dismiss();
		}
	}

	/**
	 * 普通提示对话框
	 * 
	 * @author Administrator
	 */
	public static class CustomNormalDialog extends Dialog {
		public CustomNormalDialog(Context context, String strMessage,
				DialogCallBack[] cbacks) {
			this(context, Helper.getResStyle(context, "mzProgressDialog"),
					strMessage, cbacks);
		}

		private DialogCallBack positiveCallback, negtiveCallback;

		public void setDialogCallBack(DialogCallBack[] cbacks) {
			if (cbacks.length == 2) {
				positiveCallback = cbacks[0];
				negtiveCallback = cbacks[1];
			} else {
				positiveCallback = cbacks[0];
			}

		}

		public CustomNormalDialog(Context context, int theme,
				String strMessage, DialogCallBack[] cbacks) {
			super(context, theme);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(
					Helper.getLayoutId(context, "dialog_notice2"), null);
			this.setContentView(view);
			TextView content = (TextView) findViewById(Helper.getResId(context,
					"dialog_notice_content_tv"));
			Button confirm = (Button) findViewById(Helper.getResId(context,
					"dialog_notice_confirm_bt"));
			Button quit = (Button) findViewById(Helper.getResId(context,
					"dialog_notice_quit_bt"));
			setDialogCallBack(cbacks);
			content.setText(strMessage);
			confirm.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					if (positiveCallback != null) {
						positiveCallback.callBack();
					}

				}
			});
			if (cbacks.length == 2) {
				quit.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
						if (negtiveCallback != null) {
							negtiveCallback.callBack();
						}

					}
				});
			} else {
				quit.setVisibility(View.GONE);
			}
		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			dismiss();
		}

	}

	/**
	 * 公告界面对话框
	 * 
	 * @author Administrator
	 */
	public static class CustomNoticeDialog extends Dialog {
		public CustomNoticeDialog(Context context, String strMessage,
				String title, DialogCallBack callback) {
			this(context, Helper.getResStyle(context, "mzProgressDialog"),
					strMessage, title, callback);
		}

		private DialogCallBack mcallback;

		public CustomNoticeDialog(final Context context, int theme, String url,
				String title, DialogCallBack callback) {
			super(context, theme);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(
					Helper.getLayoutId(context, "dialog_notice1"), null);
			this.setContentView(view);

			mcallback = callback;
			TextView titleTv = (TextView) view.findViewById(Helper.getResId(
					context, "logo_title"));
			TextView dateaddTv = (TextView) view.findViewById(Helper.getResId(
					context, "logo_add_date"));
			ImageView back = (ImageView) view.findViewById(Helper.getResId(
					context, "back"));
			ImageView close = (ImageView) view.findViewById(Helper.getResId(
					context, "close"));
			WebView webView = (WebView) view.findViewById(Helper.getResId(
					context, "notice_web"));

			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setLoadsImagesAutomatically(true);
			webView.getSettings().setLoadWithOverviewMode(true);
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					Uri uri = Uri.parse(url); // url为你要链接的地址
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					((Activity) context).startActivity(intent);
					return true;
				}
			});
			Util_G.debug_i("LINILQTEST", "加载地址：" + url);
			webView.loadUrl(url);

			back.setVisibility(View.GONE);
			close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					mcallback.callBack();
				}
			});
			// titleTv.setBackgroundColor(Color.WHITE);
			// titleTv.setText(title);
		}

		@Override
		public void onBackPressed() {
			dismiss();
			mcallback.callBack();
		}
	}

	/**
	 * 进度对话框，实际上只是一个进度条
	 * 
	 * @author Administrator
	 */
	public static class CustomProgressDialog extends Dialog {
		/**
		 * 对外构造函数，strMessage==null时，只显示进度条
		 * 
		 * @param context
		 * @param strMessage
		 * @param callback
		 */
		public CustomProgressDialog(Context context, String strMessage,
				DialogCallBack callback) {
			this(context, Helper.getResStyle(context, "mzProgressDialog"),
					strMessage, callback);
		}

		private DialogCallBack mcallback;

		public CustomProgressDialog(Context context, int theme,
				String strMessage, DialogCallBack callback) {
			super(context, theme);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(
					Helper.getLayoutId(context, "dialog_custompbar"), null);

			this.setContentView(view);

			TextView tvMsg = (TextView) view.findViewById(Helper.getResId(
					context, "id_tv_loadingmsg"));
			ImageView divider = (ImageView) view.findViewById(Helper.getResId(
					context, "dialog_progress_divider"));
			ImageView stop = (ImageView) view.findViewById(Helper.getResId(
					context, "dialog_progress_stop"));

			if (!StringUtil.isEmpty(strMessage)) {
				tvMsg.setBackgroundDrawable(context.getResources().getDrawable(
						Helper.getResDraw(context, "dialog_progress_login_bg")));
				tvMsg.setVisibility(View.VISIBLE);
				divider.setVisibility(View.VISIBLE);
				stop.setVisibility(View.VISIBLE);
			}
			if (callback != null) {
				mcallback = callback;
				stop.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();

					}
				});
				setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						mcallback.callBack();

					}
				});
			}

		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			dismiss();
		}

		@Override
		public void setOnCancelListener(OnCancelListener listener) {
			dismiss();
		}

	}

	public static interface DialogCallBack {
		public void callBack();
	};

}
