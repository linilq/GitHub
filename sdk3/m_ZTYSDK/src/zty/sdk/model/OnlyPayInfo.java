package zty.sdk.model;

public class OnlyPayInfo {

	private int cost;
	private int resultCode;
	
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	@Override
	public String toString() {
		return "OnlyPayInfo [cost=" + cost + ", resultCode=" + resultCode + "]";
	}
	
	
}
