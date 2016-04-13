package zty.sdk.http;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.listener.ActivateListener;
import zty.sdk.model.ActivateResult;

public class ActivateHttpCb implements HttpCallback<ActivateResult> {

	private ActivateListener listener;

	public ActivateHttpCb(ActivateListener listener) {
		this.listener = listener;
	}

	@Override
	public void onSuccess(ActivateResult object) {
		listener.onActivateSuccess(object);
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		listener.onFailure(errorCode, errorMessage);
		if (errorCode != Constants.ERROR_CODE_NET) {
			GameSDK.getOkInstance().activate();// 重新联网
		}
	}

}