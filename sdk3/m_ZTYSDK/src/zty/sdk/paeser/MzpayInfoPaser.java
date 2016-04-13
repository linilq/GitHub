package zty.sdk.paeser;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.MzPayInfo;


public class MzpayInfoPaser implements ResponseParser<MzPayInfo>{

	@Override
	public MzPayInfo getResponse(String response) {
		try {

            JSONObject object = new JSONObject(response);
            MzPayInfo orderInfo = new MzPayInfo();
            orderInfo.setCost(object.getInt("cost"));
            orderInfo.setRet(object.getInt("ret"));
            return orderInfo;

        } catch (JSONException ex) {
            return null;
        }
	}
	
	

}
