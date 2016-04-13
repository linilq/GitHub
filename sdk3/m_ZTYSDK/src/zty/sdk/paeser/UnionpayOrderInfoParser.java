package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.UnionpayOrderInfo;

public class UnionpayOrderInfoParser implements ResponseParser<UnionpayOrderInfo> {
    @Override
    public UnionpayOrderInfo getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            UnionpayOrderInfo orderInfo = new UnionpayOrderInfo();
            orderInfo.setOrderInfo(object.getString("order_info"));
           // orderInfo.setSign(object.getString("sign"));
           // orderInfo.setSignType(object.getString("sign_type"));
            return orderInfo;

        } catch (JSONException ex) {
            return null;
        }

    }
}

