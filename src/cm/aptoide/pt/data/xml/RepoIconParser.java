/**
 * RepoIconParser, 	auxiliary class to Aptoide's ServiceData
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
import cm.aptoide.pt.data.model.ViewIconInfo;

/**
 * RepoIconParser, handles Icon Repo xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class RepoIconParser extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewXmlParse parseInfo;
	private ViewIconInfo iconInfo;	
	private ArrayList<ViewApplication> applications = new ArrayList<ViewApplication>(Constants.APPLICATIONS_IN_EACH_INSERT);
	
	private EnumXmlTagsIcon tag = EnumXmlTagsIcon.apklst;
	private HashMap<String, EnumXmlTagsIcon> tagMap = new HashMap<String, EnumXmlTagsIcon>();
	
	private int appHashid = 0;
	private int parsedAppsNumber = 0;
	
	private StringBuilder tagContentBuilder;
	
		
	public RepoIconParser(ManagerXml managerXml, ViewXmlParse parseInfo){
		this.managerXml = managerXml;
		this.parseInfo = parseInfo;
		
		for (EnumXmlTagsIcon tag : EnumXmlTagsIcon.values()) {
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
		case apphashid:
			appHashid = Integer.parseInt(tagContentBuilder.toString());
			break;
		case icon:
			String iconRemotePathTail = tagContentBuilder.toString();
			iconInfo = new ViewIconInfo(iconRemotePathTail, appHashid);
			break;
			
		default:
			break;
		}
		
		
//		if(localName.trim().equals("package")){
//			application.setRepoHashid(parseInfo.getRepository().getHashid());
//			if(parsedAppsNumber >= Constants.APPLICATIONS_IN_EACH_INSERT){
//				parsedAppsNumber = 0;
//				
//				managerXml.getManagerDatabase().insertApplications(applications);
//				applications = new ArrayList<ViewApplication>(Constants.APPLICATIONS_IN_EACH_INSERT);
//			}
//			parsedAppsNumber++;
//			parseInfo.getNotification().incrementProgress(1);
//			
//			applications.add(application);
//
//		}else if(localName.trim().equals("repository")){
//			managerXml.getManagerDatabase().insertRepository(parseInfo.getRepository());
//		}
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
