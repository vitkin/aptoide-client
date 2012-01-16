package cm.aptoide.pt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CheckReposService extends Service {
	
	DbHandler db;
	Vector<String> hashids = new Vector<String>();
	Vector<String> servers= new Vector<String>();
	Vector<ServerEntry> entries = new Vector<ServerEntry>();
	private Vector<ServerNode> allServers;
	
	
	private class ServerEntry {
		public String repo;
		public boolean updates;
		public int appscount;
		
		public ServerEntry(String repo, boolean updates, int appscount) {
			this.repo = repo;
			this.updates = updates;
			this.appscount = appscount;
		}
		
	}
	
	
	DefaultHandler handler= new DefaultHandler(){
		
		private boolean t_entry = false;
		private boolean t_repo = false;
		private boolean t_hasupdates = false;
		
		private String repo;
		private boolean hasUpdates;
		private int appscount;
		private boolean t_added;
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			super.startElement(uri, localName, qName, attributes);
			
			if(qName.equalsIgnoreCase("entry")){
				t_entry=true;
				 
			}else if(qName.equalsIgnoreCase("repo")){
				t_repo=true;
			}else if(qName.equalsIgnoreCase("hasupdates")){
				t_hasupdates=true;
			}
			else if(qName.equalsIgnoreCase("added")){
				t_added=true;
			}
			
			
			
			
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
			
			
//			if(t_entry){
				
//			}else 
				if(t_repo){
				repo=new String(ch,start,length);
				
				
				
			}else if (t_hasupdates){
				hasUpdates=Boolean.parseBoolean((new String(ch,start,length)));
				if(!hasUpdates){
					appscount=0;
				}
			}else if (t_added){
				appscount=Integer.parseInt((new String(ch,start,length)));
			}
			
			
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			if(qName.equalsIgnoreCase("entry")){
				t_entry=false;
				ServerEntry entry = new ServerEntry(repo,hasUpdates,appscount);
				entries.add(entry);
			}else if(qName.equalsIgnoreCase("repo")){
				t_repo=false;
			}else if(qName.equalsIgnoreCase("hasupdates")){
				t_hasupdates=false;
			}
			else if(qName.equalsIgnoreCase("added")){
				t_added=false;
			}
			
		}

		
		
	};
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
 
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("CheckReposServer", "Started");
		try {
			db = new DbHandler(this);
			
			allServers = db.getServers();
			if(allServers!=null&&!allServers.isEmpty()){
			for(ServerNode server : allServers){
				if (server.inuse) {
					servers.add(server.uri);
					hashids.add(db.getServerDelta(server.uri));
				}
			}
			String url_servers="";
			String url_hashids="";
			if(!hashids.isEmpty()){
			for (int i=0; i!=servers.size();i++){
				
				if(i==0){
					url_servers=servers.get(i).split("http://")[1].split(".bazaarandroid.com/")[0];
					url_hashids=hashids.get(i);
				}else{
					
					url_servers+=","+servers.get(i).split("http://")[1].split(".bazaarandroid.com/")[0];
					url_hashids+=","+hashids.get(i);
				}
			}
			String url = "https://www.bazaarandroid.com/webservices/listRepositoryChange/"+url_servers+"/"+url_hashids+"/"+"xml";
			Log.d("",url);
			BufferedInputStream bstream = new BufferedInputStream(NetworkApis.getInputStream(this, url));
			SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError
			SAXParser sp = spf.newSAXParser();
			sp.parse(new InputSource(bstream), handler);
			
			int totalapps =0;
			boolean updates =false; 
			for(ServerEntry entry : entries){
				if(entry.updates){
					totalapps+=entry.appscount;
					updates = true;
				}
			}
			
			if(updates){
				Intent i = new Intent("pt.caixamagica.aptoide.HAS_UPDATES");
				i.putExtra("appscount", totalapps);
				sendBroadcast(i);
			}
			}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		
		stopSelf();
	}
	
	

}
