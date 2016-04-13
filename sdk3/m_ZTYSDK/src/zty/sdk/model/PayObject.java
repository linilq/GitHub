package zty.sdk.model;
/**
 * 储存支付的名称和对应的表达量
 * @author zhijian
 *
 */
public class PayObject {

	private String payWay = "";
	private String payName = "";
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getPayName() {
		return payName;
	}
	public void setPayName(String payName) {
		this.payName = payName;
	}
	
	
}
