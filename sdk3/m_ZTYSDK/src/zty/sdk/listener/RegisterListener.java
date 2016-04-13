package zty.sdk.listener;

import zty.sdk.model.UserInfo;

public interface RegisterListener {

    public void onRegisterSuccess(UserInfo userInfo);

    public void onFailure(int errorCode, String errorMessage);

}
