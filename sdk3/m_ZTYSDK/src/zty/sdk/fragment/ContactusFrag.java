package zty.sdk.fragment;

import zty.sdk.activity.LoginActivity.LKeyListener;
import zty.sdk.utils.Helper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactusFrag extends BaseFragment implements OnClickListener,
		LKeyListener {

	private ImageView back,close;
	private TextView pnumTv,urlTv;
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(Helper.getLayoutId(activity, "f_contactus"), container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	private void initView() {
		back = findViewById(Helper.getResId(activity, "back"));
		close = findViewById(Helper.getResId(activity, "close"));
		pnumTv = findViewById(Helper.getResId(activity, "contactus_pnum_tv"));
		urlTv = findViewById(Helper.getResId(activity, "contactus_url_tv"));
		
		close.setVisibility(View.GONE);
		back.setOnClickListener(this);
		pnumTv.setOnClickListener(this);
		urlTv.setOnClickListener(this);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goBack();
			break;

		default:
			break;
		}
		return false;
	}

	private void goBack() {
		FindPsdFrag findPsdFrag = new FindPsdFrag();
		startFragment(findPsdFrag);
		
	}

	@Override
	public void onClick(View v) {
		int tempId = v.getId();
		if (tempId == Helper.getResId(activity, "back")) {
			goBack();
		}else if(tempId == Helper.getResId(activity, "contactus_pnum_tv")){
			String pnum = activity.getResources().getString(Helper.getResStr(activity, "mz_pnum_str"));
			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+pnum));
            startActivity(intent);//内部类

		}else if(tempId == Helper.getResId(activity, "contactus_url_tv")){
			String url = activity.getResources().getString(Helper.getResStr(activity, "mz_url_str"));
			Intent intent= new Intent();        
		    intent.setAction("android.intent.action.VIEW");    
		    Uri content_url = Uri.parse("http://"+url);   
		    intent.setData(content_url);  
		    startActivity(intent);
		}

	}
}
