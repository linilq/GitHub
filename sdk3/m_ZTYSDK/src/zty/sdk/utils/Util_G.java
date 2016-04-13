package zty.sdk.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import zty.sdk.game.GameSDK;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

//import com.example.jstest.MainActivity;
//MANAGE_APP_TOKENS
public class Util_G
{
	public static String busylog = "zty.txt"; 
	public static void debug(String str){
		if(GameSDK.getOkInstance().debug.equals("1"))
    	System.out.println(str);
    }
    
	public static void debug_buf(String str){
		byte bts[] = str.getBytes();
		String disstr = "";
		for(byte b:bts)
		{
			disstr += ","+b;
			
		}
		System.out.println(disstr);
    }
	
	public static void debug_buf(byte bts[]){
		String disstr = "";
		for(byte b:bts)
		{
			disstr += ","+b;
			
		}
		System.out.println(disstr);
    }
    
    public static void debug(int data){
    	if(GameSDK.getOkInstance().debug.equals("1"))
    	System.out.println(data);
    }
	
	public static void debug_i(String tag,String str){
		if(GameSDK.getOkInstance()==null)
			return;
		if(GameSDK.getOkInstance().debug.equals("1"))
		Log.i(tag, str);
    }
	public static void printStackTrace(Exception e){
	   e.printStackTrace();
    }
	
	public static void printfStack()
	{
		 Throwable ex = new Throwable();
	     StackTraceElement[] stackElements = ex.getStackTrace();
	
	     if (stackElements != null) {
	         for (int i = 0; i < stackElements.length; i++) {
	        	 Log.v("LINILQTEST",stackElements[i].getClassName()+"."+stackElements[i].getMethodName());//返回类的完全限定名，该类包含由该堆栈跟踪元素 ?  示的执行点 ?
	         }
	     }
	 }

	
    public static void debug_e(String tag,String str){
    	//File f = new File("data/data/dasdf/zty.txt");				
    	//OutputStream out = null;
    	if(GameSDK.getOkInstance().debug.equals("1")){
    		tag = busylog;
        	String time = Util_G.getDateTime();
        	String sdpath = Environment.getExternalStorageDirectory()  
                    .getAbsolutePath();
        	writeFileData(sdpath+"/"+tag,time+": ");
        	writeFileData(sdpath+"/"+tag,str+"\n"); 
        	/*File f = new File("sdcard/"+tag,time+": ");
            if(f.exists())
            {
            	return;
            }*/
    	}
		   	
    }
    
    public static void writeFileData(String fileName,String message)
    { 
        File f = new File(fileName);
        boolean sign = f.exists();
        if(!sign)
        {
        	return;
        }
        
        try{ 

         FileOutputStream fout =new FileOutputStream(fileName,true);         
         byte [] bytes = message.getBytes(); 

         fout.write(bytes); 

          fout.close(); 

         } 

        catch(Exception e){ 

         e.printStackTrace(); 

        } 

    }    

    public static void saveFile(InputStream inStream,String filename,boolean bapend)
	{
			
		try {
			
			File file = new File(filename);
			file.createNewFile();
			FileOutputStream bao = new FileOutputStream(file,bapend);
			int len=-1;
			byte[] bt = new byte[2048];       
			
			while((len =  inStream.read(bt)) != -1)     
			{     
				bao.write(bt,0,len);
			};
			
			bao.flush();
			bao.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}
	
    /**
	 * 手机系统文件：【data】
	 * @param fileName：文件名，并非路径
	 * @return
	 * @throws IOException 
	 */
	public  static ByteArrayOutputStream getFileOutputStream(String fileName)  {
		FileInputStream f = null;		
		BufferedInputStream bu;
		ByteArrayOutputStream bo = null;
		
		byte buf[]=new byte[1024];
		
		int n;
		
		File path = new File(fileName);
		if(!path.exists()){
			return bo;
		}
		
		try {
			f = new FileInputStream(fileName);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		bu = new BufferedInputStream(f);
		bo = new ByteArrayOutputStream();
		
			
		try {
			while((n=bu.read(buf))!=-1)
			{
				//bo.write(buf);
				bo.write(buf, 0, n);
				//buf =new byte[1024];
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			f.close();
			bu.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bo;
	}
	
	private static Toast myToast = null;
	public static void DisplayToast(String str, int length)
	{
		//Toast.makeText(this, str, Toast.LENGTH_SHORT).show();//���ۼ�
		
		if(myToast == null){
			//myToast = Toast.makeText(MainActivity.getInstance(), str, length);
		}
		
		//myToast.setText(str);
		//myToast.setDuration(length);
		//myToast.show();
	}
	
	public static String strAddStr(String... strings){
		StringBuffer sb = new StringBuffer();
		for(String string:strings){
			sb.append(string);
		}
		return sb.toString();
	}
	
	public static String strAddStr(int... strings){
		StringBuffer sb = new StringBuffer();
		for(int string:strings){
			sb.append(string);
		}
		return sb.toString();
	}
	
	public static byte[] stringArray2byteArray(String[] content){
		
		if(content == null || content.length <= 0) return null;
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(byteOut);
		byte[] data = null;
		try {
			
			for(int i = 0, content_size = content.length; i < content_size; i++){
				if(content[i] == null){
					dOut.writeUTF("null");
				}else{
					dOut.writeUTF(content[i]);
				}
				
			}
			
			data = byteOut.toByteArray();
			dOut.close();
			byteOut.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return data;
	}
    
	public final static String utf8Decode(byte[] utf8_bytes) {
		try {
			return(new String(utf8_bytes, "UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}
	
	public final static byte[] utf8Encode(String utf8_str) {
		try {
			return(utf8_str.getBytes("UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @param ins
	 * @param MAXLEN Ĭ��ֵ-1
	 * @return
	 */
	public static byte[] getByteArrayFromInputstream(InputStream ins, int MAXLEN){
		
		if(MAXLEN == -1) MAXLEN = 30000;
		
		try {
			
			byte[] charset = new byte[MAXLEN];
			int ch = ins.read();
			int length = 0;
			while (ch != -1) {
				charset[length] = (byte) ch;
				ch = ins.read();
				length++;
			}
			byte[] xmlCharArray = new byte[length];
			System.arraycopy(charset, 0, xmlCharArray, 0, length);
			
			return (xmlCharArray);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public final static String replace(String content, String olds, String news) {
		int index = 0;
		while (true) {
			index = content.indexOf(olds, index);
			if (index == -1)
				break;
			content = content.substring(0, index) + news + content.substring(index + olds.length());
			index += news.length();
		}
		return content;
	}
	
	public static int dip2px(float dipValue){
		
		//Context context = MainActivity.getInstance();
		//final float scale = context.getResources().getDisplayMetrics().density;
	
		//return (int)(dipValue * scale + 0.5f);
		return 0;
	
	}

	public static int px2dip(float pxValue){
        
		//Context context = MainActivity.getInstance();
		//final float scale = context.getResources().getDisplayMetrics().density;
									
		//return (int)(pxValue / scale + 0.5f);	
		return 0;
	}
	
	public static Bitmap getBitmapFromID(int R_ID){
		//Bitmap bmp = ((BitmapDrawable) MainActivity.getInstance().getResources().getDrawable(R_ID)).getBitmap();
		//return bmp;
		return null;
	}
	
	public static Bitmap getBitmapFromID_1(int R_ID){
		//Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R_ID);
		//return bitmap;
		return null;
	}
	
	public static int getImageHeightFromID(int R_ID){
		Bitmap bitmap = getBitmapFromID(R_ID);
		return bitmap.getHeight();
	}
	
	public static boolean isNullStr(String str){
		if(str==null || str.length()<=0) return true;
		return false;
	}
	
	
	public static String getDateTime(){
		Calendar calendar=Calendar.getInstance();

		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		
		/*String time = Util_G.strAddStr(
				calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH), 
				calendar.get(Calendar.HOUR_OF_DAY), 
				calendar.get(Calendar.MINUTE), 
				calendar.get(Calendar.SECOND)
				);*/
		return time;
	}
	
     public static byte[] read(InputStream in, long len) {
		
		byte[] res = null;
		
		try {
			
			byte[] buff = new byte[100];
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			while (true) {
				int size = 0;
				if (len >= 100 || len == -1)
					size = in.read(buff);
				else
					size = in.read(buff, 0, (int) len);
				if (size == -1) {
					in.close();
					break;
				}
				bo.write(buff, 0, size);
				if (len != -1) {
					len -= size;
					if (len == 0)
						break;
				}
			}
			res = bo.toByteArray();
			bo.close();
			
		} catch (Exception e) {
			res  = null;
		}
		
		return res;
	}	
	public static void chmodRWFile(String filepath)
	{
	    	 String com = "rootsh chmod 0777 "+filepath;
	    	 Util_G.execCommon(com);    	
	}
	public static void ExecCommons(String[] commands) 
 	{

		   Process process = null;		  
             
		   DataOutputStream dataOutputStream = null;
		   DataInputStream dataintputStream = null;
		 
		   try {

		    process = Runtime.getRuntime().exec("rootsh cmd");//"rootsh cmd dffd"

		    dataOutputStream = new DataOutputStream(process.getOutputStream());

		    int length = commands.length;

		    for (int i = 0; i < length; i++) {		     
		     dataOutputStream.writeBytes(commands[i] + "\n");

		    }

		    dataOutputStream.writeBytes("exit\n");

		    dataOutputStream.flush();

		    process.waitFor();

		   } catch (Exception e) {

		    //Log.e("mvlog", "copy fail", e);

		   } finally {

		    try {
		    	
		    	dataintputStream = new DataInputStream(process.getInputStream());
	            //String res;
	            //res = dataintputStream.toString();
		    	byte[] res= read(dataintputStream,-1);
		    	 Util_G.debug_e(Util_G.busylog, "ִ�н��"+Util_G.utf8Decode(res));
		    	//Util_G.debug_e(Util_G.busylog, m_Package+"����Ŀ¼��"+path+filename);
		    	
		    	dataintputStream = new DataInputStream(process.getErrorStream());
		    	res= read(dataintputStream,-1);
		        Util_G.debug_e(Util_G.busylog, "ִ����Ϣ��"+Util_G.utf8Decode(res));
		    	//Log.e("mvlog", "su eorr");
		    	//Log.e("mvlog",Util_G.utf8Decode(res));
		        if (dataOutputStream != null)
		        {
		           dataOutputStream.close();
		        }

		        process.destroy();

		    } catch (Exception e) {

		    }

		 }

     }
	
	public static void execCommon(String str)
	{
	    try {
			Runtime.getRuntime().exec(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sleep(int second)
	{	
		try 
		{
			Thread.sleep(second*1000);			
		} 
		catch(InterruptedException e) 
		{			
			e.printStackTrace();
		}  
	}
	
	public static boolean comparestr(String str1,String str2,int length)
	{
		boolean ret = false;
		
		String cmpstr = str1;
		
		if(str1.length() > length)
		{
			cmpstr = str1.substring(0, length);
		}
		if(cmpstr.equals(str2))
		{
			ret = true;
		}
		return ret;
		
	}
	
	public static void sendTextMessage(Context context,String num,String con,PendingIntent sentIntent)
	{
		Util_G.debug_e(Util_G.busylog, "num:"+num+"content:"+con); 	
		SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(num, null, con, sentIntent, null);	
	}
	
	

	public static boolean PackageIsInstall(Context context,String packagename)
    {
    	 PackageManager pm = context.getPackageManager();
		 List<PackageInfo> packinfos = pm.getInstalledPackages(0);  
		 boolean ret = false;
		 
		 for (PackageInfo info : packinfos)//:com.zty.ompic2
		 {    	
			 String name = info.applicationInfo.packageName;
			 //Util_G.debug_e(Util_G.busylog, "�Ѱ�װ:"+name);  
			 if(name.equals(packagename))
			 {
				 ret = true;
				 break;
			 }
		 }
		 
		 return ret;
		 
    }  	

	public static void InstallPackage(Context context,String filepath)
    { 
		String[] commands = {null};	    	
    		    
    	commands[0] = "pm install -r "+filepath;	
    	Util_G.debug_e(Util_G.busylog, "commands[0]="+commands[0]);   	
	    Util_G.ExecCommons(commands);	  		 
	}  	
	
   public static void exaAssetsFile(Context context,String fileName,String DestPath)
   {
	   Util_G.debug_e(Util_G.busylog, "��ȡ�ļ�:"+fileName+"·����"+DestPath);  
	  try{
		   InputStream in = context.getResources().getAssets().open(fileName);
		   //Util_File.saveFile(in,DestPath+fileName,false);
		   //in.close();
		   //res = EncodingUtils.getString(buffer, "UTF-8");
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }
	
}

