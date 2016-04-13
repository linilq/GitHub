package zty.sdk.http;

import android.content.Context;
import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.listener.InitializeListener;
import zty.sdk.model.InitializeResult;
import zty.sdk.utils.LocalStorage;

public class InitializeHttpCb implements HttpCallback<InitializeResult>{
	private InitializeListener initializeListener;
	private Context context;
	
	public InitializeHttpCb(InitializeListener listener,Context context) {
		this.initializeListener = listener;
		this.context = context;
	}

	@Override
	public void onSuccess(InitializeResult object) {
		GameSDK.getOkInstance().deviceId = object.getDeviceId();
		LocalStorage storage = LocalStorage.getInstance(context);
		storage.putString(Constants.DEVICE_ID, object.getDeviceId());
		initializeListener.onInitSuccess(object);
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		initializeListener.onFailure(errorCode, errorMessage);
		if (errorCode != Constants.ERROR_CODE_NET) {
			GameSDK.getOkInstance().initialize();// 重新初始化
		}
	}
}
