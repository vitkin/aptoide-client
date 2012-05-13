package cm.aptoide.pt;

import java.io.File;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class RepoParser extends DefaultHandler {
	DBHandler dbhandler;
	ProgressDialog updateProgress;
	Context context;
	StringBuilder sb = new StringBuilder();
	Apk apk;
	String name;
	Handler handler;
	private long repo_id = 0;
	private boolean delta = false;
	private int apks = 0;
	private int total = 0;
	private int increment = 0;
	private String xml_path;
	private boolean isRemove;
	
	public RepoParser(Context context, Handler handler,long repo_id, String xml_path) {
		this.context=context;
		dbhandler=new DBHandler(context);
		this.handler=handler;
		this.repo_id=repo_id;
		this.xml_path=xml_path;
	}
	
	@Override
	public void startDocument() throws SAXException {
		Intent i = new Intent("ACTION_REPO_PARSE_START");
		context.sendBroadcast(i);
		
		dbhandler.open();
		dbhandler.prepareDb();
		dbhandler.beginTransation();
		
		super.startDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		switch (EnumElements.lookup(localName.toUpperCase())) {
		case PACKAGE:
			apk = new Apk();
			apk.repo_id=repo_id;
			break;

		default:
			break;
		}
		sb.setLength(0);
		
		
		super.startElement(uri, localName, qName, attributes);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sb.append(ch,start,length);
		super.characters(ch, start, length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		switch (EnumElements.lookup(localName.toUpperCase())) {
		case APKLST:
			break;
		case DEL:
			isRemove=true;
			break;
		case REPOSITORY:
			if(!delta){
				dbhandler.removeRepo(repo_id,false);
			}
			break;
		case DELTA:
			delta=true;
			dbhandler.setServerDelta(repo_id, sb.toString());
			break;
		case APKID:
			apk.apkid=sb.toString();
			break;
		case NAME:
			apk.name=sb.toString();
			break;
		case APPSCOUNT:
			total  = Integer.parseInt(sb.toString());
			handler.sendEmptyMessage(total);
			break;
		case PACKAGE:
			apks ++;
			if(isRemove)
				dbhandler.removeApk(apk.apkid);
			dbhandler.insertAPK(apk);
			int percent  = (int) Math.round(( (float) apks / (float) total) * 100);
			if(percent > increment){
				increment += 1;
				handler.sendEmptyMessage(-1);
			}
			
			
			break;
		case BASEPATH:
			dbhandler.insertBasepath(sb.toString(),repo_id);
			break;
		case ICONSPATH:
			dbhandler.insertIconspath(sb.toString(),repo_id);
			break;
		case SCREENSPATH:
			dbhandler.insertScreenspath(sb.toString(),repo_id);
			break;
		case WEBSERVICESPATH:
			dbhandler.insertWebservicespath(sb.toString(),repo_id);
			break;
		case APKPATH:
			dbhandler.insertAPKpath(sb.toString(),repo_id);
			break;
		case PATH:
			apk.path=sb.toString();
			break;
		case VER:
			apk.vername=sb.toString();
			break;
		case VERCODE:
			apk.vercode=Integer.parseInt(sb.toString());
			break;
		case ICON:
			apk.icon=sb.toString();
			break;
		case DATE:
			apk.date=sb.toString();
			break;
		case MD5H:
			apk.md5=sb.toString();
			break;
		case DWN:
			apk.downloads=sb.toString();
			break;
		case RAT:
			apk.stars=sb.toString();
			break;
		case CATG:
			apk.category1=sb.toString();
			break;
		case CATG2:
			apk.category2=sb.toString();
			break;
		case SZ:
			apk.size=sb.toString();
			break;
		case AGE:
			apk.age=sb.toString();
			break;
		case MINSDK:
			apk.minSdk=sb.toString();
			break;
		case MINSCREEN:
			apk.minScreenSize=sb.toString();
			break;
			
		default:
			break;
		}
		
		
		super.endElement(uri, localName, qName);
	}
	
	@Override
	public void endDocument() throws SAXException {
		
		System.out.println(apks);
		
		dbhandler.insertNApk(apks, repo_id);
		
		dbhandler.endTransation();
		dbhandler.close();
		if(!delta){
			File thisXML = new File(xml_path);
			Md5Handler hash = new Md5Handler();
			String deltahash = hash.md5Calc(thisXML);
			Log.d("Aptoide","A adicionar novo hash delta: " + repo_id + ":" + deltahash);
			dbhandler.setServerDelta(repo_id, deltahash);
		}
		
		
		Intent i = new Intent("ACTION_REPO_PARSE_STOP");
		context.sendBroadcast(i);
		super.endDocument();
	}
	
}
