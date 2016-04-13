package zty.sdk.utils;

import android.os.Handler;
import android.view.View;



/**
 * 防止用户快速点击 发出两次请求(或者迅速弹出两个框)
 * @author L
 *
 */
public abstract class MyOnClickListener implements View.OnClickListener {
	
	private static long clickTime = 0L;
	public final int NOTIFYDELAY = 2;
	public long delay = 700;
	public Handler handlerDelay = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				delay = 700;
				break;
			case NOTIFYDELAY:
				delay = 1200;
				sendEmptyMessageDelayed(1, delay);
				break;
			default:
				break;
			}
		};
	};
	public void onClick(View v){
		v.setSoundEffectsEnabled(false);//屏蔽系统的 按键音效
		if(System.currentTimeMillis() - clickTime>delay){
			clickTime=System.currentTimeMillis();
//			playSound();
			onMyClick(v);
		}
	}

	/**
	 * 如果要加按钮音效 可以统一在这里加
	 */
	private void playSound(){
		try{
//			SoundUtil.play(Common.BTN_CLICK);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public abstract void onMyClick(View v);
}
