package cm.aptoide.pt2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream.PutField;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cm.aptoide.pt2.services.MainService;
import cm.aptoide.pt2.util.NetworkUtils;




import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

public class ExtrasService extends Service {

	private final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private String LOCAL_PATH = SDCARD+"/.aptoide";
	private String REMOTE_EXTRAS_FILE = "/extras.xml";
	private String EXTRAS_XML_PATH = LOCAL_PATH+"/extras.xml";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private enum Enum {
		APKID,CMT,DELTA,PKG, EXTRAS
	}
	private Database dbhandler;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		File xml = new File(((ArrayList<String>) intent.getSerializableExtra("path")).get(0));
		executor.submit(new ExtrasParser(xml,getApplicationContext()));
		return START_NOT_STICKY;
	}
	
	public class ExtrasParser extends Thread{ 
		File xml;
		private Context context;
		
		public ExtrasParser(File xml, Context context) {
			this.xml=xml;
			this.context=context;
		}

		public void run(){
			try{
				setPriority(MIN_PRIORITY);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(xml, new ExtrasHandler(context));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				xml.delete();
			}

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
				cmt=Html.fromHtml(sb.toString().replace("\n","<br>")).toString();
				break;
			case DELTA:

				break;
			case PKG:
				value.put(ExtrasDbOpenHelper.COLUMN_COMMENTS_APKID, apkid);
				value.put(ExtrasDbOpenHelper.COLUMN_COMMENTS_COMMENT, cmt);
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
	
	private boolean downloadExtras(String srv, String delta_hash){
		String url = srv+REMOTE_EXTRAS_FILE;
		
		

        try {
        	
        	ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

    		android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    		android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    		System.out.println(wifi.getState() + " " + mobile.getState());
			while (wifi.getState()!=NetworkInfo.State.CONNECTED&&mobile.getState()!=NetworkInfo.State.CONNECTED) {
				System.out.println("Sleeping 10 sec" + wifi.getState() + " " + mobile.getState());
				Thread.sleep(10000);
			}
        	
        	//String delta_hash = db.getServerDelta(srv);
        	if(delta_hash!=null&&delta_hash.length()>2)
        		url = url.concat("?hash="+delta_hash);
        	
        	Log.d("Aptoide","A fazer fetch extras de: " + url);

        	
        	FileOutputStream saveit = new FileOutputStream(LOCAL_PATH+REMOTE_EXTRAS_FILE);

				BufferedInputStream instream = NetworkUtils.getInputStream(new URL(url), null, null, getApplicationContext());
				int nRead;
				byte[] data = new byte[1024];

				while ((nRead = instream.read(data, 0, data.length)) != -1) {
					saveit.write(data, 0, nRead);
				}
				 
			return true;
		} catch (UnknownHostException e){ 
			e.printStackTrace();
			return false;} 
		  catch (ClientProtocolException e) { e.printStackTrace();return false;} 
		  catch (IOException e) { e.printStackTrace();return false;}
		  catch (Exception e) {e.printStackTrace();return false;	}
	}

}
