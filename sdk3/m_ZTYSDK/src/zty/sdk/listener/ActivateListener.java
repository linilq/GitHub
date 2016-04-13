package zty.sdk.listener;

import zty.sdk.model.ActivateResult;


public interface ActivateListener {

    public void onActivateSuccess(ActivateResult activateResult);

    public void onFailure(int errorCode, String errorMessage);

}
