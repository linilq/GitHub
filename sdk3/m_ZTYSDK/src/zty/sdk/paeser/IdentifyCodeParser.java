package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.model.IdentifyCode;
import zty.sdk.utils.Util_G;

import android.util.Log;

public class IdentifyCodeParser implements ResponseParser<IdentifyCode> {
    @Override
    public IdentifyCode getResponse(String response) {

        try {
        	Util_G.debug_e(Constants.TAG1, "开始解析："+response);
            JSONObject object = new JSONObject(response);
            IdentifyCode identifyCode = new IdentifyCode();
            identifyCode.setResult(object.getString("result"));
            
            identifyCode.setMessage(object.getString("message"));
            
            if (identifyCode.getResult().equals("000")) {
            	identifyCode.setAddDate(object.getString("addDate"));
                identifyCode.setAddTime(object.getString("addTime"));
			}
            return identifyCode;

        } catch (JSONException ex) {
            Log.e(Constants.TAG1, ex.toString());
            ex.printStackTrace();
            return null;
        }

    }
}