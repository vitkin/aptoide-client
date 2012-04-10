/**
 * ParserDOMSmallRequests, 	auxiliary class to Aptoide's ServiceData
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.PreparedStatement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
import cm.aptoide.pt.data.model.ViewAppDownloadInfo;
import cm.aptoide.pt.data.webservices.EnumServerAddAppVersionCommentStatus;
import cm.aptoide.pt.data.webservices.EnumServerAddAppVersionLikeStatus;
import cm.aptoide.pt.data.webservices.EnumServerStatus;
import cm.aptoide.pt.data.webservices.ViewDownload;


/**
 * ParserDOMSmallRequests, handles small requests xml DOM parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ParserDOMSmallRequests{
	private ManagerXml managerXml = null;
	
		
	public ParserDOMSmallRequests(ManagerXml managerXml){
		this.managerXml = managerXml;
	}
	
	public EnumServerStatus parseServerLoginReturn(HttpURLConnection connection) throws ParserConfigurationException, SAXException, IOException{
		EnumServerStatus status = EnumServerStatus.SERVICE_UNAVAILABLE;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
		DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse( connection.getInputStream() );
        dom.getDocumentElement().normalize();
        NodeList receivedStatusList = dom.getElementsByTagName(EnumXmlTagsServerLogin.status.toString());
        if(receivedStatusList.getLength()>0){
        	Node receivedStatus = receivedStatusList.item(0);
        	Log.d("Aptoide-ManagerUploads login", receivedStatus.getNodeName());
        	Log.d("Aptoide-ManagerUploads login", receivedStatus.getFirstChild().getNodeValue().trim());
        	if(receivedStatus.getFirstChild().getNodeValue().trim().equals("OK")){
        		status = EnumServerStatus.SUCCESS;
        	}else{
        		NodeList receivedErrorsList = dom.getElementsByTagName(EnumXmlTagsServerLogin.entry.toString());
    	        if(receivedErrorsList.getLength()>0){
    	        	Node receivedErrors = receivedErrorsList.item(0);
    	        	String error = receivedErrors.getFirstChild().getNodeValue();
    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getNodeName());
    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getFirstChild().getNodeValue().trim());
    	        	if(error.equals("Missing authentication parameter(s): user and/or passhash")){
    	        		status = EnumServerStatus.MISSING_PARAMETER;
    	        	}else if(error.equals("Invalid login credentials")){
    	        		status = EnumServerStatus.BAD_LOGIN;
    	        	}
    	        }
        	}
        }
        if(status.equals(EnumServerStatus.SUCCESS)){
        	String token = null;
        	NodeList receivedTokenList = dom.getElementsByTagName(EnumXmlTagsServerLogin.token.toString());
	        if(receivedTokenList.getLength()>0){
	        	Node receivedToken = receivedTokenList.item(0);
	        	token = receivedToken.getFirstChild().getNodeValue().trim();
	        	Log.d("Aptoide-ManagerUploads login", receivedToken.getNodeName());
	        	Log.d("Aptoide-ManagerUploads login", receivedToken.getFirstChild().getNodeValue().trim());
	        }
	        managerXml.serviceData.getManagerPreferences().setServerToken(token);
        }
    return status;
	}
	
	public EnumServerAddAppVersionLikeStatus parseAddAppVersionLikeReturn(HttpURLConnection connection) throws ParserConfigurationException, SAXException, IOException{
		EnumServerAddAppVersionLikeStatus status = EnumServerAddAppVersionLikeStatus.SERVICE_UNAVAILABLE;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
		DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse( connection.getInputStream() );
        dom.getDocumentElement().normalize();
        NodeList receivedStatusList = dom.getElementsByTagName(EnumXmlTagsServerLogin.status.toString());
        if(receivedStatusList.getLength()>0){
        	Node receivedStatus = receivedStatusList.item(0);
        	Log.d("Aptoide-ManagerUploads addLike", receivedStatus.getNodeName());
        	Log.d("Aptoide-ManagerUploads addLike", receivedStatus.getFirstChild().getNodeValue().trim());
        	if(receivedStatus.getFirstChild().getNodeValue().trim().equals("OK")){
        		status = EnumServerAddAppVersionLikeStatus.SUCCESS;
        	}else{
        		NodeList receivedErrorsList = dom.getElementsByTagName(EnumXmlTagsServerLogin.entry.toString());
    	        if(receivedErrorsList.getLength()>0){
    	        	Node receivedErrors = receivedErrorsList.item(0);
    	        	String error = receivedErrors.getFirstChild().getNodeValue();
    	        	Log.d("Aptoide-ManagerUploads addLike", receivedErrors.getNodeName());
    	        	Log.d("Aptoide-ManagerUploads addLike", receivedErrors.getFirstChild().getNodeValue().trim());
    	        	if(error.equals("Missing authentication parameter(s): token or user&passhash")
    	        		|| error.equals("Missing repo parameter")
    	        		|| error.equals("Missing apkid parameter")
    	        		|| error.equals("Missing apkversion parameter")
    	        		|| error.equals("Missing like parameter")){
    	        		status = EnumServerAddAppVersionLikeStatus.MISSING_PARAMETER;
    	        	}else if(error.equals("Unknown token")
    	        		|| error.equals("Invalid login credentials")){
    	        		status = EnumServerAddAppVersionLikeStatus.BAD_TOKEN;
    	        	}else if(error.equals("Invalid repo!")){
    	        		status = EnumServerAddAppVersionLikeStatus.BAD_REPO;
    	        	}else if(error.equals("No apk was found with the given apphashid.")
    	        		|| error.equals("No apk was found with the given apkid and apkversion.")){
    	        		status = EnumServerAddAppVersionLikeStatus.BAD_APP_HASHID;
    	        	}
    	        }
        	}
        }
        return status;
	}
	
	public EnumServerAddAppVersionCommentStatus parseAddAppVersionCommentReturn(HttpURLConnection connection) throws ParserConfigurationException, SAXException, IOException{
		EnumServerAddAppVersionCommentStatus status = EnumServerAddAppVersionCommentStatus.SERVICE_UNAVAILABLE;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
		DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse( connection.getInputStream() );
        dom.getDocumentElement().normalize();
        NodeList receivedStatusList = dom.getElementsByTagName(EnumXmlTagsServerLogin.status.toString());
        if(receivedStatusList.getLength()>0){
        	Node receivedStatus = receivedStatusList.item(0);
        	Log.d("Aptoide-ManagerUploads addComment", receivedStatus.getNodeName());
        	Log.d("Aptoide-ManagerUploads addComment", receivedStatus.getFirstChild().getNodeValue().trim());
        	if(receivedStatus.getFirstChild().getNodeValue().trim().equals("OK")){
        		status = EnumServerAddAppVersionCommentStatus.SUCCESS;
        	}else{
        		NodeList receivedErrorsList = dom.getElementsByTagName(EnumXmlTagsServerLogin.entry.toString());
    	        if(receivedErrorsList.getLength()>0){
    	        	Node receivedErrors = receivedErrorsList.item(0);
    	        	String error = receivedErrors.getFirstChild().getNodeValue();
    	        	Log.d("Aptoide-ManagerUploads addComment", receivedErrors.getNodeName());
    	        	Log.d("Aptoide-ManagerUploads addComment", receivedErrors.getFirstChild().getNodeValue().trim());
    	        	if(error.equals("Missing authentication parameter(s): token or user&passhash")
    	        		|| error.equals("Missing repo parameter")
    	        		|| error.equals("Missing apkid parameter")
    	        		|| error.equals("Missing apkversion parameter")
    	        		|| error.equals("Missing like parameter")){
    	        		status = EnumServerAddAppVersionCommentStatus.MISSING_PARAMETER;
    	        	}else if(error.equals("Unknown token")
    	        		|| error.equals("Invalid login credentials")){
    	        		status = EnumServerAddAppVersionCommentStatus.BAD_TOKEN;
    	        	}else if(error.equals("Invalid repo!")){
    	        		status = EnumServerAddAppVersionCommentStatus.BAD_REPO;
    	        	}else if(error.equals("No apk was found with the given apphashid.")
    	        		|| error.equals("No apk was found with the given apkid and apkversion.")){
    	        		status = EnumServerAddAppVersionCommentStatus.BAD_APP_HASHID;
    	        	}
    	        }
        	}
        }
		return status;
	}
	
	public ViewAppDownloadInfo parseRepoAppDownloadXml(HttpURLConnection connection, int repoHashid){
		ViewAppDownloadInfo downloadInfo = null;
		int appHashid = 0;
		int appFullHashid = 0;
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
			DocumentBuilder builder = factory.newDocumentBuilder();
	        Document dom = builder.parse( connection.getInputStream() );
	        dom.getDocumentElement().normalize();
	        NodeList pkgList = dom.getElementsByTagName(EnumXmlTagsDownload.pkg.toString());
	        if(pkgList.getLength()>0){
	        	NodeList pkgNodes = pkgList.item(0).getChildNodes();
	        	
	        	for (int i=0; i<pkgNodes.getLength(); i++) {
					Node node = pkgNodes.item(i);
					EnumXmlTagsDownload tag = EnumXmlTagsDownload.safeValueOf(node.getNodeName());
					
					switch (tag) {
						case apphashid:
							appHashid = Integer.parseInt(node.getNodeValue());
							appFullHashid = (appHashid+"|"+repoHashid).hashCode();
							break;
							
						case path:
							String appRemotePathTail = node.getNodeValue();
							downloadInfo = new ViewAppDownloadInfo(appRemotePathTail, appFullHashid);
							downloadInfo.setAppHashid(appHashid);
							break;
							
						case md5h:
							downloadInfo.setMd5hash(node.getNodeValue());
							break;
							
						case sz:
							downloadInfo.setSize(Integer.parseInt(node.getNodeValue()));
							if(downloadInfo.getSize()==0){	//TODO complete this hack with a flag <1KB
								downloadInfo.setSize(1);
							}
							break;
							
						default:
							break;
					}
				}
				
				managerXml.getManagerDatabase().insertDownloadInfo(downloadInfo);
			
				return downloadInfo;
	        	
	        }else{
	        	return null;
	        }
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	

}
