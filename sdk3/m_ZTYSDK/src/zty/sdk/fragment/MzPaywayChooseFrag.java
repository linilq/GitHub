package zty.sdk.fragment;

import java.util.ArrayList;

import zty.sdk.fragment.PayChoicesFrag.onPayitemClickedListener;
import zty.sdk.model.PayObject;
import zty.sdk.utils.Helper;
import zty.sdk.utils.MetricUtil;
import zty.sdk.utils.PayManager;
import zty.sdk.views.MyListView;
import android.app.Service;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MzPaywayChooseFrag extends BaseFragment {

	private MyListView list;
	private String[] allPayWays = { PayManager.PAYWAY_ALI,
			PayManager.PAYWAY_TEN, PayManager.PAYWAY_UNION,
			/*PayManager.PAYWAY_YEE_MOBILECARD, PayManager.PAYWAY_YEE_GAMECARD,*/
			PayManager.PAYWAY_WECHAT };
	private String[] allPayNames = { "支付宝", "财付通", "银行卡", /*"手机充值卡", "游戏点卡",*/
			"微信支付" };

	private String[] mobileCardWays = { PayManager.PAYWAY_CHINA_MOBILE,
			PayManager.PAYWAY_CHINA_UNICOM, PayManager.PAYWAY_CHINA_TELECOM };
	private String[] mobileCardNames = { "中国移动", "中国联通", "中国电信" };

	private String[] gameCardWays = { PayManager.PAYWAY_YEE_JCARD,
			PayManager.PAYWAY_YEE_ZYCARD, PayManager.PAYWAY_YEE_TSCARD };
	private String[] gameCardNames = { "骏网一卡通", "纵游一卡通", "32卡" };

	private ArrayList<PayObject> finalPays = null;
	private ArrayList<PayObject> finalmbPays = null;
	private ArrayList<PayObject> finalgcPays = null;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(Helper.getLayoutId(activity, "f_mz_paywaychoose"), container, false);
		android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			params.height=MetricUtil.getDip(activity, 280);
			view.setLayoutParams(params);
			view.invalidate();
		}
		
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		list = findViewById(Helper.getResId(activity, "mz_payway_lv"));
		initData();
		list.setAdapter(new MyListViewAdapter());
	}

	private void initData() {
		finalPays = new ArrayList<PayObject>();
		finalmbPays = new ArrayList<PayObject>();
		finalgcPays = new ArrayList<PayObject>();

		for (int i = 0; i < allPayNames.length; i++) {
			PayObject pobj = new PayObject();
			pobj.setPayName(allPayNames[i]);
			pobj.setPayWay(allPayWays[i]);
			if (i==(allPayNames.length-1)) {
				finalPays.add(1,pobj);
			}else
				finalPays.add(pobj);
			
		}

		/*for (int i = 0; i < mobileCardNames.length; i++) {
			PayObject pobj = new PayObject();
			pobj.setPayName(mobileCardNames[i]);
			pobj.setPayWay(mobileCardWays[i]);
			finalmbPays.add(pobj);
		}

		for (int i = 0; i < gameCardNames.length; i++) {
			PayObject pobj = new PayObject();
			pobj.setPayName(gameCardNames[i]);
			pobj.setPayWay(gameCardWays[i]);
			finalgcPays.add(pobj);
		}*/

	}

	protected void doPayBaseonPayway(String tpayway) {

		Object obj = null;
		if (tpayway.equals(PayManager.PAYWAY_YEE_GAMECARD)) {
			obj = finalgcPays;
		} else if (tpayway.equals(PayManager.PAYWAY_YEE_MOBILECARD)) {
			obj = finalmbPays;
		}
		onPayitemClickedListener mPayitemClieckedListener = (onPayitemClickedListener) activity;
		mPayitemClieckedListener.onPayitemClicked(true, tpayway, obj);
	}

	private class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return finalPays.size();
		}

		@Override
		public PayObject getItem(int position) {

			return finalPays.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View container, ViewGroup root) {
			ViewHolder holder = new ViewHolder();
			if (container == null) {
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
				container = inflater.inflate(Helper.getLayoutId(activity, "pay_payway_lv_item"), root,
						false);// View.inflate(activity,
								// Helper.getLayoutId(activity, "")pay_payway_lv_item, null);
				holder.paywayIconIV = (ImageView) container
						.findViewById(Helper.getResId(activity, "pay_payway_item_icon_iv"));
				holder.paywayNameTv = (TextView) container
						.findViewById(Helper.getResId(activity, "pay_payway_item_name_tv"));
				container.setTag(holder);
			} else
				holder = (ViewHolder) container.getTag();

			String tpayWay = finalPays.get(position).getPayWay();
			String tpayName = finalPays.get(position).getPayName();

			holder.paywayIconIV.setImageResource(Helper.getResDraw(activity,
					"pay_" + tpayWay + "_icon"));
			holder.paywayNameTv.setText(tpayName);

			container.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String pway = getItem(position).getPayWay();
					doPayBaseonPayway(pway);

				}
			});
			return container;
		}

		class ViewHolder {
			ImageView paywayIconIV;
			TextView paywayNameTv;

		}

	}
}
