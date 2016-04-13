package zty.sdk.http;

import zty.sdk.listener.UserAccountListener;
import zty.sdk.model.PayPreMessage;
/**
 * 处理请求用户拇指币余额的请求结果
 * 
 * @author Administrator
 * 
 */
public class UserAccountReqHttpCb implements HttpCallback<PayPreMessage> {
	private UserAccountListener userAccountListener;

	public UserAccountReqHttpCb(UserAccountListener listener) {
		userAccountListener = listener;
	}

	@Override
	public void onSuccess(PayPreMessage object) {
		userAccountListener.onSimpleCallSuccess(object);

	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		userAccountListener.onFailure(errorCode, errorMessage);

	}

}
