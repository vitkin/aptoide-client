/**
 * RepoBareParser, 	auxiliary class to Aptoide's ServiceData
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
import cm.aptoide.pt.data.model.ViewApplication;
import cm.aptoide.pt.data.preferences.EnumMinScreenSize;
import cm.aptoide.pt.data.util.Constants;

/**
 * RepoBareParser, handles Bare Repo xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class RepoBareParser extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewXmlParse parseInfo;
	private ViewApplication application;	
	private ArrayList<ViewApplication> applications = new ArrayList<ViewApplication>(Constants.APPLICATIONS_IN_EACH_INSERT);
	private ArrayList<ArrayList<ViewApplication>> applicationsInsertStack = new ArrayList<ArrayList<ViewApplication>>(2);
	
	private EnumXmlTagsBare tag = EnumXmlTagsBare.apklst;
	
	private String packageName = "";
	private int parsedAppsNumber = 0;
	private boolean firstBucket = true;
	private boolean secondBucket = true;
	
	private StringBuilder tagContentBuilder;
	
		
	public RepoBareParser(ManagerXml managerXml, ViewXmlParse parseInfo){
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
		
		try {
			tag = EnumXmlTagsBare.valueOf(localName.trim());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		switch (tag) {
			case apkid:
				packageName = tagContentBuilder.toString();
				break;
			case vercode:
				int versionCode = Integer.parseInt(tagContentBuilder.toString().trim());
				application = new ViewApplication(packageName, versionCode, false);
				break;
			case ver:
				application.setVersionName(tagContentBuilder.toString());
				break;
			case name:
				application.setApplicationName(tagContentBuilder.toString());
				break;
			case catg2:
				application.setCategoryHashid((tagContentBuilder.toString().trim()).hashCode());
//				Log.d("Aptoide-RepoBareParser", "app: "+application.getApplicationName()+", appHashid (Not full): "+application.getHashid()+", category: "+tagContentBuilder.toString().trim()+", categoryHashid: "+application.getCategoryHashid());
				break;
			case timestamp:
				application.setTimestamp(Long.parseLong(tagContentBuilder.toString().trim()));
				break;
			case minScreen:
				application.setMinScreen(EnumMinScreenSize.valueOf(tagContentBuilder.toString().trim()).ordinal());
				break;
			case minSdk:
				application.setMinSdk(Integer.parseInt(tagContentBuilder.toString().trim()));
				break;
			case minGles:
				application.setMinGles(Float.parseFloat(tagContentBuilder.toString().trim()));
				break;
				
			case pkg:
				application.setRepoHashid(parseInfo.getRepository().getHashid());
				
				if((firstBucket && parsedAppsNumber >= managerXml.getDisplayListsDimensions().getFastReset()) 
						|| (secondBucket && parsedAppsNumber >= (managerXml.getDisplayListsDimensions().getCacheSize()-managerXml.getDisplayListsDimensions().getFastReset())) 
						|| parsedAppsNumber >= Constants.APPLICATIONS_IN_EACH_INSERT){
					final boolean insertingFirstBucket;
					if(firstBucket){
						firstBucket = false;
						insertingFirstBucket = true;
						Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
						Log.d("Aptoide-RepoBareParser", "initial bucket full, inserting apps: "+applications.size());
					}else if(secondBucket){
						secondBucket = false;
						insertingFirstBucket = false;
//						Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
						Log.d("Aptoide-RepoBareParser", "second bucket full, inserting apps: "+applications.size());
					}else{
						insertingFirstBucket = false;
						Log.d("Aptoide-RepoBareParser", "bucket full, inserting apps: "+applications.size());
					}
					parsedAppsNumber = 0;
					applicationsInsertStack.add(applications);
			
					try{
						new Thread(){
							public void run(){
								this.setPriority(Thread.MIN_PRIORITY);
								
								final ArrayList<ViewApplication> applicationsInserting = applicationsInsertStack.remove(Constants.FIRST_ELEMENT);

								managerXml.getManagerDatabase().insertApplications(applicationsInserting);
								if(insertingFirstBucket && !managerXml.serviceData.getManagerPreferences().getShowApplicationsByCategory()){
									managerXml.serviceData.resetAvailableLists();
								}
							}
						}.start();
			
					} catch(Exception e){
						/** this should never happen */
						//TODO handle exception
						e.printStackTrace();
					}
					
					applications = new ArrayList<ViewApplication>(Constants.APPLICATIONS_IN_EACH_INSERT);
				}
				parsedAppsNumber++;
				parseInfo.getNotification().incrementProgress(1);
				
				applications.add(application);
				break;
			
				
			case basepath:
				parseInfo.getRepository().setBasePath(tagContentBuilder.toString());
				break;	
			case iconspath:
				parseInfo.getRepository().setIconsPath(tagContentBuilder.toString());
				break;	
			case screenspath:
				parseInfo.getRepository().setScreensPath(tagContentBuilder.toString());
				break;	
			case appscount:
				parseInfo.getRepository().setSize(Integer.parseInt(tagContentBuilder.toString()));		
				parseInfo.getNotification().setProgressCompletionTarget(parseInfo.getRepository().getSize());
				break;
			case hash:
				parseInfo.getRepository().setDelta(tagContentBuilder.toString());
				break;
				
			case repository:
				managerXml.getManagerDatabase().insertRepository(parseInfo.getRepository());
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
		Log.d("Aptoide-RepoBareParser","Started parsing XML from " + parseInfo.getRepository() + " ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide-RepoBareParser","Done parsing XML from " + parseInfo.getRepository() + " ...");

		if(!applications.isEmpty()){
			Log.d("Aptoide-RepoBareParser", "bucket not empty, apps: "+applications.size());
			applicationsInsertStack.add(applications);
		}
		Log.d("Aptoide-RepoBareParser", "buckets: "+applicationsInsertStack.size());
		while(!applicationsInsertStack.isEmpty()){
			managerXml.getManagerDatabase().insertApplications(applicationsInsertStack.remove(Constants.FIRST_ELEMENT));
		}
		
		managerXml.parsingRepoBareFinished(parseInfo.getRepository());
		super.endDocument();
	}


}
