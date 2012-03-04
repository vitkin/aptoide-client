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
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.display.ViewDisplayListRepos;
import cm.aptoide.pt.data.display.ViewDisplayListsDimensions;
import cm.aptoide.pt.data.listeners.ViewMyapp;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.EnumNotificationTypes;
import cm.aptoide.pt.data.notifications.ViewNotification;
import cm.aptoide.pt.data.util.Constants;


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
	
	public synchronized ViewXmlParse getNewViewRepoXmlParse(ViewRepository repository, ViewCache cache, ViewNotification notification){
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
	
	
	
	public void latestVersionInfoParse(ViewCache cache){	//TODO use notification 
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_UPDATE
									, serviceData.getString(R.string.self_update), R.string.self_update);
		DefaultHandler latestVersionInfoParser = null;
	    try {
	    	XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	    	latestVersionInfoParser = new LatestVersionInfoParser(this, cache);
	    	
	    	xmlReader.setContentHandler(latestVersionInfoParser);
	    	xmlReader.setErrorHandler(latestVersionInfoParser);
	    	
	    	InputSource inputSource = new InputSource(new FileReader(cache.getFile()));
	    	Log.d("Aptoide-managerXml", cache.getLocalPath());
	    	xmlReader.parse(inputSource);
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    }		
	}
	
	public void parsingLatestVersionInfoFinished(ViewLatestVersionInfo latestVersionInfo){
		serviceData.parsingLatestVersionInfoFinished(latestVersionInfo);
	}
	

	public void repoParse(ViewRepository repository, ViewCache cache, EnumInfoType infoType){
		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
		
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_UPDATE, repoName, repository.getHashid());
		ViewXmlParse parseInfo = getNewViewRepoXmlParse(repository, cache, notification);
		DefaultHandler repoParser = null;
	    try {
	    	XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	    	switch (infoType) {
				case DELTA:
					repoParser = new RepoDeltaParser(this, parseInfo);
					break;
					
				case BARE:
					repoParser = new RepoBareParser(this, parseInfo);
					break;
					
				case ICON:
					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
					repoParser = new RepoIconParser(this, parseInfo);
					break;	
					
//				case DOWNLOAD:
//					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
//					repoParser = new RepoDownloadParser(this, parseInfo);
//					break;
				
				case STATS:
					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
					repoParser = new RepoStatsParser(this, parseInfo);
					break;
					
//				case EXTRAS:
//					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
//					repoParser = new RepoExtrasParser(this, parseInfo);
//					break;
	
				default:
					break;
			}
	    	
	    	xmlReader.setContentHandler(repoParser);
	    	xmlReader.setErrorHandler(repoParser);
	    	
	    	InputSource inputSource = new InputSource(new FileReader(new File(parseInfo.getLocalPath())));
	    	Log.d("Aptoide-managerXml", parseInfo.getLocalPath());
	    	xmlReader.parse(inputSource);
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	}


	public void repoDeltaParse(ViewRepository repository, ViewCache cache){
		if(cache != null){
			repoParse(repository, cache, EnumInfoType.DELTA);
		}
	}
	
	public void parsingRepoDeltaFinished(ViewRepository repository){
		serviceData.parsingRepoDeltaFinished(repository);
	}


	public void repoBareParse(ViewRepository repository, ViewCache cache){
		if(cache != null){
			repoParse(repository, cache, EnumInfoType.BARE);
		}
	}
	
	public void parsingRepoBareFinished(ViewRepository repository){
		serviceData.parsingRepoBareFinished(repository);
	}
	
	public void addRepoIconsInfo(ViewRepository repository){
		serviceData.addRepoIconsInfo(repository);
	}
	
	public void repoIconParse(ViewRepository repository, ViewCache cache){
		if(cache != null){
			repoParse(repository, cache, EnumInfoType.ICON);
		}
	}
	
	public void parsingRepoIconsFinished(ViewRepository repository){
		serviceData.parsingRepoIconsFinished(repository);
	}
	
//	public void repoDownloadParse(ViewRepository repository, ViewCache cache){
//		repoAppParse(repository, cache, EnumInfoType.DOWNLOAD);
//	}
	
	public void parsingRepoDownloadFinished(ViewRepository repository){
//		serviceData.parsingRepoDownloadInfoFinished(repository);
	}
	
	public void repoStatsParse(ViewRepository repository, ViewCache cache){
		if(cache != null){
			repoParse(repository, cache, EnumInfoType.STATS);
		}
	}
	
	public void parsingRepoStatsFinished(ViewRepository repository){
		serviceData.parsingRepoStatsFinished(repository);
	}
	
//	public void repoExtrasParse(ViewRepository repository, ViewCache cache){
//		repoAppParse(repository, cache, EnumInfoType.EXTRAS);
//	}
	
	public void parsingRepoExtrasFinished(ViewRepository repository){
//		serviceData.parsingRepoExtrasFinished(repository);
	}
	
	
	
	public void repoAppParse(ViewRepository repository, ViewCache cache, int appHashid, EnumInfoType infoType){
		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
		
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_APP_UPDATE, repoName, appHashid);
		ViewXmlParse parseInfo = getNewViewRepoXmlParse(repository, cache, notification);
		DefaultHandler repoParser = null;
	    try {
	    	XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	    	switch (infoType) {
//				case ICON:
//					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
//					repoParser = new RepoIconParser(this, parseInfo);
//					break;	
					
				case DOWNLOAD:
					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
					repoParser = new RepoDownloadParser(this, parseInfo, appHashid);
					break;
				
				case STATS:
					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
					repoParser = new RepoStatsParser(this, parseInfo, appHashid);
					break;
					
				case EXTRAS:
					notification.setProgressCompletionTarget(parseInfo.getRepository().getSize());
					repoParser = new RepoExtrasParser(this, parseInfo, appHashid);
					break;
	
				default:
					break;
			}
	    	
	    	xmlReader.setContentHandler(repoParser);
	    	xmlReader.setErrorHandler(repoParser);
	    	
	    	InputSource inputSource = new InputSource(new FileReader(new File(parseInfo.getLocalPath())));
	    	Log.d("Aptoide-managerXml", parseInfo.getLocalPath());
	    	xmlReader.parse(inputSource);
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public void repoAppDownloadParse(ViewRepository repository, ViewCache cache, int appHashid){
		repoAppParse(repository, cache, appHashid, EnumInfoType.DOWNLOAD);
	}
	
	public void parsingRepoAppDownloadFinished(ViewRepository repository, int appHashid){
		serviceData.parsingRepoAppDownloadInfoFinished(repository, appHashid);
	}
	
	public void repoAppStatsParse(ViewRepository repository, ViewCache cache, int appHashid){
		repoAppParse(repository, cache, appHashid, EnumInfoType.STATS);
	}
	
	public void parsingRepoAppStatsFinished(ViewRepository repository, int appHashid){
		serviceData.parsingRepoAppStatsFinished(repository, appHashid);
	}
	
	public void repoAppExtrasParse(ViewRepository repository, ViewCache cache, int appHashid){
		repoAppParse(repository, cache, appHashid, EnumInfoType.EXTRAS);
	}
	
	public void parsingRepoAppExtrasFinished(ViewRepository repository, int appHashid){
		serviceData.parsingRepoAppExtrasFinished(repository, appHashid);
	}
	
	public void myappParse(ViewCache cache, String myappName){	//TODO use notification 
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.PARSE_MYAPP, myappName, myappName.hashCode());
		DefaultHandler myappParser = null;
	    try {
	    	XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	    	myappParser = new MyappParser(this, cache);
	    	
	    	xmlReader.setContentHandler(myappParser);
	    	xmlReader.setErrorHandler(myappParser);
	    	
	    	InputSource inputSource = new InputSource(new FileReader(cache.getFile()));
	    	Log.d("Aptoide-managerXml", cache.getLocalPath());
	    	xmlReader.parse(inputSource);
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    }		
	}
	
	public void parsingMyappFinished(ViewMyapp viewMyapp, ViewDisplayListRepos listRepos){
		serviceData.parsingMyappFinished(viewMyapp, listRepos);
	}
	
	public ViewDisplayListsDimensions getDisplayListsDimensions(){
		return serviceData.getDisplayListsDimensions();
	}
	
}
