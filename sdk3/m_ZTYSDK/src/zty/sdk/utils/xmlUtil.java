package zty.sdk.utils;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

public class xmlUtil {

	public static void writeSpace(StringWriter Writer, int num)
	{
	    for (int i = 0; i < num; i++)
	    	Writer.write("  ");
	}
	
	public static void WritexmlTag(XmlSerializer xmlSerializer,StringWriter stringWriter,String tag,String value,int space)
	  {
		 try { 
			  xmlSerializer.flush();
			  stringWriter.write("\n");
			  writeSpace(stringWriter, space);
			  xmlSerializer.startTag("", tag);
			  if((value != null))
			  xmlSerializer.text(value);
			  xmlSerializer.endTag("", tag);
			  xmlSerializer.flush();
			  
		 } catch (Exception e) {  
		       e.printStackTrace();  
		 }  
	  }

	public  static void WritexmlTagHead(XmlSerializer xmlSerializer,StringWriter stringWriter,String tag,int space)
	  {
		 try { 			  
			  xmlSerializer.flush();
			  stringWriter.write("\n");		
			  writeSpace(stringWriter, space);			  
		      xmlSerializer.startTag("", tag);
		      xmlSerializer.flush();		      		  
		 
		 } catch (Exception e) {  
		       e.printStackTrace();  
		 }  
	  }	  

	public static void WritexmlTagEnd(XmlSerializer xmlSerializer,StringWriter stringWriter,String tag,int space)
	  {
		 try { 
			  xmlSerializer.flush();
			  stringWriter.write("\n");	
			  writeSpace(stringWriter, space);			  
		      xmlSerializer.endTag("", tag);
		      xmlSerializer.flush();		      		
		 } catch (Exception e) {  
		       e.printStackTrace();  
		 }  
	  }	  	  
}
