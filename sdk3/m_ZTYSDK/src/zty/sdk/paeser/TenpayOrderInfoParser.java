package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.TenpayOrderInfo;


public class TenpayOrderInfoParser implements ResponseParser<TenpayOrderInfo> {
    @Override
    public TenpayOrderInfo getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            TenpayOrderInfo orderInfo = new TenpayOrderInfo();
            orderInfo.setOrderInfo(object.getString("order_info"));
           // orderInfo.setSign(object.getString("sign"));
           // orderInfo.setSignType(object.getString("sign_type"));
            return orderInfo;

        } catch (JSONException ex) {
            return null;
        }

    }
}
