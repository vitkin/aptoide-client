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
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.model.ViewApplication;

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
	
	private EnumXmlTags tag = EnumXmlTags.apklst;
	private HashMap<String, EnumXmlTags> tagMap = new HashMap<String, EnumXmlTags>();
	
	private String packageName = "";
	private int versionCode = 0;
	private int parsedAppsNumber = 0;
	
	private StringBuilder tagContentBuilder;
	
		
	public RepoBareParser(ManagerXml managerXml, ViewXmlParse parseInfo){
		this.managerXml = managerXml;
		this.parseInfo = parseInfo;
		
		for (EnumXmlTags tag : EnumXmlTags.values()) {
			tagMap.put(tag.name(), tag);
		}
	}
	
	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		super.characters(chars, start, length);
		
		tagContentBuilder.append(new String(chars, start, length).trim());
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		switch (tag) {
		case apkid:
			packageName = tagContentBuilder.toString();
			break;
		case vercode:
			versionCode = Integer.parseInt(tagContentBuilder.toString());
			application = new ViewApplication(packageName, versionCode, false);
			break;
		case ver:
			application.setVersionName(tagContentBuilder.toString());
			break;
		case name:
			application.setApplicationName(tagContentBuilder.toString());
			break;
		case catg2:
			application.setCategoryHashid((tagContentBuilder.toString()).hashCode());
			break;
		case timestamp:
			application.setTimestamp(Integer.parseInt(tagContentBuilder.toString()));
			break;
//		case minSdk:	//TODO filters
//			application.setVersionName(new String(chars).substring(start, start + length));
//			break;
			
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
			
		default:
			break;
		}
		
		
		if(localName.trim().equals("package")){
			application.setRepoHashid(parseInfo.getRepository().getHashid());
			if(parsedAppsNumber >= Constants.APPLICATIONS_IN_EACH_INSERT){
				parsedAppsNumber = 0;
				
				managerXml.getManagerDatabase().insertApplications(applications);
				applications = new ArrayList<ViewApplication>(Constants.APPLICATIONS_IN_EACH_INSERT);
			}
			parsedAppsNumber++;
			parseInfo.getNotification().incrementProgress(1);
			
			applications.add(application);

		}else if(localName.trim().equals("repository")){
			managerXml.getManagerDatabase().insertRepository(parseInfo.getRepository());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		tagContentBuilder = new StringBuilder();
		tag = tagMap.get(localName.trim());
	}
	
	
	
	
	@Override
	public void startDocument() throws SAXException {	//TODO refacto Logs
		Log.d("Aptoide-RepoBareHandler","Started parsing XML from " + parseInfo.getRepository() + " ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide-RepoBareHandler","Done parsing XML from " + parseInfo.getRepository() + " ...");
		if(!applications.isEmpty()){
			managerXml.getManagerDatabase().insertApplications(applications);
		}
		managerXml.parsingFinished();
		super.endDocument();
	}


}