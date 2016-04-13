package zty.sdk.listener;

import zty.sdk.model.GetMSMResult;


public interface GetMSMListener {
	  public void onGetMSMSuccess(GetMSMResult getMSMResult);

	   public void onFailure(int errorCode, String errorMessage);
}
