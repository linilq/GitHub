package zty.sdk.http;

import zty.sdk.listener.FindPswListener;

public class FindPswHttpCb implements HttpCallback<String> {

	private FindPswListener findPswListener;

	public FindPswHttpCb(FindPswListener findPswListener) {
		this.findPswListener = findPswListener;
	}

	@Override
	public void onSuccess(String object) {
		if (object.equals("success")) {
			findPswListener
					.onFindPswSuccess("密码找回成功，您的密码将通过短信形式发送到该绑定号码,请注意查收。");
		} else {
			onFailure(0, "密码找回失败，您所输入的号码尚未绑定任何账号，请输入正确的号码。");
		}
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		findPswListener.onFindPswFailure(errorMessage);
	}

}