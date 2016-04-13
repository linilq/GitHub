package zty.sdk.http;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import zty.sdk.game.Constants;
import zty.sdk.game.GameSDK;
import zty.sdk.utils.DeviceInfoUtil;
import zty.sdk.utils.DialogUtil;
import zty.sdk.utils.StringUtil;
import zty.sdk.utils.Util_G;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.AsyncTask;
import android.os.Handler;

public class HttpRequest<T> extends AsyncTask<String, Integer, T> {

    private Context context;
    private ResponseParser<T> responseParser;
    private HttpCallback<T> callBack;

    private int errorCode;
    private String errorMessage;
    private boolean running;
    private boolean showProgrsess = false;
    private int SHOW_PRO = 0;
    private int Exception_CLOSE_AUTOPRO = 1;
    private int Succ_CLOSE_AUTOPRO = 3;
    private int Exception_CLOSE_SELFDEFPRO = 2;
    
    
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		if (msg.what == SHOW_PRO) {
    			 DialogUtil.showProgressDialog(context, "", 0,null); 
			}else if (msg.what == Exception_CLOSE_AUTOPRO) {
				DialogUtil.closeProgressDialog();
				callBack.onFailure(errorCode, errorMessage);
			}else if(msg.what == Exception_CLOSE_SELFDEFPRO){
				callBack.onFailure(errorCode, errorMessage);
			}else if(msg.what == Succ_CLOSE_AUTOPRO){
				DialogUtil.closeProgressDialog();
			}
    	};
    };
    
    public boolean isRunning() {
		return running;
	}

	public HttpRequest(Context context, ResponseParser<T> responseParser, HttpCallback<T> callBack,boolean showProgrsess) {
        this.context = context;
        this.responseParser = responseParser;
        this.callBack = callBack;
        this.showProgrsess = showProgrsess;
    }

    @Override
    protected void onPreExecute() {
    	Util_G.debug_e("", "网络访问onPreExecute()：");
    	running = true;
    	GameSDK.getOkInstance().mRequest = null;
    	if (this.showProgrsess) {
    		handler.sendEmptyMessage(SHOW_PRO);
		}
    }

    @Override
    protected T doInBackground(String... strings) {

    	 Util_G.debug_e("", "网络访问doInBackground地址："+strings[0]);
        if (strings.length == 0) {
            errorCode = Constants.ERROR_CODE_SYS;
            errorMessage = "参数错误！";
            return null;
        }
       
        try {

            String url = strings[0];
            String postData = strings.length == 2 ? strings[1] : null;

            HttpClient client = new DefaultHttpClient();
            setProxyIfNecessary(context, client);
            HttpParams localHttpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(localHttpParams, 30000);
            HttpConnectionParams.setSoTimeout(localHttpParams, 30000);
            HttpClientParams.setRedirecting(localHttpParams, false);

            HttpPost post = new HttpPost(url);
            post.setHeader("Accept", "*/*");
            post.setHeader("Accept-Charset", "UTF-8,*;q=0.5");
            post.setHeader("Accept-Encoding", "gzip,deflate");
            post.setHeader("Accept-Language", "zh-CN");
            post.setHeader("User-Agent", "ZTYSDK");
            //设置post的数据
            if (postData != null) {
                HttpEntity postEntify = new ByteArrayEntity(postData.getBytes("UTF-8"));//UnsupportedEncodingException
                post.setEntity(postEntify);
            }
            Util_G.debug_i(Constants.TAG, url);
            Util_G.debug_i(Constants.TAG, postData);
            HttpResponse response = client.execute(post);//ClientProtocolException
            int ret = response.getStatusLine().getStatusCode();
            
            if (ret == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase("gzip")) {
                            entity = new InflatingEntity(response.getEntity());
                            break;
                        }
                    }
                }

                String content = EntityUtils.toString(entity);// IOException
                
                Util_G.debug_i(Constants.TAG1, "网络访问结果："+content);
                if (!StringUtil.isEmpty(content)&&responseParser!=null) {
                    return responseParser.getResponse(content);
                } else {
                    return null;
                }

            }

        } catch (Exception ex) {
        	if(ex != null)
        	Util_G.debug_i("test", ex.toString());
            errorCode = Constants.ERROR_CODE_SYS;
            errorMessage = "网络错误，网络不给力";
            boolean info = DeviceInfoUtil.is_network(context);
            if(!info)
            {
            	errorCode = Constants.ERROR_CODE_NET;
                errorMessage = "网络没打开，请先打开网络";
            }
            cancelTask();
        }
        return null;

    }

    /**
     * 取消任务
     */
    public void cancelTask()
    {
    	if (this.showProgrsess) {
    		//如果使用自动进度条，则发送消息CLOSE_PRO，关闭进度条，并执行回调的onFailure方法
    		handler.sendEmptyMessage(Exception_CLOSE_AUTOPRO);
    	}else{
    		//如未使用自动进度条，则发送消息CLOSE_PRO_SELFDEFINED,执行回调onFailure方法
    		handler.sendEmptyMessage(Exception_CLOSE_SELFDEFPRO);
    	}
    	running = false;
    }
    
    @Override
    protected void onPostExecute(T response) {
        if(running)
        {
        	if (this.showProgrsess) {
        		handler.sendEmptyMessage(Succ_CLOSE_AUTOPRO);
        	}
	        if (response != null) {
	        	if(callBack != null)
	            callBack.onSuccess(response);
	        } else {
	        	if(callBack != null)
	            callBack.onFailure(errorCode, errorMessage);
	        }
        }
    }

    private static void setProxyIfNecessary(Context context, HttpClient client) {

        NetworkInfo localNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((localNetworkInfo != null) &&
                (localNetworkInfo.isAvailable()) &&
                (localNetworkInfo.getType() == 0)) {
            String host = Proxy.getDefaultHost();
            int port = Proxy.getDefaultPort();
            if ((host != null) && (port >= 0)) {
                HttpHost httpHost = new HttpHost(host, port);
                client.getParams().setParameter("http.route.default-proxy", httpHost);
            }
        }

    }


}
