package zty.sdk.model;

public class YeepayCardRsqResult {
	  private int code;
	    private String message;

	    public YeepayCardRsqResult(int code, String message) {
	        this.code = code;
	        this.message = message;
	    }

	    public int getCode() {
	        return code;
	    }

	    public void setCode(int code) {
	        this.code = code;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    @Override
	    public String toString() {
	        return "YeepayCardResult{" +
	                "code=" + code +
	                ", message='" + message + '\'' +
	                '}';
	    }
}
