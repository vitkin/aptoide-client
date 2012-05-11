package cm.aptoideconcept.pt;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream.PutField;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;

public class ExtrasService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private enum Enum {
		APKID,CMT,DELTA,PKG, EXTRAS
	}
	private DBHandler dbhandler;
	@Override
	public void onStart(final Intent intent, int startId) {
		
		super.onStart(intent, startId);
		
//		dbhandler = new DBHandler(getApplicationContext());
//		dbhandler.openExtras();
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser sp = factory.newSAXParser();

			new Thread(new Runnable() {
				public void run() {
					try {
						sp.parse(openHttpConnection(intent.getExtras()
								.getString("repo") + "extras.xml"), handler);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private InputStream openHttpConnection(String urlStr) {
	    InputStream in = null;
	    int resCode = -1;

	        try {


	            URL url = new URL(urlStr);
	            URLConnection urlConn = url.openConnection();

	            if (!(urlConn instanceof HttpURLConnection)) {

	                throw new IOException ("URL is not an Http URL");

	            }

	            HttpURLConnection httpConn = (HttpURLConnection)urlConn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setRequestMethod("GET");
	            httpConn.connect();
	            resCode = httpConn.getResponseCode();


	            if (resCode == HttpURLConnection.HTTP_OK) {

	                in = httpConn.getInputStream(); 

	            } 

	        } catch (MalformedURLException e) {
	        e.printStackTrace();
	        } catch (IOException e) {
	        e.printStackTrace();
	        }

	    return in;
	    }
	
	DefaultHandler handler = new DefaultHandler(){
		
		StringBuilder sb = new StringBuilder();
		String apkid;
		String cmt;
		private ContentValues value;
		private ContentValues[] value2 = new ContentValues[0];
		private ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		private int i = 0;
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			switch (Enum.valueOf(localName.toUpperCase())) {
			case PKG:
				value=new ContentValues();
				break;
			}
			sb.setLength(0);
		};
		public void startDocument() throws SAXException {
		};
		public void characters(char[] ch, int start, int length) throws SAXException {
			sb.append(ch,start,length);
		};
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch (Enum.valueOf(localName.toUpperCase())) {
			case APKID:
				apkid=sb.toString();
				break;
			case CMT:
				cmt=sb.toString();
				break;
			case DELTA:

				break;
			case PKG:
				value.put(ExtrasDBStructure.COLUMN_COMMENTS_APKID, apkid);
				value.put(ExtrasDBStructure.COLUMN_COMMENTS_COMMENT, cmt);
				values.add(value);
				i++;
				if(i%100==0){
					getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
					values.clear();
				}
				
//				getContentResolver().insert(ExtrasContentProvider.CONTENT_URI, value);
//				dbhandler.addComment(apkid,cmt);
				apkid="";
				cmt="";
				break;
			case EXTRAS:
				break;
			default:
				break;
			}
		};
		public void endDocument() throws SAXException {
			if(values.size()>0){
				getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
				values.clear();
			}
			
			System.out.println("Extras ended.");
		};
	};

}
