package zty.sdk.paeser;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.utils.Util_G;

public class FindPswPaser implements ResponseParser<String>{

	@Override
	public String getResponse(String response) {
		String msg = "";
		try {
			Util_G.debug_i(Constants.TAG1, "开始解析："+response);
			JSONObject json = new JSONObject(response);
			msg = json.getString("result");
			return msg;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
	}

}
