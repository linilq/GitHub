package zty.sdk.listener;

import zty.sdk.model.IdentifyCode;



public interface IdentifyCodeListener {
	void onIdentifyCodeSuccess(IdentifyCode obj );
	void onIdentifyCodeFailure(String errorCode,String Message);
}
