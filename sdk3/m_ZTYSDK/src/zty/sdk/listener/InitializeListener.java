package zty.sdk.listener;

import zty.sdk.model.InitializeResult;

public interface InitializeListener {

    public void onInitSuccess(InitializeResult initializeResult);

    public void onFailure(int errorCode, String errorMessage);

}
