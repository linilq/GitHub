package zty.sdk.model;

public class TenpayOrderInfo {
	 private String orderInfo;
	 

	    public TenpayOrderInfo() {
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
