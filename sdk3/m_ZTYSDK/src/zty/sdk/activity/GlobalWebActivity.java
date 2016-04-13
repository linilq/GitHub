package zty.sdk.activity;

import zty.sdk.utils.Helper;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GlobalWebActivity extends Activity {
	private View rootView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = View.inflate(this, Helper.getLayoutId(this, "activity_web"), null);
		setContentView(rootView);
		Bundle bundle = getIntent().getExtras();
		String url = bundle.getString("url");
		showWebActivity(url);
	}
	public void showWebActivity(String url) {
		findViewById(Helper.getResId(this, "bt_web_return")).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		WebView mWebView = (WebView)findViewById(Helper.getResId(this, "events_show_wv"));
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptEnabled(true);//这两句必须设置，否则webview里面的点击都会没有效果
		webSettings.setLoadsImagesAutomatically(true);//
		mWebView.requestFocus();
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//设置为无缓存
		mWebView.setBackgroundColor(0);//设置背景颜色
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed(); // 接受所有网站的证书
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {//这个方法是在webview开始loading的时候会自动调用
				super.onPageStarted(view, url, favicon);
				findViewById(Helper.getResId(GlobalWebActivity.this, "events_download_pro")).setVisibility(View.VISIBLE);
			}
			@Override
			public void onPageFinished(WebView view, String url) {//这个方法是webview在loading结束的时候会自动调用可以在这里调用进度条关闭
				super.onPageFinished(view, url);
				findViewById(Helper.getResId(GlobalWebActivity.this, "events_download_pro")).setVisibility(View.INVISIBLE);
			}
		});
		mWebView.loadUrl(url);
	}
}
