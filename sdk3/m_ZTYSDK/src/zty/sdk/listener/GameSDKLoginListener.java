package zty.sdk.listener;
/**
 * 登陆结果监听器，处理登陆成功与失败的相关逻辑
 * 
 * @author Administrator
 *
 */
public interface GameSDKLoginListener {

	/**
	 * 登陆成功
	 * @param username
	 * @param userId
	 * @param sign
	 */
    public void onLoginSucess(String username,int userId,String sign);

    /**
     * 登陆失败
     */
    public void onLoginCancelled();//


}
