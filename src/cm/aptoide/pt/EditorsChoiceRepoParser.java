package cm.aptoide.pt;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;
import android.text.Html;

public class EditorsChoiceRepoParser extends DefaultHandler {
	StringBuilder sb = new StringBuilder();
	Context context;
	private DBHandler dbhandler;
	private Apk apk;
	private Repository repository;
	private ContentValues value;
	private ContentValues[] value2 = new ContentValues[0];
	private ArrayList<ContentValues> values = new ArrayList<ContentValues>();
	
	
	public EditorsChoiceRepoParser(Context context) {
		this.context=context;
		dbhandler=new DBHandler(context);
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		dbhandler.open();
		dbhandler.deleteEditorsChoice();
//		dbhandler.beginTransation();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		switch (EnumElements.lookup(localName.toUpperCase())) {
		case PACKAGE:
			apk = new Apk();
			value=new ContentValues();
//			apk.repo_id=repo_id;
			break;
		case REPOSITORY:
			repository = new Repository();
			break;
		default:
			break;
		}
		sb.setLength(0);
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch,start,length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		switch (EnumElements.lookup(localName.toUpperCase())) {
		case REPOSITORY:
			System.out.println(repository.name+"repo");
			apk.repo_id=dbhandler.insertEditorChoiceRepo(repository);
			apk.id=dbhandler.insertEditorsChoice(apk);
			dbhandler.inserFeaturedScreenshots(apk);
			context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
			break;
		case APKID:
			apk.apkid=sb.toString();
			break;
		case NAME:
			if(repository.name!=null){
				apk.name=Html.fromHtml(sb.toString()).toString();
			}else{
				repository.name=sb.toString();
			}
			
			break;
		case HIGHLIGHT:
			apk.highlighted="yes";
			break;
		case VERCODE:
			apk.vercode=Integer.valueOf(sb.toString());
			break;
		case VER:
			apk.vername=sb.toString();
			break;
		case CATG2:
			apk.category2=sb.toString();
			break;
		case TIMESTAMP:
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
		case FEATUREGRAPHIC:
			apk.featuregraphic=sb.toString();
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
		case FEATUREGRAPHICPATH:
			repository.featuregraphicpath=sb.toString();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
//		dbhandler.endTransation();
	}
	
	
}
