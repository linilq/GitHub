package zty.sdk.http;

import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.model.YeepayCardResult;
import zty.sdk.model.YeepayCardRsqResult;
import zty.sdk.paeser.YeepayCardRsqResultParser;
import zty.sdk.utils.DialogUtil;
import zty.sdk.utils.Helper;
import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

public class YeepayCardResultHttpCb implements HttpCallback<YeepayCardResult> {

	private Activity activity;
	private Handler handler;
	private YeepayCardResult myeepayCardResult;
	private int requestAmount;
	private int MAXTIMES = 10;
	private int trytimes = 0;
	
	public YeepayCardResultHttpCb(Activity mactivity,Handler mhandler,int requestAmount){
		activity = mactivity;
		handler = mhandler;
		this.requestAmount = requestAmount;
	}
	@Override
	public void onSuccess(YeepayCardResult object) {
		if (object.getCode() == 1) {
			
			myeepayCardResult = object;
			startYeeCardpayRsq(object);
		} else {
			Toast.makeText(activity.getApplicationContext(),
					"支付失败！" + object.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Toast.makeText(activity, errorMessage,
				Toast.LENGTH_SHORT).show();
	}

	public void startYeeCardpayRsq(YeepayCardResult object) {
		int time = 1000 * 2;
		if (trytimes!=0) {
			time = 1000 * 10;
		}
		Runnable runnable = new GetYeeCardPayRsqRunnable(object);

		handler.postDelayed(runnable, time);
		
		
	}
	
	private class GetYeeCardPayRsqRunnable implements Runnable {
		YeepayCardResult object;

		public GetYeeCardPayRsqRunnable(YeepayCardResult object) {
			this.object = object;
		}

		public void run() {
			getYeeCardpayRsq(object);
		}
	}
	
	public void getYeeCardpayRsq(YeepayCardResult result) {
		String api = Constants.SERVER_URL+"/yeepayrsq_sign";
		HttpRequest<YeepayCardRsqResult> request = new HttpRequest<YeepayCardRsqResult>(activity,
				new YeepayCardRsqResultParser(), new YeepayCardRsqResultListener(activity),false);
		try {

			JSONObject payRequest = new JSONObject();
			payRequest.put("pay_no", result.getPayNO());
			payRequest.put("ver", Constants.GAME_SDK_VERSION);
			request.execute(api, payRequest.toString());
		} catch (Exception ex) {
			GameSDK.getOkInstance().makeToast("支付信息提交错误！");
		}
	}
	
	public class YeepayCardRsqResultListener implements HttpCallback<YeepayCardRsqResult> {

		private Activity activity1;
		
		public YeepayCardRsqResultListener(Activity mactivity){
			activity1 = mactivity;
			
		}
		@Override
		public void onSuccess(YeepayCardRsqResult object) {
			if (object.getCode() == 1) {
				Toast.makeText(activity1.getApplicationContext(),
						"支付卡信息已提交到服务器！如果卡信息有效充值金额稍后到账。", Toast.LENGTH_LONG).show();// "支付卡信息已提交到服务器！如果卡信息有效充值金额稍后到账。"
				DialogUtil.showNormalDialog(activity1, activity1.getString(Helper.getResStr(activity1, "shop_succ_promit")),null);
//				GameSDK.getInstance().notifyPaymentFinish(requestAmount);
				activity1.finish();
			} else if (object.getCode() == 0) {
				if (trytimes > MAXTIMES) {
					Toast.makeText(
							activity1.getApplicationContext(),
							"支付超时！如果卡信息有效充值金额稍后到账。", Toast.LENGTH_LONG).show();
//					GameSDK.getInstance().notifyPaymentFinish(requestAmount);
					activity1.finish();
				} else {
					trytimes++;
					startYeeCardpayRsq(myeepayCardResult);
				}
			} else {
				Toast.makeText(activity1.getApplicationContext(),
						"支付失败！" + object.getMessage(), Toast.LENGTH_SHORT)
						.show();
			}
			
		}

		@Override
		public void onFailure(int errorCode, String errorMessage) {
			if (trytimes > MAXTIMES) {
				Toast.makeText(activity1, errorMessage,
						Toast.LENGTH_SHORT).show();
			} else {
				trytimes++;
				startYeeCardpayRsq(myeepayCardResult);
			}
		}

	}
}
