package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.MmpayOrderInfo;

public class MmpayOrderInfoParser  implements ResponseParser<MmpayOrderInfo> {
    @Override
    
    public MmpayOrderInfo getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            MmpayOrderInfo orderInfo = new MmpayOrderInfo();
            orderInfo.setRet(object.getInt("ret"));
            orderInfo.setOrderNo(object.getString("order_no"));
            
            return orderInfo;

        } catch (JSONException ex) {
            return null;
        }

    }

}
