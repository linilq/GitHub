package zty.sdk.paeser;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.YeepayCardResult;

public class YeepayCardResultParser implements ResponseParser<YeepayCardResult> {
    @Override
    public YeepayCardResult getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            return new YeepayCardResult(object.getInt("code"), object.getString("message"),object.getString("pay_no"));

        } catch (JSONException ex) {
            return null;
        }

    }
}
