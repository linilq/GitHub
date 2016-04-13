package zty.sdk.fragment;

import java.util.ArrayList;

import zty.sdk.model.PayObject;
import zty.sdk.utils.Helper;
import zty.sdk.utils.MetricUtil;
import zty.sdk.utils.PayManager;
import zty.sdk.views.MyListView;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PayChoicesFrag extends BaseFragment {
	private LinearLayout paywayNoticeLL;
	private MyListView paywaylist;
	private ScrollView mScrollView;
	private View mainView;
	private Button payBt;
	/**
	 * 刚进入界面时初始化一个支付方式
	 */
	private String firstPayway;
	/**
	 * showAll==1时根据是否存在历史支付方式决定显示；showAll==2时直接显示所有支付方式
	 */
	private int showAll = 1;

	private ArrayList<PayObject> finalPays = null;
	private ArrayList<PayObject> finalmbPays = null;
	private ArrayList<PayObject> finalgcPays = null;

	private MyListViewAdapter adapter;
	private onPayitemClickedListener mPayitemClieckedListener;
	private onShowOtherPayListener mShowOtherPayListener;

	public interface onPayitemClickedListener {
		public void onPayitemClicked(boolean isMzCharge, String payway,
				Object otherObj);
	}

	public interface onShowOtherPayListener {
		public void onShowOtherPay();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		try {
			if (activity != null) {
				mPayitemClieckedListener = (onPayitemClickedListener) activity;
			}
		} catch (Exception e) {
			throw new ClassCastException(activity.toString()
					+ "must implements onPayitemClickedListener");
		}
		try {
			if (activity != null) {
				mShowOtherPayListener = (onShowOtherPayListener) activity;
			}
		} catch (Exception e) {
			throw new ClassCastException(activity.toString()
					+ "must implements onShowOtherPayListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(Helper.getLayoutId(activity, "f_paywaychoose"), container, false);
		android.view.ViewGroup.LayoutParams params = mainView.getLayoutParams();
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.v("LINILQTEST", "PayChoicesFrag is LANDSCAPE");
			params.height = LayoutParams.MATCH_PARENT;
			mainView.setLayoutParams(params);
			mainView.invalidate();
		}else{
			Log.v("LINILQTEST", "PayChoicesFrag is PORTRIT!!!");
			if (showAll==2) {
				//其他界面退回需要显示所有支付方式时
				params.height = MetricUtil.getDip(activity, 330);
			}else{
				//刚进入这个fragment界面时
				params.height = LayoutParams.WRAP_CONTENT;
			}
			mainView.setLayoutParams(params);
			mainView.invalidate();
		}
		
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
		showView();
	}

	private void showView() {
		adapter = new MyListViewAdapter();
		paywaylist.setAdapter(adapter);
		if (finalPays.size() == 1) {
			paywayNoticeLL.setVisibility(View.GONE);
			payBt.setVisibility(View.VISIBLE);
		} else {
			paywayNoticeLL.setVisibility(View.VISIBLE);
			payBt.setVisibility(View.GONE);
		}
		payBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 根据当前的方式（此时即上一次支付方式）执行支付
				doPayBaseonPayway(firstPayway);

			}
		});
		if (firstPayway!=null) {
			LayoutParams mScrollParams = mScrollView.getLayoutParams();
			mScrollParams.width = LayoutParams.MATCH_PARENT;
			mScrollParams.height = LayoutParams.WRAP_CONTENT;
			mScrollView.setLayoutParams(mScrollParams);
			mScrollView.invalidate();
		}
	}

	private void initData() {
		finalPays = new ArrayList<PayObject>();
		finalmbPays = new ArrayList<PayObject>();
		finalgcPays = new ArrayList<PayObject>();
		
		finalmbPays = PayManager.initMBPays(sdk);
		finalgcPays = PayManager.initGCPays(sdk);
		if (showAll == 1) {
			//如果有历史支付则加载，没有则加载支付宝
			finalPays = PayManager.initFirstFinalPays(sdk);
			firstPayway = finalPays.get(0).getPayWay();
		}else{
			//直接加载所有支付方式
			initAllFinalPays();
		}
	}

	private void initAllFinalPays() {
		finalPays = PayManager.initFinalPayways(sdk);
		
		if (finalmbPays!=null&&finalmbPays.size() > 0) {
			// 当三者中任一不被限制时，都要加入总支付方式中
			PayObject pobj = new PayObject();
			String temppayway = "yeemb";
			String temppayName = "手机充值卡";
			pobj.setPayName(temppayName);
			pobj.setPayWay(temppayway);
			finalPays.add(pobj);
		}

		if (finalgcPays!=null&&finalgcPays.size() > 0) {
			// 当三种支付中任一不被限制时，都要加入总支付方式中
			PayObject pobj = new PayObject();
			String temppayway = "yeegc";
			String temppayName = "游戏点卡";
			pobj.setPayName(temppayName);
			pobj.setPayWay(temppayway);
			finalPays.add(pobj);
		}
		
	}

	private void initView() {
		paywayNoticeLL = findViewById(Helper.getResId(activity, "pay_choose_notice_tv"));
		paywaylist = findViewById(Helper.getResId(activity, "pay_payway_lv"));
		payBt = findViewById(Helper.getResId(activity, "pay_payway_paybt"));
		mScrollView = findViewById(Helper.getResId(activity, "pay_payway_scrollv"));
		
	}

	protected void doPayBaseonPayway(String tpayway) {

		Object obj = null;
		if (tpayway.equals(PayManager.PAYWAY_YEE_GAMECARD)) {
			obj = finalgcPays;
		} else if (tpayway.equals(PayManager.PAYWAY_YEE_MOBILECARD)) {
			obj = finalmbPays;
		}
		mPayitemClieckedListener.onPayitemClicked(false, tpayway, obj);
	}

	@Override
	public boolean handleMessage(Message msg) {
		showAll = msg.what;
		return super.handleMessage(msg);
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
				holder.otherTv = (TextView) container
						.findViewById(Helper.getResId(activity, "pay_payway_item_other_tv"));
				container.setTag(holder);
			} else
				holder = (ViewHolder) container.getTag();
			String tpayWay = finalPays.get(position).getPayWay();
			String tpayName = finalPays.get(position).getPayName();
			holder.paywayIconIV.setImageResource(Helper.getResDraw(activity,
					"pay_" + tpayWay + "_icon"));
			holder.paywayNameTv.setText(tpayName);
			if (getCount() == 1) {
				holder.otherTv.setVisibility(View.VISIBLE);
				holder.otherTv.setBackgroundDrawable(activity.getResources()
						.getDrawable(
								Helper.getResDraw(activity,
										"pay_payway_item_switch_icon")));
				holder.otherTv.setText("");
				
				holder.otherTv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							mShowOtherPayListener.onShowOtherPay();
						} else {
							showOtherPay();
						}
					}

				});
			} else if (tpayWay.equals("mzpay")) {
				holder.otherTv.setVisibility(View.VISIBLE);
				holder.otherTv.setBackgroundDrawable(null);
				holder.otherTv.setText(sdk.mzbalance + "拇指币");
			} else if (tpayWay.equals("onlypay")) {
				holder.otherTv.setVisibility(View.VISIBLE);
				holder.otherTv.setBackgroundDrawable(null);
				holder.otherTv.setText(sdk.olbalance + "专属币");
			} else {
				holder.otherTv.setVisibility(View.GONE);
			}

			container.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					String pway = getItem(position).getPayWay();
					sdk.savePayway(pway);
					doPayBaseonPayway(pway);

				}
			});
			return container;
		}

		class ViewHolder {
			ImageView paywayIconIV;
			TextView paywayNameTv, otherTv;

		}

	}

	public void showOtherPay() {
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			android.view.ViewGroup.LayoutParams params = mainView.getLayoutParams();
			params.height = MetricUtil.dip2px(activity, 330);
			getView().setLayoutParams(params);
			getView().invalidate();
		}else{
			
		}
		//初始化所有的支付方式
		initAllFinalPays();
		adapter.notifyDataSetChanged();
		payBt.setVisibility(View.GONE);
		paywayNoticeLL.setVisibility(View.VISIBLE);
	}

}
