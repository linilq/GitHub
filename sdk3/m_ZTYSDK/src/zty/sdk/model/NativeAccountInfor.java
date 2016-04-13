package zty.sdk.model;

import org.json.JSONObject;

import zty.sdk.alipay.Base64;

/**
 * 本地账户信息model类
 * 
 * @author Administrator
 * 
 */
public class NativeAccountInfor {

	/**
	 * 账号顺序，默认显示值为1的账号
	 */
	private int indexnum;
	private String usn;
	private String psd;
	/**
	 * 绑定状态，false未绑定，true绑定
	 */
	private String bstatus;
	/**
	 * 通知用户绑定的状态
	 */
	private String nstatus;
	/**
	 * 绑定手机号码
	 */
	private String pnum;
	private String preferpayway;
	

	public NativeAccountInfor() {
	}

	public NativeAccountInfor(JSONObject json) {
		indexnum = json.optInt("indexnum");
		usn = json.optString("usn");
		psd = json.optString("psd");
		bstatus = json.optString("bstatus");
		nstatus = json.optString("nstatus");
		pnum = Base64.decode(json.optString("pnum")).toString();
		preferpayway = json.optString("preferpayway");
	}

	
	public String getPreferpayway() {
		return preferpayway;
	}

	public void setPreferpayway(String preferpayway) {
		this.preferpayway = preferpayway;
	}

	public int getIndexnum() {
		return indexnum;
	}

	public void setIndexnum(int indexnum) {
		this.indexnum = indexnum;
	}

	public String getUsn() {
		return usn;
	}

	public void setUsn(String username) {
		this.usn = username;
	}

	public String getPsd() {
		return psd;
	}

	public void setPsd(String psd) {
		this.psd = psd;
	}

	public String getBstatus() {
		return bstatus;
	}

	public void setBstatus(String bstatus) {
		this.bstatus = bstatus;
	}

	public String getNstatus() {
		return nstatus;
	}

	public void setNstatus(String nstatus) {
		this.nstatus = nstatus;
	}

	public String getPnum() {
		return pnum;
	}

	public void setPnum(String pnum) {
		this.pnum = pnum;
	}

	@Override
	public String toString() {
		return "NativeAccountInfor [index=" + indexnum + ", username=" + usn
				+ ", psd=" + psd + ", bstatus=" + bstatus + ", nstatus="
				+ nstatus + ", pnum=" + pnum + ", preferpayway=" + preferpayway
				+ "]";
	}

}
