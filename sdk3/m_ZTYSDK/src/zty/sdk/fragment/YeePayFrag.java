package zty.sdk.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.fragment.PayChoicesFrag.onPayitemClickedListener;
import zty.sdk.game.GameSDK;
import zty.sdk.model.PayObject;
import zty.sdk.utils.Helper;
import zty.sdk.utils.MetricUtil;
import zty.sdk.utils.StringUtil;
import zty.sdk.views.MyGridView;
import android.app.Service;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class YeePayFrag extends BaseFragment implements OnClickListener {

	/**
	 * 1表示手机卡支付；2表示游戏点卡
	 */
	private int payType;
	private ImageView paywayIV, paywayitemIV, morePaywayIV;
	private TextView paywayTV, paywayitemTV;
	private EditText cardnoET, cardpsdET;
	private MyGridView gridView;
	private String[] mbPayNums = { "10", "20", "30", "50", "100" };
	private String[] gcPayNums = { "10", "20", "30", "50", "100" };
	public PopupWindow pop;
	private ArrayList<PayObject> finalYeePays = null;
	private MyGridViewAdapter adapter;
	private Button submit;
	/**
	 * 存储选中的计费金额
	 */
	private String selectedAmount = "";
	private int selectedAmountId = 1;
	private String selectedPayWay = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int layout_id = Helper.getLayoutId(activity, "f_yeepay_por");
		if (isLandScape()) {
			layout_id = Helper.getLayoutId(activity, "f_yeepay_land");
		}
		View view = inflater.inflate(layout_id, container, false);
		android.view.ViewGroup.LayoutParams params = view.getLayoutParams();

		if (sdk.isMzCharge) {
			params.height = MetricUtil.dip2px(activity, 300);
		} else {
			params.height = MetricUtil.dip2px(activity, 330);
		}

		view.setLayoutParams(params);
		view.invalidate();
		return view;
	}

	public boolean isLandScape() {
		return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
		showView();
	}

	private void showView() {
		int imagResId = Helper.getResDraw(activity, "pay_yeegc_icon");
		String payNamestr = "游戏点卡";
		if (payType == 1) {
			imagResId = Helper.getResDraw(activity, "pay_yeemb_icon");
			payNamestr = "手机充值卡";
			adapter = new MyGridViewAdapter(mbPayNums);
		} else if (payType == 2) {
			adapter = new MyGridViewAdapter(gcPayNums);
		}

		gridView.setAdapter(adapter);
		paywayIV.setImageResource(imagResId);
		paywayTV.setText(payNamestr);
		paywayitemIV.setImageResource(Helper.getResDraw(activity, "yeepay_"
				+ selectedPayWay + "_icon"));
		paywayitemTV.setText(finalYeePays.get(0).getPayName());

	}

	private void initData() {
		if (!StringUtil.isEmpty(sdk.mobilePayStr)) {
			mbPayNums = sdk.mobilePayStr.split(",");
		}
		if (!StringUtil.isEmpty(sdk.jcardPayStr)) {
			gcPayNums = sdk.jcardPayStr.split(",");
		}
		selectedPayWay = finalYeePays.get(0).getPayWay();
		if (payType == 1) {
			selectedAmount = mbPayNums[1];
		} else {
			selectedAmount = gcPayNums[1];
		}
	}

	private void initView() {
		paywayIV = findViewById(Helper.getResId(activity, "yeepay_pay_icon_iv"));
		paywayTV = findViewById(Helper.getResId(activity, "yeepay_pay_name_tv"));

		paywayitemIV = findViewById(Helper.getResId(activity, "yeepay_itemicon_iv"));
		paywayitemTV = findViewById(Helper.getResId(activity, "yeepay_itemname_et"));

		morePaywayIV = findViewById(Helper.getResId(activity, "yeepay_more_payway_iv"));

		cardnoET = findViewById(Helper.getResId(activity, "yeepay_cardno_et"));
		cardpsdET = findViewById(Helper.getResId(activity, "yeepay_cardpsd_et"));
		gridView = findViewById(Helper.getResId(activity, "yeepay_price_grid"));
		submit = findViewById(Helper.getResId(activity, "yeepay_submit_bt"));

		morePaywayIV.setOnClickListener(this);
		paywayitemTV.setOnClickListener(this);
		submit.setOnClickListener(this);

	}

	@Override
	public boolean handleMessage(Message msg) {
		payType = msg.what;
		if (msg.obj instanceof ArrayList<?>) {
			finalYeePays = (ArrayList<PayObject>) msg.obj;
		}
		return super.handleMessage(msg);
	}

	@Override
	public void onClick(View v) {
		int tempId = v.getId();
		if (tempId == Helper.getResId(activity, "yeepay_more_payway_iv")
				|| tempId == Helper.getResId(activity, "yeepay_itemname_et")) {
			toogleMorBt();
		} else if (tempId == Helper.getResId(activity, "yeepay_submit_bt")) {
			// 支付
			String cardNo = cardnoET.getText().toString();
			String cardPsd = cardpsdET.getText().toString();

			if (StringUtil.isEmpty(cardNo)) {
				GameSDK.getOkInstance().makeToast("卡号不能为空");
				return;
			}
			if (StringUtil.isEmpty(cardPsd)) {
				GameSDK.getOkInstance().makeToast("密码不能为空");
				return;
			}

			onPayitemClickedListener listener = (onPayitemClickedListener) activity;

			JSONObject payRequest = new JSONObject();
			try {
				payRequest.put("card_no", cardNo);
				payRequest.put("payway", selectedPayWay);
				payRequest.put("card_pass", cardPsd);
				payRequest.put("total_fee", selectedAmount);
				payRequest.put("order_amount",selectedAmount);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			listener.onPayitemClicked(sdk.isMzCharge, selectedPayWay,
					payRequest);
		}

	}

	private RotateAnimation getMorebtAnim() {
		RotateAnimation anim = new RotateAnimation(0, 180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setStartOffset(50);
		anim.setDuration(100);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (pop != null && pop.isShowing()) {
					pop.dismiss();
					morePaywayIV.setImageDrawable(activity.getResources()
							.getDrawable(Helper.getResDraw(activity, "qstart_more_btn1")));
				} else {
					showMore();
					morePaywayIV.setImageDrawable(activity.getResources()
							.getDrawable(Helper.getResDraw(activity, "qstart_more_btn")));
				}
				morePaywayIV.clearAnimation();
			}
		});
		return anim;
	}

	private void showMore() {
		LinearLayout paynameLayout = findViewById(Helper.getResId(activity, "yeepay_name_layout"));
		int widthINpix = paynameLayout.getWidth();
		if (pop == null) {
			ListView contentView = (ListView) View.inflate(activity,
					Helper.getLayoutId(activity, "more_content"), null);

			contentView.setAdapter(new MyListAdapter());

			pop = new PopupWindow(contentView, widthINpix,
					LayoutParams.WRAP_CONTENT, true);

		}

		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(activity.getResources().getDrawable(
				Helper.getResDraw(activity, "edit_bg")));
		pop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				morePaywayIV.setImageDrawable(activity.getResources()
						.getDrawable(Helper.getResDraw(activity, "qstart_more_btn1")));

			}
		});
		pop.showAsDropDown(paynameLayout);

	}

	public void toogleMorBt() {
		RotateAnimation anim = getMorebtAnim();
		morePaywayIV.startAnimation(anim);
	}

	public class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return finalYeePays.size();
		}

		@Override
		public PayObject getItem(int position) {
			return finalYeePays.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(Helper.getLayoutId(activity, "more_content_item"),
						parent, false);
			}
			ImageView paywayIV = (ImageView) convertView
					.findViewById(Helper.getResId(activity, "content_iv"));
			TextView paynameTV = (TextView) convertView
					.findViewById(Helper.getResId(activity, "content_tv"));
			ImageView deleteIV = (ImageView) convertView
					.findViewById(Helper.getResId(activity, "delete_content"));

			final int resId = Helper.getResDraw(activity,
					"yeepay_" + getItem(position).getPayWay() + "_icon");
			final String payname = getItem(position).getPayName();
			paywayIV.setImageResource(resId);
			paynameTV.setText(payname);
			deleteIV.setVisibility(View.GONE);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					toogleMorBt();
					paywayitemIV.setImageResource(resId);
					paywayitemTV.setText(payname);
					selectedPayWay = getItem(position).getPayWay();
				}

			});
			return convertView;
		}

	}

	public class MyGridViewAdapter extends BaseAdapter {

		private String[] chargeNum;

		public MyGridViewAdapter(String[] mchargeNum) {
			chargeNum = mchargeNum;
		}

		public void setDataSet(String[] mchargeNum) {
			chargeNum = mchargeNum;
		}

		@Override
		public int getCount() {
			return chargeNum.length;
		}

		@Override
		public String getItem(int position) {
			return chargeNum[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(Helper.getLayoutId(activity, "yeepay_grid_item"),
						parent, false);
			}

			TextView t = (TextView) convertView
					.findViewById(Helper.getResId(activity, "yeepay_grid_item_amount_tv"));
			if (position == selectedAmountId) {
				t.setBackgroundDrawable(getResources().getDrawable(
						Helper.getResDraw(activity, "yeepay_grid_item_selected_bg")));
			} else {
				t.setBackgroundDrawable(getResources().getDrawable(
						Helper.getResDraw(activity, "yeepay_grid_item_normal_bg")));
			}
			t.setText(getItem(position) + "元");
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sdk.makeToast(getItem(position) + "元");
					selectedAmountId = position;
					selectedAmount = getItem(position);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

}
