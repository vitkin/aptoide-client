package cm.aptoide.pt;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;
import android.text.Html;

public class TopAppsRepoParser extends DefaultHandler {
	Context context;
	private DBHandler dbhandler;
	private Apk apk;
	private StringBuilder sb = new StringBuilder();
	private Repository repository;
	private ContentValues value;
	private ContentValues[] value2 = new ContentValues[0];
	private ArrayList<ContentValues> values = new ArrayList<ContentValues>();
	
	
	public TopAppsRepoParser(Context context) {
		this.context=context;
		dbhandler=new DBHandler(context);
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		dbhandler.open();
		
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		switch (EnumElements.lookup(localName.toUpperCase())) {
		case PACKAGE:
			apk = new Apk();
			apk.repo_id=-1;
			value=new ContentValues();
			break;
		case REPOSITORY:
			repository = new Repository();
			repository.id="-1";
			break;
		default:
			break;
		}
		sb .setLength(0);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch,start,length);
	}
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		switch (EnumElements.lookup(localName.toUpperCase())) {
		case REPOSITORY:
			repository.name="apps";
			dbhandler.insertEditorChoiceRepo(repository);
			break;
		case APKID:
			apk.apkid=sb.toString();
			break;
		case NAME:
			apk.name=Html.fromHtml(sb.toString()).toString();
			break;
		case PACKAGE:
			apk.id=dbhandler.insertEditorsChoice(apk);
			dbhandler.inserFeaturedScreenshots(apk);
			context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
			break;
		case VERCODE:
			apk.vercode=Integer.valueOf(sb.toString());
			break;
		case HASH:
			if(dbhandler.verifyTopAppsHash(sb.toString())){
				throw new SAXException();
			}
			dbhandler.deleteTopApps();
			repository.hash=sb.toString();
			break;
		case VER:
			apk.vername=sb.toString();
			break;
		case CATG2:
			apk.category1="Featured";
			apk.category2=sb.toString();
			break;
		case TIMESTAMP:
			apk.date=sb.toString();
			break;
		case MINSCREEN:
//			apk.minScreenSize=sb.toString();
			break;
		case MINSDK:
			apk.minSdk=sb.toString();
			break;
		case DWN:
			apk.downloads=sb.toString();
			break;
		case RAT:
			apk.stars=sb.toString();
			break;
		case CMT:
			value.put(ExtrasDBStructure.COLUMN_COMMENTS_APKID, apk.apkid);
			value.put(ExtrasDBStructure.COLUMN_COMMENTS_COMMENT, sb.toString());
			values.add(value);
			break;
		case SCREEN:
			apk.screenshots.add(sb.toString());
			break;
		case ICON:
			apk.icon=sb.toString();
			break;
		case PATH:
			apk.path=sb.toString();
			break;
		case MD5H:
			apk.md5=sb.toString();
			break;
		case SZ:
			apk.size=sb.toString();
			break;
		case BASEPATH:
			repository.basepath=sb.toString();
			break;
		case ICONSPATH:
			repository.iconspath=sb.toString();
			break;
		case SCREENSPATH:
			repository.screenspath=sb.toString();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}
