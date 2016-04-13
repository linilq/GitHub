package zty.sdk.paeser;


import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.model.GetMSMResult;

import android.util.Log;

public class GetMSMResultParser implements ResponseParser<GetMSMResult> {
    @Override
    public GetMSMResult getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            GetMSMResult activateResult = new GetMSMResult();
            activateResult.setNum(object.optString("num"));
            activateResult.setContent(object.optString("content"));        
            return activateResult;

        } catch (JSONException ex) {
            Log.e(Constants.TAG, ex.toString());
            ex.printStackTrace();
            return null;
        }

    }
}