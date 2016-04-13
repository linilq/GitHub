package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.YeepayCardRsqResult;

public class YeepayCardRsqResultParser implements ResponseParser<YeepayCardRsqResult> {
    @Override
    public YeepayCardRsqResult getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            return new YeepayCardRsqResult(object.getInt("code"), object.getString("message"));

        } catch (JSONException ex) {
            return null;
        }

    }
}
