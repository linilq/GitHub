package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity;
import zty.sdk.activity.PaymentActivity;
import zty.sdk.game.GameSDK;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment implements Callback {

	public Handler handler = new Handler(this);
	public Activity activity;
	public GameSDK sdk;
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = getActivity();
		sdk = GameSDK.getOkInstance();
	}
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	
	public void startFragment(BaseFragment frag) {
		if (activity instanceof LoginActivity) {
			LoginActivity act = (LoginActivity) getActivity();
			act.startFrag(frag);
		}else if(getActivity() instanceof PaymentActivity){
			PaymentActivity act = (PaymentActivity) getActivity();
			act.startFragment(frag);
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int resId) {
		Activity act = getActivity();
		if (act == null) {
			return null;
		}
		return (T) act.findViewById(resId);
	}
}
