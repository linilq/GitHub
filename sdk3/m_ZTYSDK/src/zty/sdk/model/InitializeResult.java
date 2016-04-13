package zty.sdk.model;

import java.io.Serializable;

public class InitializeResult implements SDKObject, Serializable {

    public String deviceId;
   // public String url;
    public InitializeResult(String deviceId) {
        this.deviceId = deviceId;
        //this.url = url;
    }

   
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String toString() {
        return "InitializeResult{" +
                "deviceId='" + deviceId + '\'' +
                '}';
    }

}
