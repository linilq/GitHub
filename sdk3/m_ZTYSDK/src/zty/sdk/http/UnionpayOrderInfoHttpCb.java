package zty.sdk.http;

import zty.sdk.http.HttpCallback;
import zty.sdk.model.UnionpayOrderInfo;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.UnionpayUtils;
import android.app.Activity;
import android.widget.Toast;

public class UnionpayOrderInfoHttpCb implements HttpCallback<UnionpayOrderInfo> {

	private Activity activity;
	
	public UnionpayOrderInfoHttpCb(Activity mactivity){
		activity = mactivity;
	}
	@Override
	public void onSuccess(UnionpayOrderInfo object) {
		String tn = object.getOrderInfo();
		if (StringUtil.isEmpty(tn)) {
			Toast.makeText(activity, "支付失败，请稍后再试",
					Toast.LENGTH_SHORT).show();
		} else {
			UnionpayUtils.pay(activity, tn,
					UnionpayUtils.MODE_PRODUCT);// MODE_TEST,MODE_PRODUCT
		}
		
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Toast.makeText(activity, errorMessage,
				Toast.LENGTH_SHORT).show();
	}

}
