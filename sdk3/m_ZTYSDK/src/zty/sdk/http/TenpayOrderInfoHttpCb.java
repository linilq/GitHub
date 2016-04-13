package zty.sdk.http;

import zty.sdk.http.HttpCallback;
import zty.sdk.model.TenpayOrderInfo;
import zty.sdk.utils.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class TenpayOrderInfoHttpCb implements HttpCallback<TenpayOrderInfo> {

	private Context mContext;
	
	public TenpayOrderInfoHttpCb(Context context){
		mContext = context;
	}
	
	@Override
	public void onSuccess(TenpayOrderInfo object) {
		String tn = object.getOrderInfo();
		if (StringUtil.isEmpty(tn)) {
			Toast.makeText(mContext, "支付失败，请稍后再试",
					Toast.LENGTH_SHORT).show();
		} else {
			goto_url(tn);
		}
		
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Toast.makeText(mContext, errorMessage,
				Toast.LENGTH_SHORT).show();
		
	}

	public void goto_url(String url) {
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.VIEW");
		localIntent.setData(Uri.parse(url));
		mContext.startActivity(localIntent);
	}
}
