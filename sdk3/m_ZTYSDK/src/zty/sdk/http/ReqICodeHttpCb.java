package zty.sdk.http;

import zty.sdk.listener.RequestCodeListener;
import zty.sdk.model.IdentifyCode;
/**
 * 处理网络访问成功与失败的回调，注意：网络访问成功不代表验证码获取成功
 * @author Administrator
 */
public class ReqICodeHttpCb implements HttpCallback<IdentifyCode> {
	private RequestCodeListener reqICodeListener;

	public ReqICodeHttpCb(RequestCodeListener reqICodeListener) {
		this.reqICodeListener = reqICodeListener;
	}

	@Override
	public void onSuccess(IdentifyCode code) {
		if (code.getResult().equals("000")) {
			reqICodeListener.onRequestCodeSuccess(code);
		} else
			reqICodeListener.onRequestCodeFailure(code.getResult(),
					code.getMessage());
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		reqICodeListener.onRequestCodeFailure(errorCode + "", errorMessage);

	}

}