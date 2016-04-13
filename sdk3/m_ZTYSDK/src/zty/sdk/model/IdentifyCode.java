package zty.sdk.model;

public class IdentifyCode {

	private String addTime;
	private String addDate;
	private String result;
	private String Message;
	private String code;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddDate() {
		return addDate;
	}
	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "IdentifyCode [addTime=" + addTime + ", addDate=" + addDate
				+ ", result=" + result + ", Message=" + Message + ", code="
				+ code + "]";
	}
	
	
	
	
	
	

}
