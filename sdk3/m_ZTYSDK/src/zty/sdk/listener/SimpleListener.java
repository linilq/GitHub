package zty.sdk.listener;


public interface SimpleListener {
	 public void onSimpleCallSuccess(Object obj);

	 public void onFailure(int errorCode, String errorMessage);
}
