/**
 * ManagerXml,		auxilliary class to Aptoide's ServiceData
 * Copyright (C) 2011  Duarte Silveira
 * duarte.silveira@caixamagica.pt
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

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.EnumNotificationTypes;
import cm.aptoide.pt.data.notifications.ViewNotification;


/**
 * ManagerXml, models xml parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerXml{

	AptoideServiceData serviceData;
	
	/** Ongoing */
	private HashMap<Integer, ViewXmlParse> xmlParseViews;
	
	/** Object reuse pool */
	private ArrayList<ViewXmlParse> xmlParseViewsPool;
	
	public synchronized ViewXmlParse getNewViewXmlParse(ViewRepository repository, ViewCache cache, ViewNotification notification){
		ViewXmlParse xmlParseView;
		if(xmlParseViewsPool.isEmpty()){
			xmlParseView = new ViewXmlParse(repository, cache, notification);
		}else{
			ViewXmlParse viewXmlParse = xmlParseViewsPool.remove(Constants.FIRST_ELEMENT);
			viewXmlParse.reuse(repository, cache, notification);
			xmlParseView = viewXmlParse;
		}
		xmlParseViews.put(notification.getNotificationHashid(), xmlParseView);	//TODO check for concurrency issues
		return xmlParseView;
	}
	
	
	public ManagerXml(AptoideServiceData serviceData) {
		this.serviceData = serviceData;
		
		xmlParseViews = new HashMap<Integer, ViewXmlParse>();
		xmlParseViewsPool = new ArrayList<ViewXmlParse>();
	}

	
	public ManagerDatabase getManagerDatabase(){
		return serviceData.getManagerDatabase();
	}
	
	public void parsingFinished(){
		serviceData.updateLists();
	}


	public void repoParse(ViewRepository repository, ViewCache cache){
		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
		
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPOS_UPDATE, repoName, repository.getHashid());
		ViewXmlParse parseInfo = getNewViewXmlParse(repository, cache, notification);
		
	    try {
	    	XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	    	RepoBareParser repoBareParser = new RepoBareParser(this, parseInfo);
	    	xmlReader.setContentHandler(repoBareParser);
	    	xmlReader.setErrorHandler(repoBareParser);
//	    	}else{
//	    		ExtrasRssHandler handler = new ExtrasRssHandler(this, srv);
//	    		xr.setContentHandler(handler);
//	    		xml_file = new File(EXTRAS_XML_PATH);
//	    	}
	    	
	    	InputSource inputSource = new InputSource(new FileReader(new File(parseInfo.getLocalPath())));
	    	Log.d("Aptoide-managerXml", parseInfo.getLocalPath());
	    	xmlReader.parse(inputSource);
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	}
	
}
