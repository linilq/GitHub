package zty.sdk.paeser;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.http.ResponseParser;
import zty.sdk.model.InitializeResult;

public class InitializeResultParser implements ResponseParser<InitializeResult> {
    @Override
    public InitializeResult getResponse(String response) {

        try {
            JSONObject object = new JSONObject(response);
            return new InitializeResult(object.getString("device_id"));
        } catch (JSONException ex) {
            return null;
        }

    }
}
