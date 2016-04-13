package zty.sdk.http;

import zty.sdk.activity.PaymentActivity;
import zty.sdk.http.HttpCallback;
import zty.sdk.model.MzPayInfo;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MzPayHttpCb implements HttpCallback<MzPayInfo> {

	private Handler mhandler;
	private Activity macticity;
	public MzPayHttpCb(Handler handler,Activity acticity){
		mhandler = handler;
		macticity = acticity;
	}
	@Override
	public void onSuccess(MzPayInfo object) {
		if (object.getRet()==200) {
			Message msg = Message.obtain();
			msg.what = PaymentActivity.NOTIFY_MZ_PAY_SCC;
			msg.obj = object;
			mhandler.sendMessage(msg);
		}else{
			onFailure(0, "服务器忙，请稍后再试");
		}
		
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Toast.makeText(macticity, errorMessage, Toast.LENGTH_LONG).show();
		
	}

}
