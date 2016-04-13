package zty.sdk.paeser;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.model.PayPreMessage;
import zty.sdk.utils.Util_G;

public class PayPreMsgPaser implements ResponseParser<PayPreMessage> {

	@Override
	public PayPreMessage getResponse(String response) {
		
		try {
			Util_G.debug_i(Constants.TAG1, "开始解析："+response);
			JSONObject json = new JSONObject(response);
			PayPreMessage account = new PayPreMessage(json);
			return account;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
