package zty.sdk.listener;

import zty.sdk.model.IdentifyCode;


public interface RequestCodeListener {
	void onRequestCodeSuccess(IdentifyCode code );
	void onRequestCodeFailure(String errorCode,String Message);

}
