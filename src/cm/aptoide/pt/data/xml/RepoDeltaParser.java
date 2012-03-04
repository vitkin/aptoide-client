/**
 * RepoDeltaParser, 	auxiliary class to Aptoide's ServiceData
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
import cm.aptoide.pt.data.model.ViewIconInfo;
import cm.aptoide.pt.data.model.ViewListIds;
import cm.aptoide.pt.data.preferences.EnumMinScreenSize;

/**
 * RepoDeltaParser, handles Delta Repo xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class RepoDeltaParser extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewXmlParse parseInfo;
	private ViewApplication application;
	private ArrayList<ViewApplication> newApplications = new ArrayList<ViewApplication>();
	private ViewIconInfo icon;
	private ArrayList<ViewIconInfo> newIcons = new ArrayList<ViewIconInfo>();
	private ViewListIds removedApplications = new ViewListIds();
	
	private EnumXmlTagsDelta tag = EnumXmlTagsDelta.apklst;
	
	private String packageName = "";
	String path;
	private boolean toRemove = false;
	private int repoSizeDifferential = 0;
	
	private StringBuilder tagContentBuilder;
	
		
	public RepoDeltaParser(ManagerXml managerXml, ViewXmlParse parseInfo){
		this.managerXml = managerXml;
		this.parseInfo = parseInfo;
		
//		for (EnumXmlTagsDelta tag : EnumXmlTagsDelta.values()) {
//			tagMap.put(tag.name(), tag);
//		}
	}
	
	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		super.characters(chars, start, length);
		
		tagContentBuilder.append(new String(chars, start, length).trim());
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
//		tag = tagMap.get(localName.trim());
		try {
			tag = EnumXmlTagsDelta.valueOf(localName.trim());
		} catch (Exception e) {
			tag = null;
			e.printStackTrace();
		}
		
		if(tag != null){
			switch (tag) {
				case apphashid:
					if(toRemove){
						removedApplications.addId((Integer.parseInt(tagContentBuilder.toString())+"|"+parseInfo.getRepository().getHashid()).hashCode());
					}
					break;
			
				case apkid:
					packageName = tagContentBuilder.toString();
					break;
				case vercode:
					int versionCode = Integer.parseInt(tagContentBuilder.toString());
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
					application.setTimestamp(Long.parseLong(tagContentBuilder.toString()));
					break;
				case minScreen:
					application.setMinScreen(EnumMinScreenSize.valueOf(tagContentBuilder.toString().trim()).ordinal());
					break;
				case minSdk:
					application.setMinSdk(Integer.parseInt(tagContentBuilder.toString().trim()));
					break;
//				case minGles:
//					application.setMinGles(Integer.parseInt(tagContentBuilder.toString().trim()));
//					break;
					
				case icon:
					icon = new ViewIconInfo(tagContentBuilder.toString(), application.getFullHashid());
					break;
					
				case pkg:
					parseInfo.getNotification().incrementProgress(1);
					if(toRemove){
						repoSizeDifferential--;
					}else{
						repoSizeDifferential++;
						application.setRepoHashid(parseInfo.getRepository().getHashid());
						newApplications.add(application);
						newIcons.add(icon);
					}
					toRemove = false;
					break;
				
					
				case basepath:
					path = tagContentBuilder.toString();
					if(!path.equals(parseInfo.getRepository().getBasePath())){
						parseInfo.getRepository().setBasePath(path);
					}
					break;	
				case iconspath:
					path = tagContentBuilder.toString();
					if(!path.equals(parseInfo.getRepository().getIconsPath())){
						parseInfo.getRepository().setIconsPath(tagContentBuilder.toString());
					}
					break;	
				case screenspath:
					path = tagContentBuilder.toString();
					if(!path.equals(parseInfo.getRepository().getScreensPath())){
						parseInfo.getRepository().setScreensPath(tagContentBuilder.toString());
					}
					break;	
				case appscount:
//					repoSizeDifferential = Integer.parseInt(tagContentBuilder.toString());
					parseInfo.getNotification().setProgressCompletionTarget(repoSizeDifferential);
					break;
				case delta:
					parseInfo.getRepository().setDelta(tagContentBuilder.toString());
					break;
					
				case repository:
					break;
					
				default:
					break;
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		tagContentBuilder = new StringBuilder();
		
		if(localName.trim().equals("del")){
			toRemove = true;
		}
		
	}
	
	
	
	
	@Override
	public void startDocument() throws SAXException {	//TODO refacto Logs
		Log.d("Aptoide-RepoBareParser","Started parsing XML from " + parseInfo.getRepository() + " ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		parseInfo.getRepository().setSize(parseInfo.getRepository().getSize()+repoSizeDifferential);
		Log.d("Aptoide-RepoBareParser","Done parsing XML from " + parseInfo.getRepository() + " ... size diff: "+repoSizeDifferential);
		
		managerXml.getManagerDatabase().updateRepository(parseInfo.getRepository());
		Log.d("Aptoide-RepoBareParser","inserting new apps: " + newApplications + " ...");		
		managerXml.getManagerDatabase().insertApplications(newApplications);
		Log.d("Aptoide-RepoBareParser","removing apps: " + removedApplications + " ...");	
		managerXml.getManagerDatabase().removeApplications(removedApplications);
		
		Log.d("Aptoide-RepoBareParser","inserting new apps icons: " + newIcons + " ...");	
		managerXml.getManagerDatabase().insertIconsInfo(newIcons);
		
		managerXml.parsingRepoDeltaFinished(parseInfo.getRepository());
		super.endDocument();
	}


}
