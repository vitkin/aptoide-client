/**
 * ParserComments, 	auxiliary class to Aptoide's ServiceData
 * Copyright (C) 2011 Duarte Silveira
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of previous Aptoide's RssHandler with
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cm.aptoide.pt.data.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import cm.aptoide.pt.data.display.ViewDisplayComment;
import cm.aptoide.pt.data.display.ViewDisplayListComments;
import cm.aptoide.pt.data.model.ViewAppDownloadInfo;
import cm.aptoide.pt.data.model.ViewIconInfo;
import cm.aptoide.pt.data.util.Constants;

/**
 * ParserComments, handles Comments xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ParserComments extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewXmlParse parseInfo;
	private int appHashid;
	private ViewDisplayComment comment;
	private ViewDisplayListComments comments = new ViewDisplayListComments();
//	private ArrayList<ArrayList<ViewAppDownloadInfo>> downloadsInfoInsertStack = new ArrayList<ArrayList<ViewAppDownloadInfo>>(2);
	
	private EnumXmlTagsComments tag = EnumXmlTagsComments.response;
	
	private int parsedAppsNumber = Constants.EMPTY_INT;
	
	private StringBuilder tagContentBuilder;
	
		
	public ParserComments(ManagerXml managerXml, ViewXmlParse parseInfo, int appHashid){
		this.managerXml = managerXml;
		this.parseInfo = parseInfo;
	}	
	
	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		super.characters(chars, start, length);
		
		tagContentBuilder.append(new String(chars, start, length).trim());
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		tag = EnumXmlTagsComments.safeValueOf(localName.trim());
		
		switch (tag) {
//			case status:
//				if(!tagContentBuilder.toString().equals("OK")){
//				}
//				break;
			case errors:
				String error = tagContentBuilder.toString();
				managerXml.serviceData.parsingRepoAppCommentsError(parseInfo.getRepository().getHashid(), appHashid, error);	
				throw new SAXException(error);
		
			case id:
				comment = new ViewDisplayComment(Long.parseLong(tagContentBuilder.toString()));
				break;
				
			case useridhash:
				comment.setUserHashid(tagContentBuilder.toString());
				break;
				
			case username:
				comment.setUserName(tagContentBuilder.toString());
				break;
				
			case answerto:
				comment.setAnswerTo(Long.parseLong(tagContentBuilder.toString()));
				break;
				
			case subject:
				comment.setSubject(tagContentBuilder.toString());
				break;
				
			case text:
				comment.setBody(tagContentBuilder.toString());
				break;
				
			case timestamp:
				comment.setTimestamp(tagContentBuilder.toString());
				break;
				
			case lang:
				comment.setLanguage(tagContentBuilder.toString());
				break;
				
				
			case entry:
//				if(parsedAppsNumber >= Constants.APPLICATIONS_IN_EACH_INSERT){
//					parsedAppsNumber = 0;
//					downloadsInfoInsertStack.add(downloadsInfo);
//
//					Log.d("Aptoide-ParserComments", "bucket full, inserting download infos: "+comments.size());
//					try{
//						new Thread(){
//							public void run(){
//								this.setPriority(Thread.NORM_PRIORITY);
//								final ArrayList<ViewAppDownloadInfo> downloadsInfoInserting = downloadsInfoInsertStack.remove(Constants.FIRST_ELEMENT);
//								
//								managerXml.getManagerDatabase().insertDownloadsInfo(downloadsInfoInserting);
//							}
//						}.start();
//		
//					} catch(Exception e){
//						/** this should never happen */
//						//TODO handle exception
//						e.printStackTrace();
//					}
//					
//					comments = new ArrayList<ViewDisplayComment>(Constants.APPLICATIONS_IN_EACH_INSERT);
//				}
//				parsedAppsNumber++;
				parseInfo.getNotification().incrementProgress(1);
				
				comments.add(comment);
				break;
				
			default:
				break;
		}
		
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		tagContentBuilder = new StringBuilder();
		
	}
	
	
	
	
	@Override
	public void startDocument() throws SAXException {	//TODO refacto Logs
		Log.d("Aptoide-ParserComments","Started parsing XML ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide-ParserComments","Done parsing XML ...");
		
//		if(!comments.isEmpty()){
//			Log.d("Aptoide-ParserComments", "bucket not empty, apps: "+downloadsInfo.size());
//			downloadsInfoInsertStack.add(downloadsInfo);
//		}

		Log.d("Aptoide-ParserComments", "comments: "+comments.size());
		managerXml.serviceData.parsingRepoAppCommentsFinished(parseInfo.getRepository().getHashid(), appHashid, comments);			
		
		super.endDocument();
	}


}
