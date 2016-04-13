package zty.sdk.listener;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.model.PayPreMessage;
import zty.sdk.utils.Util_G;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UserAccountListener implements SimpleListener {
	private Intent intent;
	private Activity activity;

	public UserAccountListener(Activity mactivity, Intent mintent) {
		intent = mintent;
		activity = mactivity;
	}

	@Override
	public void onSimpleCallSuccess(Object obj) {
		if (obj != null) {
			GameSDK sdk = GameSDK.getOkInstance();
			PayPreMessage info = (PayPreMessage) obj;
			sdk.mzbalance = info.getMz_balance();
			sdk.olbalance = info.getOnly_balance();
			String payways = info.getPayWaySign();
			sdk.paywaysign.clear();
			for (int i = 0; i < payways.length(); i++) {
				sdk.paywaysign.add(Integer.parseInt(payways.charAt(i) + ""));
			}
			
			sdk.mobilePayStr = info.getMobilePayStr();
			sdk.unicomPayStr = info.getUnicomPayStr();
			sdk.telecomPayStr = info.getTelecomPayStr();
			sdk.jcardPayStr = info.getJcardPayStr();
			sdk.zycardPayStr = info.getZycardPayStr();
			sdk.tscardPayStr = info.getTscardPayStr();
			
			activity.startActivity(intent);
			activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			Util_G.debug_e("", "访问网络成功");
		} else {
			onFailure(4001, "访问网络成功但是返回信息有异常");

		}
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Util_G.debug_i(Constants.TAG1, "访问网络成功但是返回信息有异常" + "出错！错误代码："
				+ errorCode + ",信息：" + errorMessage);
		Toast.makeText(activity, "出错！错误代码：" + errorCode + ",信息：" + errorMessage,
				0).show();

	}

}
