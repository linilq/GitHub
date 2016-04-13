package zty.sdk.paeser;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.model.ActivateResult;

public class ActivateResultParser implements ResponseParser<ActivateResult> {
    @Override
    public ActivateResult getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            ActivateResult activateResult = new ActivateResult();
            activateResult.setServerUrl(object.optString("serverurl"));
            activateResult.setChangePassword(object.optString("changepassword_url"));
            activateResult.setRegisterUrl(object.optString("register_url"));
            activateResult.setLoginUrl(object.optString("login_url"));
            activateResult.setAlipayWapUrl(object.optString("alipay_wap_url"));
            activateResult.setPayways(object.optString("payways"));
            activateResult.setAdfd(object.optString("afdf"));
            activateResult.setDipcon(object.optString("dipcon"));
            activateResult.setDipcon2(object.optString("dipcon2"));
            activateResult.setDipurl(object.optString("dipurl"));
            activateResult.setNoturl(object.optString("noturl"));
            activateResult.setExiturl(object.optString("exiturl"));
            activateResult.setIdenti1(object.getString("reqCodeUrl"));
            activateResult.setIdenti2(object.getString("identiCodeUrl"));
            activateResult.setPaywaysign(object.optString("cpaywaysign"));
            return activateResult;

        } catch (JSONException ex) {
            Log.e(Constants.TAG, ex.toString());
            ex.printStackTrace();
            return null;
        }

    }
}