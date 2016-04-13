package zty.sdk.http;

import zty.sdk.activity.PaymentActivity;
import zty.sdk.http.HttpCallback;
import zty.sdk.model.OnlyPayInfo;
import zty.sdk.utils.StringUtil;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class OnlyPayHttpCb implements HttpCallback<OnlyPayInfo> {

	private Handler mhandler;
	private Activity macticity;
	public OnlyPayHttpCb(Handler handler,Activity acticity){
		mhandler = handler;
		macticity = acticity;
	}
	@Override
	public void onSuccess(OnlyPayInfo object) {
		if (object.getResultCode()==200) {
			Message msg = Message.obtain();
			msg.what = PaymentActivity.NOTIFY_OL_PAY_SCC;
			msg.obj = object;
			mhandler.sendMessage(msg);
		}else{
			onFailure(0, "服务器忙，请稍后再试");
		}
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		if (StringUtil.isEmpty(errorMessage)) {
			errorMessage = "支付失败，请联系客服!";
		}
		Toast.makeText(macticity, errorMessage, Toast.LENGTH_LONG).show();
	}

}
