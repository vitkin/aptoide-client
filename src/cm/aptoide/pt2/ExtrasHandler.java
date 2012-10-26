package cm.aptoide.pt2;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;
import android.text.Html;

public class ExtrasHandler extends DefaultHandler {

	Context context;
	
	public ExtrasHandler(Context context) {
		this.context = context;
	}
	
	private enum Enum {
		APKID,CMT,DELTA,PKG, EXTRAS
	}
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
			default:
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
					context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
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
				context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
				values.clear();
			}
			
			System.out.println("Extras ended.");
		};
	
}