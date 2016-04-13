package zty.sdk.http;

import zty.sdk.game.GameSDK;
import zty.sdk.listener.RegisterListener;
import zty.sdk.model.UserInfo;

public class RegisterHttpCb implements HttpCallback<UserInfo> {

	private RegisterListener registerListener;

	public RegisterHttpCb(RegisterListener listener) {
		this.registerListener = listener;
	}

	@Override
	public void onSuccess(UserInfo object) {
		if (object.getResult() == 1) {
			GameSDK.getOkInstance().userInfo = object;
			registerListener.onRegisterSuccess(object);
		} else {
			registerListener.onFailure(object.getResult(),
					object.getMessage());
		}
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		registerListener.onFailure(errorCode, errorMessage);
	}

}
