package zty.sdk.model;

import org.json.JSONObject;

/**
 * 进入支付界面前的一些相关信息。如：支付金额列表，用户账户余额等
 * @author Administrator
 *
 */
public class PayPreMessage {

	/**
	 * 当前登陆账号的拇指币余额
	 */
	private int mz_balance;
	/**
	 * 当前登录账号的专属币账号余额
	 */
	private int only_balance;
	/**
	 * 移动话费卡金额串
	 */
	private String mobilePayStr;
	/**
	 * 联通话费卡金额串
	 */
	private String unicomPayStr;
	/**
	 * 电信话费卡金额串
	 */
	private String telecomPayStr;
	/**
	 * 骏网支付卡金额串
	 */
	private String jcardPayStr;
	/**
	 * 纵游支付卡金额串
	 */
	private String zycardPayStr;
	/**
	 * 32支付卡金额串
	 */
	private String tscardPayStr;

	private String payWaySign;
	
	public PayPreMessage(){
		
	}
	
	public PayPreMessage(JSONObject json){
		mz_balance = Integer.valueOf(json.optString("userbalance"));
		only_balance = Integer.valueOf(json.optString("onlybalance"));
		payWaySign = json.optString("paywaysign");
		mobilePayStr = json.optString("mobile");
		unicomPayStr = json.optString("unicom");
		telecomPayStr = json.optString("telecom");
		jcardPayStr = json.optString("jcard");
		zycardPayStr = json.optString("zycard");
		tscardPayStr = json.optString("tscard");
	}
	
	public int getOnly_balance() {
		return only_balance;
	}

	public void setOnly_balance(int only_balance) {
		this.only_balance = only_balance;
	}

	public int getMz_balance() {
		return mz_balance;
	}

	public void setMz_balance(int mz_balance) {
		this.mz_balance = mz_balance;
	}

	public String getMobilePayStr() {
		return mobilePayStr;
	}

	public void setMobilePayStr(String mobilePayStr) {
		this.mobilePayStr = mobilePayStr;
	}

	public String getUnicomPayStr() {
		return unicomPayStr;
	}

	public void setUnicomPayStr(String unicomPayStr) {
		this.unicomPayStr = unicomPayStr;
	}

	public String getTelecomPayStr() {
		return telecomPayStr;
	}

	public void setTelecomPayStr(String telecomPayStr) {
		this.telecomPayStr = telecomPayStr;
	}

	public String getJcardPayStr() {
		return jcardPayStr;
	}

	public void setJcardPayStr(String jcardPayStr) {
		this.jcardPayStr = jcardPayStr;
	}

	public String getZycardPayStr() {
		return zycardPayStr;
	}

	public void setZycardPayStr(String zycardPayStr) {
		this.zycardPayStr = zycardPayStr;
	}

	public String getTscardPayStr() {
		return tscardPayStr;
	}

	public void setTscardPayStr(String tscardPayStr) {
		this.tscardPayStr = tscardPayStr;
	}
	public String getPayWaySign() {
		return payWaySign;
	}

	public void setPayWaySign(String payWaySign) {
		this.payWaySign = payWaySign;
	}

	@Override
	public String toString() {
		return "PayPreMessage [mz_balance=" + mz_balance + ", only_balance="
				+ only_balance + ", mobilePayStr=" + mobilePayStr
				+ ", unicomPayStr=" + unicomPayStr + ", telecomPayStr="
				+ telecomPayStr + ", jcardPayStr=" + jcardPayStr
				+ ", zycardPayStr=" + zycardPayStr + ", tscardPayStr="
				+ tscardPayStr +", payWaySign="
						+ payWaySign + "]";
	}

	
	
	
	
}
