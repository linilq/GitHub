package zty.sdk.http;

import zty.sdk.game.GameSDK;
import zty.sdk.listener.LoginListener;
import zty.sdk.model.UserInfo;

public class LoginHttpCb implements HttpCallback<UserInfo> {

	private LoginListener loginListener;

	public LoginHttpCb(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

	@Override
	public void onSuccess(UserInfo object) {
		if (object.getResult() == 1) {
			GameSDK.getOkInstance().userInfo = object;
			// 先保存账号才能跑下面的方法
			loginListener.onLoginSuccess(object);
			GameSDK.getOkInstance().afdf3();
		} else {
			loginListener
					.onFailure(object.getResult(), object.getMessage());
		}
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		loginListener.onFailure(errorCode, errorMessage);
	}

}
