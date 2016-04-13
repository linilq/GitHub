package zty.sdk.model;

public class UnionpayOrderInfo {
	 private String orderInfo;
	 

	    public UnionpayOrderInfo() {
	    }

	    public String getOrderInfo() {
	        return orderInfo;
	    }

	    public void setOrderInfo(String orderInfo) {
	        this.orderInfo = orderInfo;
	    }

	    @Override
	    public String toString() {
	        return "AlipayOrderInfo{" +
	                "orderInfo='" + orderInfo + '\'' +
	                '}';
	    }
}
