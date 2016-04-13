package zty.sdk.listener;

import zty.sdk.model.UserInfo;

public interface LoginListener {

    public void onLoginSuccess(UserInfo userInfo);

    public void onFailure(int errorCode, String errorMessage);


}
