package zty.sdk.fragment;

import zty.sdk.game.Constants;
import zty.sdk.utils.Helper;
import zty.sdk.utils.MetricUtil;
import zty.sdk.utils.Util_G;
import zty.sdk.views.MyGridView;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MzMoneyChooseFrag extends BaseFragment {

	private Button submit;
	private MyGridView grid;
	private String[] mzChargeNums = { "10", "20", "30", "50", "100", "500",
			"1000" };
	private int selectedAmountId = 1;
	private String selectedAmount = "20";
	private EditText edText;
	private MyGridViewAdapter adapter;
	private onAmountChangeListener onAChangeListener;

	public interface onAmountChangeListener {
		public void onAmountChoose(String amount);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			if (activity != null) {
				onAChangeListener = (onAmountChangeListener) activity;
			} else {
				throw new NullPointerException("activity is null");
			}

		} catch (Exception e) {
			throw new ClassCastException(activity.toString()
					+ "must implements onPayitemClickedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(Helper.getLayoutId(activity, "f_mz_moneychoose"), container,
				false);
		android.view.ViewGroup.LayoutParams params = view.getLayoutParams();

		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			params.height = MetricUtil.getDip(activity, 280);
			view.setPadding(0, 0, 0, 20);
			view.setLayoutParams(params);
			view.invalidate();
		}else{
			Log.v("LINILQTEST", "MzMoneyChooseFrag: now the window ISNOT portrit");
		}
		
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		initData();
		showView();

	}

	private void initData() {
		sdk.isMzCharge = true;
		sdk.mzcharge = 20;
		onAChangeListener.onAmountChoose(selectedAmount);

	}

	private void showView() {
		adapter = new MyGridViewAdapter(mzChargeNums);
		grid.setAdapter(adapter);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sdk.mzcharge = Integer.valueOf(selectedAmount);
				if (sdk.mzcharge==0) {
					sdk.makeToast(activity.getResources().getString(Helper.getResStr(activity, "pay_input_zero_str")));
				}else{
					startFragment(new MzPaywayChooseFrag());
				}
				
			}
		});

	}

	private void initView() {
		submit = findViewById(Helper.getResId(activity, "mzchoose_submit_bt"));
		grid = findViewById(Helper.getResId(activity, "mzchoose_price_grid"));

	}

	private class MyGridViewAdapter extends BaseAdapter {

		private String[] chargeNum;

		public MyGridViewAdapter(String[] mchargeNum) {
			chargeNum = mchargeNum;
		}

		@Override
		public int getCount() {
			return chargeNum.length + 1;
		}

		@Override
		public String getItem(int position) {
			if (position == chargeNum.length) {
				return "";
			}
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
			EditText eText = (EditText) convertView
					.findViewById(Helper.getResId(activity, "yeepay_grid_item_amount_et"));
			TextView t = (TextView) convertView
					.findViewById(Helper.getResId(activity, "yeepay_grid_item_amount_tv"));
			if (position == chargeNum.length) {
				edText = eText;
				edText.setVisibility(View.VISIBLE);
				edText.addTextChangedListener(getWatcher());

			} else {
				eText.setVisibility(View.GONE);
				if (position == selectedAmountId) {
					t.setBackgroundDrawable(getResources().getDrawable(
							Helper.getResDraw(activity, "yeepay_grid_item_selected_bg")));
				} else {
					t.setBackgroundDrawable(getResources().getDrawable(
							Helper.getResDraw(activity, "yeepay_grid_item_normal_bg")));
				}
				t.setText(getItem(position));
				convertView.setOnClickListener(getOnClickedListener(position));
			}
			return convertView;
		}

		public OnClickListener getOnClickedListener(final int position) {
			return new OnClickListener() {

				@Override
				public void onClick(View v) {
					Util_G.debug_i(Constants.TAG1, "position：" + position);
					selectedAmountId = position;
					selectedAmount = getItem(position);
					if (!edText.getText().toString().isEmpty()) {
						edText.setText("");
					}
					onAChangeListener.onAmountChoose(selectedAmount);
					adapter.notifyDataSetChanged();
				}
			};
		}

		public TextWatcher getWatcher() {
			return new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String temp = s.toString().trim();

					int length = (temp).length();
					if (length > 0) {
						selectedAmount = s.toString();
						onAChangeListener.onAmountChoose(selectedAmount);
						selectedAmountId = -1;
						adapter.notifyDataSetChanged();

					} else {
						selectedAmount = "0";
						onAChangeListener.onAmountChoose(selectedAmount);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			};
		}
	}
}
