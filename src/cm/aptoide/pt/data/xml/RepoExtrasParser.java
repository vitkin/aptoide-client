/**
 * RepoExtrasParser, 	auxiliary class to Aptoide's ServiceData
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
import cm.aptoide.pt.data.model.ViewExtraInfo;
import cm.aptoide.pt.data.model.ViewScreenInfo;
import cm.aptoide.pt.data.util.Constants;

/**
 * RepoExtrasParser, handles Extras repo xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class RepoExtrasParser extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewXmlParse parseInfo;
	private ViewExtraInfo extraInfo;
	private ArrayList<ViewExtraInfo> extras = new ArrayList<ViewExtraInfo>(Constants.APPLICATIONS_IN_EACH_INSERT);
	private ArrayList<ArrayList<ViewExtraInfo>> extrasInsertStack = new ArrayList<ArrayList<ViewExtraInfo>>(2);
	private ViewScreenInfo screenInfo;
	private ArrayList<ViewScreenInfo> screensInfo = new ArrayList<ViewScreenInfo>(Constants.APPLICATIONS_IN_EACH_INSERT);
	private ArrayList<ArrayList<ViewScreenInfo>> screensInfoInsertStack = new ArrayList<ArrayList<ViewScreenInfo>>(2);
	
	private EnumXmlTagsExtras tag = EnumXmlTagsExtras.apklst;
	
	private int appHashid = Constants.EMPTY_INT;
	private int appFullHashid = Constants.EMPTY_INT;
	private int parsedAppsNumber = Constants.EMPTY_INT;
	
	private int screenOrderNumber = 1;
	
	private StringBuilder tagContentBuilder;
	
		
	public RepoExtrasParser(ManagerXml managerXml, ViewXmlParse parseInfo){
		this.managerXml = managerXml;
		this.parseInfo = parseInfo;
	}
	
	public RepoExtrasParser(ManagerXml managerXml, ViewXmlParse parseInfo, int appHashid){
		this(managerXml, parseInfo);
		this.appHashid = appHashid;
	}
	
	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		super.characters(chars, start, length);
		
		tagContentBuilder.append(new String(chars, start, length).trim());
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		try {
			tag = EnumXmlTagsExtras.valueOf(localName.trim());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		switch (tag) {
			case apphashid:
				appFullHashid = (Integer.parseInt(tagContentBuilder.toString())+"|"+parseInfo.getRepository().getHashid()).hashCode();
				break;
				
			case cmt:
				String description = tagContentBuilder.toString();
				extraInfo = new ViewExtraInfo( appFullHashid, description);
				break;
				
			case screen:
				String screenRemotePathTail = tagContentBuilder.toString();
				screenInfo = new ViewScreenInfo(screenRemotePathTail, screenOrderNumber, appFullHashid);
				screensInfo.add(screenInfo);
				screenOrderNumber++;
				break;
				
			case pkg:
				if(parsedAppsNumber >= Constants.APPLICATIONS_IN_EACH_INSERT){
					parsedAppsNumber = 0;
					extrasInsertStack.add(extras);

					Log.d("Aptoide-RepoExtrasParser", "bucket full, inserting extras: "+extras.size());
					try{
						new Thread(){
							public void run(){
								this.setPriority(Thread.NORM_PRIORITY);
								final ArrayList<ViewExtraInfo> extrasInfoInserting = extrasInsertStack.remove(Constants.FIRST_ELEMENT);
								
								managerXml.getManagerDatabase().insertExtras(extrasInfoInserting);
							}
						}.start();
		
					} catch(Exception e){
						/** this should never happen */
						//TODO handle exception
						e.printStackTrace();
					}
					
					extras = new ArrayList<ViewExtraInfo>(Constants.APPLICATIONS_IN_EACH_INSERT);
				}
				
				if(screensInfo.size() >= Constants.APPLICATIONS_IN_EACH_INSERT){
					screensInfoInsertStack.add(screensInfo);
					
					Log.d("Aptoide-RepoExtrasParser", "screens bucket full, inserting screens: "+screensInfo.size());
					try{
						new Thread(){
							public void run(){
								this.setPriority(Thread.NORM_PRIORITY);
								final ArrayList<ViewScreenInfo> screensInfoInserting = screensInfoInsertStack.remove(Constants.FIRST_ELEMENT);
								
								managerXml.getManagerDatabase().insertScreensInfo(screensInfoInserting);
							}
						}.start();
		
					} catch(Exception e){
						/** this should never happen */
						//TODO handle exception
						e.printStackTrace();
					}
					
					extras = new ArrayList<ViewExtraInfo>(Constants.APPLICATIONS_IN_EACH_INSERT);
				}
				
				parsedAppsNumber++;
				parseInfo.getNotification().incrementProgress(1);
				
				extras.add(extraInfo);
				
				screenOrderNumber = 1;
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
		Log.d("Aptoide-RepoExtrasHandler","Started parsing XML from " + parseInfo.getRepository() + " ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide-RepoExtrasHandler","Done parsing XML from " + parseInfo.getRepository() + " ...");
		
		if(!extras.isEmpty()){
			Log.d("Aptoide-RepoExtrasParser", "bucket not empty, apps: "+extras.size());
			extrasInsertStack.add(extras);
		}

		Log.d("Aptoide-RepoExtrasParser", "buckets: "+extrasInsertStack.size());
		while(!extrasInsertStack.isEmpty()){
			managerXml.getManagerDatabase().insertExtras(extrasInsertStack.remove(Constants.FIRST_ELEMENT));			
		}
		
		if(!screensInfo.isEmpty()){
			Log.d("Aptoide-RepoExtrasParser", "screens bucket not empty, screens: "+screensInfo.size());
			screensInfoInsertStack.add(screensInfo);
		}

		Log.d("Aptoide-RepoExtrasParser", "screens buckets: "+screensInfoInsertStack.size());
		while(!screensInfoInsertStack.isEmpty()){
			managerXml.getManagerDatabase().insertScreensInfo(screensInfoInsertStack.remove(Constants.FIRST_ELEMENT));			
		}

		if(appHashid != Constants.EMPTY_INT){
			managerXml.parsingRepoAppExtrasFinished(parseInfo.getRepository(), appHashid);
		}else{
			managerXml.parsingRepoExtrasFinished(parseInfo.getRepository());
		}
		super.endDocument();
	}


}
