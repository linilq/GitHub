package zty.sdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlStorage extends DefaultHandler {
	List<KeyValueData> m_list;

	KeyValueData m_KeyValueData;
	String curElement;
	String m_FilePath;

	public XmlStorage(String FilePath) {
		m_list = new ArrayList<KeyValueData>();
		m_FilePath = FilePath;

		ByteArrayOutputStream bo;

		bo = Util_G.getFileOutputStream(m_FilePath);
		if ((bo != null) && (bo.size() > 0)) {
			InputStream is = new ByteArrayInputStream(bo.toByteArray());
			parsexml(is);
		}

	}

	String getValue(String key) {
		String ret = null;

		for (KeyValueData data : m_list) {
			if (data.m_Key.equals(key)) {
				ret = data.m_Value;
			}
		}
		return ret;
	}

	KeyValueData getKeyValue(String key) {
		KeyValueData ret = null;

		for (KeyValueData data : m_list) {
			if (data.m_Key.equals(key)) {
				ret = data;
			}
		}
		return ret;
	}

	public int getInt(String key, int defValue) {
		int ret = defValue;

		String Value = getValue(key);
		if (Value != null) {
			ret = Integer.valueOf(Value);
		}
		return ret;
	}

	public void putInt(String key, int Value) {

		KeyValueData data = getKeyValue(key);
		if (data != null) {
			data.m_Value = String.valueOf(Value);
		} else {
			data = new KeyValueData(key, String.valueOf(Value));
			m_list.add(data);
		}
	}

	public String getString(String key, String defValue) {
		String ret = getValue(key);
		if (ret == null) {
			ret = defValue;
		}
		return ret;
	}

	public void putString(String key, String Value) {

		KeyValueData data = getKeyValue(key);
		if (data != null) {
			data.m_Value = Value;
		} else {
			data = new KeyValueData(key, Value);
			m_list.add(data);
		}

	}

	public String GenxmlString() {
		XmlSerializer xmlSerializer = Xml.newSerializer();
		StringWriter stringWriter = new StringWriter();

		try {

			xmlSerializer.setOutput(stringWriter);
			xmlSerializer.startDocument("UTF-8", Boolean.valueOf(true));
			xmlUtil.WritexmlTagHead(xmlSerializer, stringWriter, "map", 0);
			for (KeyValueData data : m_list) {
				if (data.m_Value != null) {
					xmlUtil.WritexmlTag(xmlSerializer, stringWriter,
							data.m_Key, data.m_Value, 1);
				}
			}
			xmlUtil.WritexmlTagEnd(xmlSerializer, stringWriter, "map", 0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	public void commit() {
		String str = GenxmlString();
		if (str.length() > 0) {
			InputStream inStream = new ByteArrayInputStream(str.getBytes());
			Util_G.saveFile(inStream, m_FilePath, false);
		}
	}

	public void endDocument() {
	}

	public void startDocument() {
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		curElement = null;
		if (m_KeyValueData != null) {
			m_list.add(m_KeyValueData);
			m_KeyValueData = null;
		}
	}

	public final void characters(char[] ch, int start, int length)
			throws SAXException {
		if (curElement != null) {
			String data = new String(ch, start, length);
			m_KeyValueData.m_Value = data;
		}

	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (!localName.equals("map")) {
			curElement = localName;
			m_KeyValueData = new KeyValueData(curElement, null);
		}
	}

	public final void parsexml(InputStream inStream) {

		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(inStream, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
