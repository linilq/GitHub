package zty.sdk.http;

import java.net.URLEncoder;

import zty.sdk.alipay.AlixDefine;
import zty.sdk.game.Constants;
import zty.sdk.http.HttpCallback;
import zty.sdk.model.AlipayOrderInfo;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class AlipayOrderInfoHttpCb implements HttpCallback<AlipayOrderInfo> {
	private Handler mhandler;
	private Activity macticity;

	public AlipayOrderInfoHttpCb(Handler handler,Activity acticity){
		mhandler = handler;
		macticity = acticity;
	}
	
	@Override
	public void onSuccess(AlipayOrderInfo object) {
		final String info = object.getOrderInfo() + "&sign=" + "\""
				+ URLEncoder.encode(object.getSign()) + "\""
				+ "&sign_type=\"" + object.getSignType() + "\"";

		Log.i(Constants.TAG, info);
		
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(macticity);
				// 调用支付接口
				String result = alipay.pay(info);//支付返回结果 是阻塞等待的

				Message msg = new Message();
				msg.what = AlixDefine.RQF_PAY;
				msg.obj = result;
				mhandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
		
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Toast.makeText(macticity, errorMessage,
				Toast.LENGTH_SHORT).show();
		
	}

}
