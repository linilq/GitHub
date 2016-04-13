package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.model.OnlyPayInfo;
import zty.sdk.utils.Util_G;

public class OnlypayInfoPaser implements ResponseParser<OnlyPayInfo> {

	@Override
	public OnlyPayInfo getResponse(String response) {
		try {

            JSONObject object = new JSONObject(response);
            OnlyPayInfo orderInfo = new OnlyPayInfo();
            orderInfo.setCost(object.getInt("cost"));
            orderInfo.setResultCode(object.getInt("ret"));
            Util_G.debug_i(Constants.TAG, "专属币支付结果："+orderInfo.toString());
            return orderInfo;

        } catch (JSONException ex) {
            return null;
        }
	}

}
