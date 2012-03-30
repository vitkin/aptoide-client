/**
 * RepoDownloadParser, 	auxiliary class to Aptoide's ServiceData
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
import cm.aptoide.pt.data.model.ViewAppDownloadInfo;
import cm.aptoide.pt.data.model.ViewIconInfo;
import cm.aptoide.pt.data.util.Constants;

/**
 * RepoDownloadParser, handles Download Repo xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class RepoDownloadParser extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewXmlParse parseInfo;
	private ViewAppDownloadInfo downloadInfo;
	private ViewIconInfo iconInfo;
	private ArrayList<ViewAppDownloadInfo> downloadsInfo = new ArrayList<ViewAppDownloadInfo>(Constants.APPLICATIONS_IN_EACH_INSERT);
	private ArrayList<ArrayList<ViewAppDownloadInfo>> downloadsInfoInsertStack = new ArrayList<ArrayList<ViewAppDownloadInfo>>(2);
	
	private EnumXmlTagsDownload tag = EnumXmlTagsDownload.apklst;
	
	private int appHashid = Constants.EMPTY_INT;
	private int appFullHashid = Constants.EMPTY_INT;
	private int parsedAppsNumber = Constants.EMPTY_INT;
	
	private StringBuilder tagContentBuilder;
	
		
	public RepoDownloadParser(ManagerXml managerXml, ViewXmlParse parseInfo){
		this.managerXml = managerXml;
		this.parseInfo = parseInfo;
	}	
	public RepoDownloadParser(ManagerXml managerXml, ViewXmlParse parseInfo, int appHashid){
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
		
		tag = EnumXmlTagsDownload.safeValueOf(localName.trim());
		
		switch (tag) {
			case apphashid:
				appFullHashid = (Integer.parseInt(tagContentBuilder.toString())+"|"+parseInfo.getRepository().getHashid()).hashCode();
				break;
				
			case path:
				String appRemotePathTail = tagContentBuilder.toString();
				downloadInfo = new ViewAppDownloadInfo(appRemotePathTail, appFullHashid);
				break;
				
			case md5h:
				downloadInfo.setMd5hash(tagContentBuilder.toString());
				break;
				
			case sz:
				downloadInfo.setSize(Integer.parseInt(tagContentBuilder.toString()));
				if(downloadInfo.getSize()==0){	//TODO complete this hack with a flag <1KB
					downloadInfo.setSize(1);
				}
				break;

				
			case pkg:
				if(parsedAppsNumber >= Constants.APPLICATIONS_IN_EACH_INSERT){
					parsedAppsNumber = 0;
					downloadsInfoInsertStack.add(downloadsInfo);

					Log.d("Aptoide-RepoDownloadParser", "bucket full, inserting download infos: "+downloadsInfo.size());
					try{
						new Thread(){
							public void run(){
								this.setPriority(Thread.NORM_PRIORITY);
								final ArrayList<ViewAppDownloadInfo> downloadsInfoInserting = downloadsInfoInsertStack.remove(Constants.FIRST_ELEMENT);
								
								managerXml.getManagerDatabase().insertDownloadsInfo(downloadsInfoInserting);
							}
						}.start();
		
					} catch(Exception e){
						/** this should never happen */
						//TODO handle exception
						e.printStackTrace();
					}
					
					downloadsInfo = new ArrayList<ViewAppDownloadInfo>(Constants.APPLICATIONS_IN_EACH_INSERT);
				}
				parsedAppsNumber++;
				parseInfo.getNotification().incrementProgress(1);
				
				downloadsInfo.add(downloadInfo);
				break;
				
				
			case icon:
				iconInfo = new ViewIconInfo(tagContentBuilder.toString(), appFullHashid);
				Log.d("Aptoide-RepoDownload+IconParser", "inserting icon");
				try{
					new Thread(){
						public void run(){
							this.setPriority(Thread.NORM_PRIORITY);
							final ArrayList<ViewIconInfo> iconsInfoInserting = new ArrayList<ViewIconInfo>();
							iconsInfoInserting.add(iconInfo);
							
							managerXml.getManagerDatabase().insertIconsInfo(iconsInfoInserting);
							
							managerXml.serviceData.parsingIconFromDownloadInfoFinished(iconInfo, parseInfo.getRepository());
						}
					}.start();
	
				} catch(Exception e){
					/** this should never happen */
					//TODO handle exception
					e.printStackTrace();
				}
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
		Log.d("Aptoide-RepoDownloadHandler","Started parsing XML from " + parseInfo.getRepository() + " ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide-RepoIconHandler","Done parsing XML from " + parseInfo.getRepository() + " ...");
		
		if(!downloadsInfo.isEmpty()){
			Log.d("Aptoide-RepoDownloadParser", "bucket not empty, apps: "+downloadsInfo.size());
			downloadsInfoInsertStack.add(downloadsInfo);
		}

		Log.d("Aptoide-RepoInfoParser", "buckets: "+downloadsInfoInsertStack.size());
		while(!downloadsInfoInsertStack.isEmpty()){
			managerXml.getManagerDatabase().insertDownloadsInfo(downloadsInfoInsertStack.remove(Constants.FIRST_ELEMENT));			
		}
		
		if(appHashid != Constants.EMPTY_INT){
			managerXml.parsingRepoAppDownloadFinished(parseInfo.getRepository(), appHashid);
		}else{
			managerXml.parsingRepoDownloadFinished(parseInfo.getRepository());			
		}
		super.endDocument();
	}


}
