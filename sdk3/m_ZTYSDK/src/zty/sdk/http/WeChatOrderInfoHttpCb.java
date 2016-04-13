package zty.sdk.http;

import java.util.Map;

import zty.sdk.game.Constants;
import zty.sdk.model.WeiXinOrderInfo;
import zty.sdk.utils.Helper;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.Util_G;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.switfpass.pay.service.GetPrepayIdResult;
import com.switfpass.pay.utils.Util;
import com.switfpass.pay.utils.XmlUtils;

public class WeChatOrderInfoHttpCb implements HttpCallback<WeiXinOrderInfo> {

	private Activity mContext;
	private String preOrderInfo;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==11)
				goto_url(preOrderInfo);
		};
	};
	
	public WeChatOrderInfoHttpCb(Activity activity){
		mContext = activity;
	}
	
	
	@Override
	public void onSuccess(WeiXinOrderInfo object) {
		preOrderInfo = object.getOrderInfo();
		if (StringUtil.isEmpty(preOrderInfo)) {
			Toast.makeText(mContext, "获取订单失败，请稍后重试",
					Toast.LENGTH_SHORT).show();
		} else {
			
			goto_url(preOrderInfo);
		}
		
	}

	@Override
	public void onFailure(int errorCode, String errorMessage) {
		Toast.makeText(mContext, errorMessage,
				Toast.LENGTH_SHORT).show();
		
	}

	public void goto_url(String result) {
		Util_G.debug_i(Constants.TAG1, "wechat支付请求信息："+result);
		GetPrepayIdTask task = new GetPrepayIdTask();
		task.execute(result);
		
	}
	
	private class GetPrepayIdTask extends AsyncTask<String, Void, Map<String, String>>{
        
        private ProgressDialog dialog;
        private String amountStr;
        
        @Override
        protected void onPreExecute(){
            dialog =
                ProgressDialog.show(mContext,
                		mContext.getString(Helper.getResStr(mContext, "app_tip")),
                		
                		mContext.getString(Helper.getResStr(mContext, "getting_prepayid")));
        }
        
        @Override
        protected void onPostExecute(Map<String, String> result){
            if (dialog != null){
                dialog.dismiss();
            }
            
            if (result == null){
                Toast.makeText(mContext, "下订单失败", Toast.LENGTH_LONG).show();
                handler.sendEmptyMessage(11);
            }else{
                if (result.get("status").equalsIgnoreCase("0")){
                	// 成功
                    Toast.makeText(mContext, "进入微信", Toast.LENGTH_LONG).show();
                    RequestMsg msg = new RequestMsg();
                    
                    int amount = Integer.valueOf(amountStr)/100;
                    msg.setMoney(Double.parseDouble(amount+""));
                    msg.setTokenId(result.get("token_id"));
                    msg.setOutTradeNo(result.get("out_trade_no"));
                    // 微信wap支付
                    msg.setTradeType(MainApplication.PAY_WX_WAP);
                    PayPlugin.unifiedH5Pay(mContext, msg);
                    
                }else{
                    Toast.makeText(mContext, "下订单失败", Toast.LENGTH_LONG)
                        .show();
                    handler.sendEmptyMessage(11);
                }
                
            }
            
        }
        
        @Override
        protected void onCancelled(){
            super.onCancelled();
        }
        
        @Override
        protected Map<String, String> doInBackground(String... params){
            // 统一预下单接口
            String url = "https://paya.swiftpass.cn/pay/gateway";
            
            String entity = params[0];
            amountStr = (String) XmlUtils.parse(entity).get("total_fee");
            
            Log.d(Constants.TAG1, "doInBackground, url = " + url);
            Log.d(Constants.TAG1, "doInBackground, entity = " + entity);
            Log.d(Constants.TAG1, "doInBackground, amountStr = " + amountStr);
            
            GetPrepayIdResult result = new GetPrepayIdResult();
            
            byte[] buf = Util.httpPost(url, entity);
            if (buf == null || buf.length == 0){
                return null;
            }
            String content = new String(buf);
            
            Log.d(Constants.TAG1, "doInBackground, content = " + content);
            
            result.parseFrom(content);
          
            
           try{
                return XmlUtils.parse(content);
            }catch (Exception e){
             
                e.printStackTrace();
                return null;
            }
        }
    }
}
