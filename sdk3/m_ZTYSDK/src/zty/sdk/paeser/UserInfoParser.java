package zty.sdk.paeser;

import org.json.JSONException;
import org.json.JSONObject;

import zty.sdk.game.Constants;
import zty.sdk.http.ResponseParser;
import zty.sdk.model.UserInfo;
import zty.sdk.utils.Util_G;
import android.util.Log;

public class UserInfoParser implements ResponseParser<UserInfo> {
    @Override
    public UserInfo getResponse(String response) {

        try {

            JSONObject object = new JSONObject(response);
            UserInfo userInfo = new UserInfo();
            userInfo.setResult(object.optInt("result"));
            userInfo.setMessage(object.optString("message"));
            if (userInfo.getResult() == 1) {
//                userInfo.setUserId(object.getInt("ID"));
                userInfo.setLoginAccount(object.optString("LOGIN_ACCOUNT"));
                userInfo.setUserId(object.optInt("ACCOUNT_ID"));
                userInfo.setSign(object.optString("sign"));
                ////***************2015-03-25*********************////
                Util_G.debug_i(Constants.TAG1, object.optString("bindstatus"));
                userInfo.setB_status(object.optString("bindstatus"));
                userInfo.setPnum(object.optString("pnum"));
               ////***************2015-03-25*********************////
                userInfo.setAmount(object.optInt("amount"));
                userInfo.setMax_amount(object.optInt("max_amount"));
                Util_G.debug_i("test", "amount="+object.optInt("amount"));
                Util_G.debug_i("test", "max_amount="+object.optInt("max_amount"));
            }
            return userInfo;

        } catch (JSONException ex) {
            Log.e(Constants.TAG, ex.toString());
            ex.printStackTrace();
            return null;
        }

    }
}