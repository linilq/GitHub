package zty.sdk.paeser;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.WeiXinOrderInfo;


public class WeiXinOrderInfoParser implements ResponseParser<WeiXinOrderInfo> {
    @Override
    public WeiXinOrderInfo getResponse(String response) {

        try {

//            JSONObject object = new JSONObject(response);
            WeiXinOrderInfo orderInfo = new WeiXinOrderInfo();
            orderInfo.setOrderInfo(response);
           // orderInfo.setSign(object.getString("sign"));
           // orderInfo.setSignType(object.getString("sign_type"));
            return orderInfo;

        } catch (Exception ex) {
            return null;
        }

    }
}
