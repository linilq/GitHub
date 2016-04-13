package zty.sdk.model;

public class YeepayCardResult {

    private int code;
    private String pay_no;
    private String message;
    public int trytimers;
    
    public YeepayCardResult(int code, String message,String pay_no) {
        this.code = code;
        this.message = message;
        this.pay_no = pay_no;
        trytimers = 0;
    }

    public String getPayNO() {
        return pay_no;
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
