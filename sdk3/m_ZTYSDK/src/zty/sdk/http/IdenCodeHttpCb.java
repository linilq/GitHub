package zty.sdk.http;

import zty.sdk.listener.IdentifyCodeListener;
import zty.sdk.model.IdentifyCode;
/**
 * 处理验证验证码的网络请求结果，网络请求成功不代表手机号码最终绑定成功
 * @author Administrator
 */
public class IdenCodeHttpCb implements HttpCallback<IdentifyCode> {
	private IdentifyCodeListener idCodeListener;

	public IdenCodeHttpCb(IdentifyCodeListener idCodeCallback) {
		this.idCodeListener = idCodeCallback;
	}

	@Override
	public void onSuccess(IdentifyCode object) {
		if (object.getResult().equals("000")) {
			idCodeListener.onIdentifyCodeSuccess(object);
		} else {
			idCodeListener.onIdentifyCodeFailure(object.getResult(),
					object.getMessage());
		}

	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		idCodeListener.onIdentifyCodeFailure(errorCode + "", errorMessage);
	}

}
