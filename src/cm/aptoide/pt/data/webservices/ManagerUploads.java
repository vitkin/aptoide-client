/**
 * ManagerUploads,		auxilliary class to Aptoide's ServiceData
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


package cm.aptoide.pt.data.webservices;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.ViewClientStatistics;
import cm.aptoide.pt.data.model.ViewLogin;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.data.xml.EnumXmlTagsServerLogin;

/**
 * ManagerUploads, centralizes all upload processes
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerUploads {
	
	private AptoideServiceData serviceData;
//	private ManagerCache managerCache;
	private ConnectivityManager connectivityState;
	
	/** Waiting **/
//	private IconsDownloadManager iconsDownloadManager;
	
	/** Object reuse pool */
//	private ArrayList<ViewDownload> downloadPool;

	
//    private class IconsDownloadManager{
//    	private ExecutorService iconGettersPool;
//    	private AtomicInteger iconsDownloadedCounter;
//    	
//    	public IconsDownloadManager(){
//    		iconGettersPool = Executors.newFixedThreadPool(Constants.MAX_PARALLEL_DOWNLOADS);
//    		iconsDownloadedCounter = new AtomicInteger(0);
//    	}
//    	
//    	public void executeDownload(ViewDownload downloadInfo){
//    		iconGettersPool.execute(new GetIcon(downloadInfo));
//        }
//    	
//    	private class GetIcon implements Runnable{
//
//    		private ViewDownload iconDownload;
//    		
//			public GetIcon(ViewDownload iconDownloadInfo) {
//				this.iconDownload = iconDownloadInfo;
//			}
//
//			@Override
//			public void run() {
////				downloads.put(download.getNotification().getNotificationHashid(), download);
//				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//				download(iconDownload, false);
//				recicleViewDownload(iconDownload);
//				if(iconsDownloadedCounter.incrementAndGet() >= Constants.ICONS_REFRESH_INTERVAL){
//					iconsDownloadedCounter.set(0);
//					serviceData.refreshAvailableDisplay();
//				}
//			}
//    		
//    	}
//    }
	
	
//	public ManagerCache getManagerCache(){
//		return this.managerCache;
//	}
	
	private ViewClientStatistics getClientStatistics(){
		return serviceData.getStatistics();
	}
	
	
	public ManagerUploads(AptoideServiceData serviceData) {
		this.serviceData = serviceData;
//		managerCache = new ManagerCache();
		
		connectivityState = (ConnectivityManager)serviceData.getSystemService(Context.CONNECTIVITY_SERVICE);

//		iconsDownloadManager = new IconsDownloadManager();
		
//		this.downloadPool = new ArrayList<ViewDownload>(Constants.MAX_PARALLEL_DOWNLOADS);
		
//		Log.d("Aptoide","******* \n Downloads will be made to: " + Constants.PATH_CACHE + "\n ********");
	}
		
	

	//TODO refactor all this to reduce data redundancy and memory waste
//	public synchronized ViewDownload getNewViewDownload(String remotePath, ViewCache cache, ViewNotification notification){
//		ViewDownload download;
//		if(downloadPool.isEmpty()){
//			download = new ViewDownload(remotePath, cache, notification);
//		}else{
//			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
//			viewDownload.reuse(remotePath, cache, notification);
//			download = viewDownload;
//		}
////		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
//		return download;
//	}
//	
//	public synchronized ViewDownload getNewViewDownload(String remotePath, ViewLogin login, ViewCache cache, ViewNotification notification){
//		ViewDownload download;
//		if(downloadPool.isEmpty()){
//			download = new ViewDownload(remotePath, login, cache, notification);
//		}else{
//			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
//			viewDownload.reuse(remotePath, login, cache, notification);
//			download = viewDownload;
//		}
////		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
//		return download;
//	}
//	
//	public synchronized ViewDownload getNewViewDownload(String remotePath, int size, ViewLogin login, ViewCache cache, ViewNotification notification){
//		ViewDownload download;
//		if(downloadPool.isEmpty()){
//			download = new ViewDownload(remotePath, size, login, cache, notification);
//		}else{
//			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
//			viewDownload.reuse(remotePath, size, login, cache, notification);
//			download = viewDownload;
//		}
////		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
//		return download;
//	}
//	
//	public synchronized ViewDownload getNewViewDownload(String remotePath, int size, ViewCache cache, ViewNotification notification){
//		ViewDownload download;
//		if(downloadPool.isEmpty()){
//			download = new ViewDownload(remotePath, size, cache, notification);
//		}else{
//			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
//			viewDownload.reuse(remotePath, size, cache, notification);
//			download = viewDownload;
//		}
////		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
//		return download;
//	}
//	
//	public synchronized void recicleViewDownload(ViewDownload download){
////		serviceData.getManagerNotifications().recycleNotification(download.getNotification());
//		download.clean();
//		downloadPool.add(download);
//	}
	
	public boolean isConnectionAvailable(){
		boolean connectionAvailable = false;
		try {
			connectionAvailable = connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable mobile: "+connectionAvailable);	
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wifi: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wimax: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable ethernet: "+connectionAvailable);
		} catch (Exception e) { }
		
		return connectionAvailable;
	}
	
	public boolean isPermittedConnectionAvailable(ViewIconDownloadPermissions permissions){
		boolean connectionAvailable = false;
		if(permissions.isWiFi()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wifi: "+connectionAvailable);
			} catch (Exception e) { }
		} 
		if(permissions.isWiMax()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wimax: "+connectionAvailable);
			} catch (Exception e) { }
		} 
		if(permissions.isMobile()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable mobile: "+connectionAvailable);
			} catch (Exception e) { }
		}
		if(permissions.isEthernet()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable ethernet: "+connectionAvailable);
			} catch (Exception e) { }
		}

		Log.d("ManagerDownloads", "isPermittedConnectionAvailable: "+connectionAvailable+"  permissions: "+permissions);
		return connectionAvailable;
	}
	
	
	
	public EnumServerLoginStatus login(ViewLogin login){	//TODO receive repo to support multiple servers
		EnumServerLoginStatus status = EnumServerLoginStatus.SERVICE_UNAVAILABLE;
		String endpointString = String.format(Constants.URI_FORMAT_LOGIN_WS, URLEncoder.encode(login.getUsername()), URLEncoder.encode(login.getPassword()));

		try {
			URL endpoint = new URL(endpointString);
			HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection(); //Careful with UnknownHostException. Throws MalformedURLException, IOException
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			connection.setConnectTimeout(TIME_OUT);
//			
//			SharedPreferences sPref = mctx.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
//			String myid = sPref.getString("myId", "NoInfo");
//			String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
//			
//			connection.setRequestProperty("User-Agent", "aptoide-" + mctx.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));
			
			
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
	        		status = EnumServerLoginStatus.SUCCESS;
	        	}else{
	        		NodeList receivedErrorsList = dom.getElementsByTagName(EnumXmlTagsServerLogin.errors.toString());
	    	        if(receivedErrorsList.getLength()>0){
	    	        	Node receivedErrors = receivedErrorsList.item(0);
	    	        	String error = receivedErrors.getFirstChild().getNodeValue();
	    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getNodeName());
	    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getFirstChild().getNodeValue().trim());
	    	        	if(error.equals("Missing authentication parameter(s): user and/or passhash")){
	    	        		status = EnumServerLoginStatus.MISSING_PARAMETER;
	    	        	}else if(error.equals("Invalid login credentials")){
	    	        		status = EnumServerLoginStatus.BAD_LOGIN;
	    	        	}
	    	        }
	        	}
	        }
	        if(status.equals(EnumServerLoginStatus.SUCCESS)){
	        	String token = null;
	        	NodeList receivedTokenList = dom.getElementsByTagName(EnumXmlTagsServerLogin.token.toString());
		        if(receivedTokenList.getLength()>0){
		        	Node receivedToken = receivedTokenList.item(0);
		        	token = receivedToken.getFirstChild().getNodeValue().trim();
		        	Log.d("Aptoide-ManagerUploads login", receivedToken.getNodeName());
		        	Log.d("Aptoide-ManagerUploads login", receivedToken.getFirstChild().getNodeValue().trim());
		        }
		        serviceData.getManagerPreferences().setServerLogin(login);
	        	serviceData.getManagerPreferences().setServerToken(token);
	        }
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return status;
	}
	
	public EnumServerAddAppVersionLikeStatus addAppVersionLike(String repo, int appHashid, boolean like){	//TODO create ViewLike from args
		EnumServerAddAppVersionLikeStatus status = EnumServerAddAppVersionLikeStatus.SERVICE_UNAVAILABLE;
		String token = serviceData.getManagerPreferences().getToken();	//TODO add support for multiple servers
		String endpointString;
		if(like){
			endpointString = String.format(Constants.URI_FORMAT_ADD_LIKE_WS, URLEncoder.encode(token), URLEncoder.encode(repo), URLEncoder.encode(Integer.toString(appHashid)));
		}else{
			endpointString = String.format(Constants.URI_FORMAT_ADD_DISLIKE_WS, URLEncoder.encode(token), URLEncoder.encode(repo), URLEncoder.encode(Integer.toString(appHashid)));
		}

		try {
			URL endpoint = new URL(endpointString);
			HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection(); //Careful with UnknownHostException. Throws MalformedURLException, IOException
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			connection.setConnectTimeout(TIME_OUT);
//			
//			SharedPreferences sPref = mctx.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
//			String myid = sPref.getString("myId", "NoInfo");
//			String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
//			
//			connection.setRequestProperty("User-Agent", "aptoide-" + mctx.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));
			
			
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
	        		status = EnumServerAddAppVersionLikeStatus.SUCCESS;
	        	}else{
	        		NodeList receivedErrorsList = dom.getElementsByTagName(EnumXmlTagsServerLogin.errors.toString());
	    	        if(receivedErrorsList.getLength()>0){
	    	        	Node receivedErrors = receivedErrorsList.item(0);
	    	        	String error = receivedErrors.getFirstChild().getNodeValue();
	    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getNodeName());
	    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getFirstChild().getNodeValue().trim());
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
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return status;
	}
	
	public EnumServerAddAppVersionCommentStatus addAppVersionComment(String repo, int appHashid, String commentBody, String subject, long answerTo){	//TODO create ViewComment from args
		EnumServerAddAppVersionCommentStatus status = EnumServerAddAppVersionCommentStatus.SERVICE_UNAVAILABLE;
		String token = serviceData.getManagerPreferences().getToken();	//TODO add support for multiple servers

		try {
			URL endpoint = new URL(Constants.URI_ADD_COMMENT_POST_WS);

			HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();//Careful with UnknownHostException 

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			//		String myid = sPref.getString("myId", "NoInfo");
			//		String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
			//		connection.setRequestProperty("User-Agent", "aptoide-" + mctx.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));


			//Variable definition
			StringBuilder postArguments = new StringBuilder();
			postArguments.append(URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8"));
			postArguments.append("&"+URLEncoder.encode("repo", "UTF-8") + "=" + URLEncoder.encode(repo, "UTF-8"));
			postArguments.append("&"+URLEncoder.encode("apkid", "UTF-8") + "=" + URLEncoder.encode("apphashid", "UTF-8"));
			postArguments.append("&"+URLEncoder.encode("apkversion", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(appHashid), "UTF-8"));
			postArguments.append("&"+URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(commentBody, "UTF-8"));

			String language = serviceData.getResources().getConfiguration().locale.getLanguage()+"_"+serviceData.getResources().getConfiguration().locale.getCountry();
			Log.d("Aptoide-ManagerUploads", "addAppVersionComment, language: "+language);

			postArguments.append("&"+URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode( language, "UTF-8"));
			if(answerTo!=Constants.EMPTY_INT){
				postArguments.append("&"+URLEncoder.encode("answerto", "UTF-8") + "=" + URLEncoder.encode(Long.toString(answerTo), "UTF-8"));
			}
			if(subject!=null && subject.length()!=0){
				postArguments.append("&"+URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8"));
			}
			postArguments.append("&"+URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8"));


			connection.setDoOutput(true);
			connection.setDoInput(true);


			OutputStreamWriter output = new OutputStreamWriter(connection.getOutputStream());
			output.write(postArguments.toString());
			output.flush();



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
	        		status = EnumServerAddAppVersionCommentStatus.SUCCESS;
	        	}else{
	        		NodeList receivedErrorsList = dom.getElementsByTagName(EnumXmlTagsServerLogin.errors.toString());
	    	        if(receivedErrorsList.getLength()>0){
	    	        	Node receivedErrors = receivedErrorsList.item(0);
	    	        	String error = receivedErrors.getFirstChild().getNodeValue();
	    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getNodeName());
	    	        	Log.d("Aptoide-ManagerUploads login", receivedErrors.getFirstChild().getNodeValue().trim());
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
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return status;
	}
	

//	URL webService = new URL(url);
//	HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection(); //Careful with UnknownHostException. Throws MalformedURLException, IOException
//	
//	conn.setRequestMethod("GET");
//	conn.setRequestProperty("Accept", "application/xml");
//	conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//	conn.setConnectTimeout(TIME_OUT);
//	
//	SharedPreferences sPref = mctx.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
//	String myid = sPref.getString("myId", "NoInfo");
//	String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
//	
//	conn.setRequestProperty("User-Agent", "aptoide-" + mctx.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));
	
	
//	public ViewCache downloadLatestVersionInfo(){
//		ViewCache cache = managerCache.getNewLatestVersionDownloadViewCache();
//		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_UPDATE
//									, serviceData.getString(R.string.self_update), R.string.self_update);
//		ViewDownload download = getNewViewDownload(Constants.URI_LATEST_VERSION_XML, cache, notification);
//
//		download(download, false);
//
//		return cache;
//	}
	
	
	
//	public boolean isIconCached(int appHashid){
//		return managerCache.isIconCached(appHashid);
//	}
//	
//	public void getIcon(ViewDownloadInfo iconInfo, boolean isLoginRequired, ViewLogin login){
//			ViewDownload download;
//			ViewCache cache = managerCache.getNewIconViewCache(iconInfo.getAppHashid());
//			ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_ICONS, iconInfo.getAppName(), iconInfo.getAppHashid());
//			
//			if(isLoginRequired){
//				download = getNewViewDownload(iconInfo.getRemotePath(), login, cache, notification);
//			}else{
//				download = getNewViewDownload(iconInfo.getRemotePath(), cache, notification);
//			}
////			iconsDownloadManager.executeDownload(download);		
//	}
//	
//
//	public void getRepoIcons(ViewDownloadStatus downloadStatus, ArrayList<ViewDownloadInfo> iconsInfo){
//		Log.d("Aptoide-ManagerDownloads", "getRepoIcons ");
//		
//		for (ViewDownloadInfo iconInfo : iconsInfo) {
//			if(isIconCached(iconInfo.getAppHashid())){
//				continue;
//			}else{
//				getIcon(iconInfo, downloadStatus.getRepository().isLoginRequired(), downloadStatus.getRepository().getLogin());
//			}
//		}
//		
//		downloadStatus.incrementOffset(serviceData.getDisplayListsDimensions().getCacheSize());
//		serviceData.getRepoIcons(downloadStatus);
//		
//	}
//	
//	public boolean isScreenCached(int appHashid, int orderNumber){
//		return managerCache.isScreenCached(appHashid, orderNumber);
//	}
//	
//	public void getScreen(ViewDownloadInfo screenInfo, boolean isLoginRequired, ViewLogin login, int screensNumber, int orderNumber){
//			ViewDownload download;
//			ViewCache cache = managerCache.getNewScreenViewCache(screenInfo.getAppHashid(), orderNumber);
//			ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_SCREENS, screenInfo.getAppName(), screenInfo.getAppHashid());
//			
//			if(isLoginRequired){
//				download = getNewViewDownload(screenInfo.getRemotePath(), login, cache, notification);
//			}else{
//				download = getNewViewDownload(screenInfo.getRemotePath(), cache, notification);
//			}
////			screensDownloadManager.executeDownload(download, screensNumber, orderNumber);		
//	}
//	
//	public void getAppScreens(ViewRepository repository, ArrayList<ViewDownloadInfo> screensInfo){
//		if(!(screensInfo.size()>0)){
//			return;
//		}
//		int orderNumber = 1;
//		for (ViewDownloadInfo screenInfo : screensInfo) {
//			Log.d("Aptoide-ManagerDownloads", "getAppScreens screen: "+screenInfo);
//			if(isScreenCached(screenInfo.getAppHashid(), orderNumber)){
//				orderNumber++;
//				if(orderNumber > screensInfo.size()){
//					serviceData.gettingAppScreensFinished(screenInfo.getAppHashid());
//				}
//				continue;
//			}else{
//				getScreen(screenInfo, repository.isLoginRequired(), repository.getLogin(), screensInfo.size(), orderNumber);
//				orderNumber++;
//				if(orderNumber > screensInfo.size()){
//					serviceData.gettingAppScreensFinished(screenInfo.getAppHashid());
//				}
//			}
//			
//		}
//	}
	

	
//	public void getRepoIcons(ViewRepository repository, int offset, ArrayList<ViewDownloadInfo> iconsInfo){
//		int iconsCount = iconsInfo.size();
//		ViewDownloadInfo iconInfo = null;
//		ViewCache cache = null;
//		ViewNotification notification;
//		int downloaded;
//		
//		do{
//			
//			for(downloaded = 0; downloaded < Constants.MAX_PARALLEL_DOWNLOADS; ){ //TODO howto keep always only max number concurrent downloads going
//				final ViewDownload download;
//				iconInfo = iconsInfo.get(downloaded);
//				cache = managerCache.getNewIconViewCache(iconInfo.getAppHashid());
//				notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_ICONS, iconInfo.getAppName(), iconInfo.getAppHashid());
//				
//				if(repository.isLoginRequired()){
//					download = getNewViewDownload(iconInfo.getRemotePath(), repository.getLogin(), cache, notification);
//				}else{
//					download = getNewViewDownload(iconInfo.getRemotePath(), cache, notification);
//				}
//				try{
//
//					new Thread(){
//						public void run(){
//							this.setPriority(Thread.MAX_PRIORITY);
//							
//							int retrysCount = 3;
//							boolean downloadSuccess = false; 
//							
//							do{
//								downloadSuccess = download(download.getNotification().getNotificationHashid(), true);
//								retrysCount--;
//							}while( !downloadSuccess && retrysCount > 0 );
//							
//							if(downloadSuccess){
//								//TODO increment downloaded
//							}
//						}
//					}.start();
//
//				} catch(Exception e){
//					/** this should never happen */
//					//TODO handle exception
//					e.printStackTrace();
//				}
//			}
//			iconsCount -= downloaded;
//			
//		}while (iconsCount > 0);
//		
//		if(repository.getSize() > offset){
//			serviceData.getRepoIcons(repository, offset+iconsCount);
//		}
//	}
	

	
//	public ViewCache startRepoDeltaDownload(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.DELTA);
//	}
//	
//	public ViewCache startRepoBareDownload(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.BARE);
//	}
//	
//	public ViewCache startRepoIconDownload(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.ICON);
//	}
	
//	public ViewCache startRepoAppDownloads(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.DOWNLOAD);
//	}
	
//	public ViewCache startRepoExtraDownload(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.EXTRAS);
//	}
	
	
//	public ViewCache startRepoDownload(ViewRepository repository, EnumInfoType infoType){
//		ViewCache cache = null;
//		ViewNotification notification = null;
//		ViewDownload download;
//		
//		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
//		String xmlRemotePath = null;
//		
//		switch (infoType) {
//			case DELTA:															//TODO info=bare+icon&
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=bare+icon&unix_timestamp=true&show_apphashid=true&hash="+repository.getDelta();
//				cache = managerCache.getNewRepoDeltaViewCache(repository.getHashid());
//				break;
//		
//			case BARE:
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=bare&unix_timestamp=true";
//				EnumAppsSorting bareSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceData.getManagerPreferences().getAppsSortingPolicy());
//				switch (bareSortingPolicy) {
//					case ALPHABETIC:
//						xmlRemotePath += "&order_by=alphabetic&order_direction=ascending";
//						break;
//					case FRESHNESS:
//						xmlRemotePath += "&order_by=freshness&order_direction=descending";
//						break;
//					case STARS:
//						xmlRemotePath += "&order_by=rating&order_direction=descending";
//						break;
//					case DOWNLOADS:
//						xmlRemotePath += "&order_by=downloads&order_direction=descending";
//						break;
//					
//	
//					default:
//						break;
//				}
////				xmlRemotePath = "http://aptoide.com/teste/apps.xml";
////				xmlRemotePath = "http://aptoide.com/testing/xml/info.xml";
//				cache = managerCache.getNewRepoBareViewCache(repository.getHashid());
//				notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_BARE_DOWNLOAD, repoName, repository.getHashid());
//				break;
//				
//			case ICON:
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=icon&show_apphashid=true";
//				EnumAppsSorting iconSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceData.getManagerPreferences().getAppsSortingPolicy());
//				switch (iconSortingPolicy) {
//					case ALPHABETIC:
//						xmlRemotePath += "&order_by=alphabetic&order_direction=ascending";
//						break;
//					case FRESHNESS:
//						xmlRemotePath += "&order_by=freshness&order_direction=descending";
//						break;
//					case STARS:
//						xmlRemotePath += "&order_by=rating&order_direction=descending";
//						break;
//					case DOWNLOADS:
//						xmlRemotePath += "&order_by=downloads&order_direction=descending";
//						break;
//					
//	
//					default:
//						break;
//				}
////				xmlRemotePath = "http://aptoide.com/testing/xml/info_icon.xml";
//				cache = managerCache.getNewRepoIconViewCache(repository.getHashid());
//				break;
//				
////			case DOWNLOAD:
//////				xmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=download";	//TODO implement rest of args
////				xmlRemotePath = "http://aptoide.com/testing/xml/info_download.xml";
////				cache = managerCache.getNewRepoDownloadViewCache(repository.getHashid());
////				break;
//				
//			case STATS:
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_STATS_XML+"show_apphashid=true";
//				EnumAppsSorting statsSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceData.getManagerPreferences().getAppsSortingPolicy());
//				switch (statsSortingPolicy) {
//					case ALPHABETIC:
//						xmlRemotePath += "&order_by=alphabetic&order_direction=ascending";
//						break;
//					case FRESHNESS:
//						xmlRemotePath += "&order_by=freshness&order_direction=descending";
//						break;
//					case STARS:
//						xmlRemotePath += "&order_by=rating&order_direction=descending";
//						break;
//					case DOWNLOADS:
//						xmlRemotePath += "&order_by=downloads&order_direction=descending";
//						break;
//					
//	
//					default:
//						break;
//				}
////				xmlRemotePath = "http://aptoide.com/testing/xml/stats.xml";
//				cache = managerCache.getNewRepoStatsViewCache(repository.getHashid());
//				break;	
//				
////			case EXTRAS:
//////				xmlPath = repository.getUri()+Constants.PATH_REPO_EXTRAS_XML;	//TODO implement rest of args
////				xmlRemotePath = "http://aptoide.com/testing/xml/extras.xml";
////				cache = managerCache.getNewRepoExtrasViewCache(repository.getHashid());
////				break;
//				
//			default:
//				break;
//		}
//		if(!infoType.equals(EnumInfoType.BARE)){
//			notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_UPDATE, repoName, repository.getHashid());
//		}
//		if(repository.isLoginRequired()){
//			xmlRemotePath += "&username="+repository.getLogin().getUsername()+"&password="+repository.getLogin().getPassword();
//		}
////		if(repository.isLoginRequired()){
////			download = getNewViewDownload(xmlRemotePath, repository.getLogin(), cache, notification);
////		}else{
//			download = getNewViewDownload(xmlRemotePath, cache, notification);
////		}
//		
//		download(download, true);
//		
//		return cache;
//	}
//	
//	
//	public ViewCache startRepoAppDownload(ViewRepository repository, int appHashid, EnumInfoType infoType){
//		ViewCache cache = null;
//		ViewNotification notification;
//		ViewDownload download;
//		
//		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
//		String xmlRemotePath = null;
//		
//		switch (infoType) {
//			case DOWNLOAD:
//				String infoRequest = "info=download";
//				if(!isIconCached(appHashid)){
//					infoRequest += "+icon";
//				}
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+infoRequest+"&show_apphashid=true&apphashid="+appHashid;
////				xmlRemotePath = "http://aptoide.com/testing/xml/info_download.xml";
//				cache = managerCache.getNewRepoAppDownloadViewCache(repository.getHashid(), appHashid);
//				break;
//				
//			case EXTRAS:
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_EXTRAS_XML+"show_apphashid=true&apphashid="+appHashid;
////				xmlRemotePath = "http://aptoide.com/testing/xml/extras.xml";
//				cache = managerCache.getNewRepoAppExtrasViewCache(repository.getHashid(), appHashid);
//				break;
//			
//			case STATS:
//				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_STATS_XML+"show_apphashid=true&apphashid="+appHashid;
////				xmlRemotePath = "http://aptoide.com/testing/xml/stats.xml";
//				cache = managerCache.getNewRepoAppStatsViewCache(repository.getHashid(), appHashid);
//				break;
//	
//			default:
//				break;
//		}
//
//		
//		notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_APP_UPDATE, repoName, appHashid);
//		if(repository.isLoginRequired()){
//			xmlRemotePath += "&username="+repository.getLogin().getUsername()+"&password="+repository.getLogin().getPassword();
//		}
////			download = getNewViewDownload(xmlRemotePath, repository.getLogin(), cache, notification);
////		}else{
//			download = getNewViewDownload(xmlRemotePath, cache, notification);
////		}
//		
//		download(download, true);
//		
//		return cache;
//	}
//	
//	public ViewCache downloadMyapp(String uriString, String myappName){
//		ViewCache cache = managerCache.getNewMyappDownloadViewCache(myappName);
//		if(!managerCache.isMyAppCached(myappName)){
//			ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_MYAPP, "myapp", myappName.hashCode());
//			ViewDownload download = getNewViewDownload(uriString, cache, notification);
//			
//			download(download, false);
//		}
//		return cache;
//	}
//	
//	public ViewDownload prepareApkDownload(int appHashid, String appName, String remotePath, ViewLogin login, int size, String md5Hash){
//		ViewCache cache = managerCache.getNewAppViewCache(appHashid, md5Hash);
//		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_APP, appName, appHashid, size);
//		return getNewViewDownload(remotePath, size, login, cache, notification);
//	}
//	
//	public ViewDownload prepareApkDownload(int appHashid, String appName, String remotePath, int size, String md5Hash){
//		ViewCache cache = managerCache.getNewAppViewCache(appHashid, md5Hash);
//		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_APP, appName, appHashid, size);
//		return getNewViewDownload(remotePath, size, cache, notification);
//	}
//	
//	public ViewCache downloadApk(ViewDownload download){
////		Log.d("Aptoide-ManagerDownloads", "apk download: "+download.getCache());
//		if(!getManagerCache().isApkCached(download.getNotification().getTargetsHashid()) || !getManagerCache().md5CheckOk(download.getCache())){
//			download(download, false);
//		}
//		Log.d("Aptoide-ManagerDownloads", "apk download: "+download.getNotification().getTargetsHashid());
//		
//		return download.getCache();
//	}
	
//	public void startRepoDownloadAndProcessing(ViewRepository repository){
//		ViewCache cache;
//		ViewNotification notification;
//		ViewDownload download;
//		
//		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split(".")[Constants.FIRST_ELEMENT];
//		
////		String bareInfoXmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=bare";
//		String bareInfoXmlPath = "http://aptoide.com/testing/xml/info.xml";
//		cache = managerCache.getNewRepoViewCache(repository.getHashid());
//		notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPOS_UPDATE, repoName, repository.getHashid(), 1);
//		if(repository.isLoginRequired()){	//TODO get login from pool
//			download = getNewViewDownload(bareInfoXmlPath, repository.getLogin(), cache, notification);
//		}else{
//			download = getNewViewDownload(bareInfoXmlPath, cache, notification);
//		}
//		download(download.getNotification().getNotificationHashid(), true);
//		//TODO set notification's progressCompletionTarget
//	}
	
	
	
	//TODO refactor magic numbers, logs and exceptions
//	private void download(ViewDownload download, boolean overwriteCache){
//		ViewCache localCache = download.getCache();
//		ViewNotification notification = download.getNotification();
//
//		String localPath = localCache.getLocalPath();
//		String remotePath = download.getRemotePath();
//		int targetBytes;
//		ViewClientStatistics clientStatistics = getClientStatistics();
//
//		if(overwriteCache){
//			getManagerCache().clearCache(localCache);
//		}
//
//		try{
//			FileOutputStream fileOutputStream = new FileOutputStream(localPath);
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet(remotePath);
////			Log.d("Aptoide-download","downloading from: "+remotePath+" to: "+localPath);
//
////				SharedPreferences sPref = context.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
////				String myid = sPref.getString("myId", "NoInfo");
////				String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
//
////TODO refactor this user-agent string
////				mHttpGet.setHeader("User-Agent", "aptoide-" + context.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));
//
//			if(download.isLoginRequired()){		//TODO refactor using username/password args when using webservices (only exception left is when getting hard-disk files)
//				URL url = new URL(remotePath);
//				httpClient.getCredentialsProvider().setCredentials(
//						new AuthScope(url.getHost(), url.getPort()),
//						new UsernamePasswordCredentials(download.getLogin().getUsername(), download.getLogin().getPassword()));
//			}
//
//			HttpResponse httpResponse = httpClient.execute(httpGet);
//			if(httpResponse == null){
//				Log.d("Aptoide-ManagerDownloads","Problem in network... retry...");	
//				httpResponse = httpClient.execute(httpGet);
//				if(httpResponse == null){
//					Log.d("Aptoide-ManagerDownloads","Major network exception... Exiting!");
//					/*msg_al.arg1= 1;
//						 download_error_handler.sendMessage(msg_al);*/
//					throw new TimeoutException();
//				}
//			}
//
//			if(httpResponse.getStatusLine().getStatusCode() == 401){
//				Log.d("Aptoide-ManagerDownloads","401 Time out!");
//				fileOutputStream.close();
//				managerCache.clearCache(download.getCache());
//				throw new TimeoutException();
//			}else if(httpResponse.getStatusLine().getStatusCode() == 404){
//				Log.d("Aptoide-ManagerDownloads","404 Not found!");
//				fileOutputStream.close();
//				managerCache.clearCache(download.getCache());
//				throw new Exception();	//TODO not found exception
//			}else{
////				if(download.isSizeKnown()){
////					targetBytes = download.getSize()*Constants.KBYTES_TO_BYTES;	//TODO check if server sends kbytes or bytes
////					notification.setProgressCompletionTarget(targetBytes);
////				}else{
//				if(httpResponse.containsHeader("Content-Length")){
//					targetBytes = Integer.parseInt(httpResponse.getFirstHeader("Content-Length").getValue());
//					Log.d("Aptoide-ManagerDownloads","targetBytes: "+targetBytes);
//					notification.setProgressCompletionTarget(targetBytes);
//				}
////				}
//				
//				Log.d("Aptoide-ManagerDownloads", "Download target size: "+notification.getProgressCompletionTarget());
//
//				InputStream inputStream= null;
//				
//				if((httpResponse.getEntity().getContentEncoding() != null) && (httpResponse.getEntity().getContentEncoding().getValue().equalsIgnoreCase("gzip"))){
//
//					Log.d("Aptoide-ManagerDownloads","with gzip");
//					inputStream = new GZIPInputStream(httpResponse.getEntity().getContent());
//
//				}else{
//
////					Log.d("Aptoide-ManagerDownloads","No gzip");
//					inputStream = httpResponse.getEntity().getContent();
//
//				}
//				
//				byte data[] = new byte[8096];
//				int bytesRead;
//
//				while((bytesRead = inputStream.read(data, 0, 8096)) > 0) {
//					notification.incrementProgress(bytesRead);
//					fileOutputStream.write(data,0,bytesRead);
//				}
//				Log.d("Aptoide-ManagerDownloads","Download done! Name: "+notification.getActionsTargetName() +" localPath: "+localPath);
//				notification.setCompleted(true);
//				fileOutputStream.flush();
//				fileOutputStream.close();
//				inputStream.close();
//
//				if(localCache.hasMd5Sum()){
//					getManagerCache().md5CheckOk(localCache);
//					//TODO md5check boolean return handle  by  raising exception
//				}
//
//			}
//		}catch (Exception e) { //TODO  retry on java.net.SocketException: The operation timed out
//			//TODO handle exception
//			e.printStackTrace();
//		}
//	}
	
	
	
//TODO refactor	
	
//	public void startDownload(DownloadNode downloadNode){
//		HashMap<String,String> notification = new HashMap<String,String>();
//		notification.put("remotePath", downloadNode.getRemotePath());				//
//		notification.put("md5sum", downloadNode.getMd5sum());						//
//		
//		notification.put("packageName", downloadNode.getPackageName());				//
//		notification.put("appName", downloadNode.getAppName());						//
//		notification.put("intSize", Integer.toString(downloadNode.getSize()));		//
//		notification.put("intProgress", "0");										//
//		notification.put("version", downloadNode.version);							//
//		notification.put("localPath", downloadNode.getLocalPath());					//
//		notification.put("isUpdate", Boolean.toString(downloadNode.isUpdate()));	//
//		if(downloadNode.isRepoPrivate()){
//			notification.put("loginRequired", "true");
//			notification.put("username", downloadNode.getLogins()[0]);
//			notification.put("password", downloadNode.getLogins()[1]);
//		}else{
//			notification.put("loginRequired", "false");
//		}
//		Log.d("Aptoide-DowloadQueueService", "download Started");
//		notifications.put(downloadNode.getPackageName().hashCode(), notification);
//		setNotification(downloadNode.getPackageName().hashCode(), 0);
//		downloadFile(downloadNode.getPackageName().hashCode());
//	}
	
//-------------------
	
	
//	public void startExternalDownload(String remotePath, String localPath, String apkName, Context context){
//		this.context = context;
//		HashMap<String,String> notification = new HashMap<String,String>();
//		notification.put("remotePath", remotePath);
//	}
	
//	public void setCurrentContext(Context context){
//		this.context = context;
//	}

	
//TODO moved to notifications, just check if there's something useful


//	private void setNotification(int apkidHash, int progress) {
//
//		String appName = notifications.get(apkidHash).get("appName");
//		int size = Integer.parseInt(notifications.get(apkidHash).get("intSize"));
//		String version = notifications.get(apkidHash).get("version");
//		
//		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
//		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
//		StringBuilder textApp = new StringBuilder(getString(R.string.download_alrt)+" "+appName);
//		if(version!=null){
//			Log.d("Aptoide", "External download taking place. Unable to retrive version.");
//			textApp.append(" v."+version);
//		}
//		contentView.setTextViewText(R.id.download_notification_name, textApp.toString());
//		
//		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, progress, false);	
//		
//    	Intent onClick = new Intent();
//		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt");
//		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//		onClick.setAction("cm.aptoide.pt.FROM_NOTIFICATION");
//    	
//    	// The PendingIntent to launch our activity if the user selects this notification
//    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
//
//    	Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.download_alrt)+" "+appName, System.currentTimeMillis());
//    	notification.flags |= Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
//		notification.contentView = contentView;
//
//
//		// Set the info for the notification panel.
//    	notification.contentIntent = onClickAction;
////    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);
//
//
//		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//    	// Send the notification.
//    	// We use the position because it is a unique number.  We use it later to cancel.
//    	notificationManager.notify(apkidHash, notification); 
//    	
////		Log.d("Aptoide-DownloadQueueService", "Notification Set");
//    }
//	
//	private void setFinishedNotification(int apkidHash, String localPath) {
//		
//		String packageName = notifications.get(apkidHash).get("packageName");
//		String appName = notifications.get(apkidHash).get("appName");
//		int size = Integer.parseInt(notifications.get(apkidHash).get("intSize"));
//		String version = notifications.get(apkidHash).get("version");
//		
//		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
//		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
//		contentView.setTextViewText(R.id.download_notification_name, getString(R.string.finished_download_message)+" "+appName+" v."+version);
//		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, size*KBYTES_TO_BYTES, false);	
//		
//		Intent onClick = new Intent("pt.caixamagica.aptoide.INSTALL_APK", Uri.parse("apk:"+packageName));
//		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt.RemoteInTab");
//		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//    	onClick.putExtra("localPath", localPath);
//    	onClick.putExtra("packageName", packageName);
//    	onClick.putExtra("apkidHash", apkidHash);
//    	onClick.putExtra("isUpdate", Boolean.parseBoolean(notifications.get(packageName.hashCode()).get("isUpdate")));
//    	/*Changed by Rafael Campos*/
//    	onClick.putExtra("version", version);
//		 Log.d("Aptoide-DownloadQueuService","finished notification apkidHash: "+apkidHash +" localPath: "+localPath);	
//    	
//    	// The PendingIntent to launch our activity if the user selects this notification
//    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
//				
//    	Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.finished_download_alrt)+" "+appName, System.currentTimeMillis());
//    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.contentView = contentView;
//
//
//		// Set the info for the notification panel.
//    	notification.contentIntent = onClickAction;
////    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);
//
//
//		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//    	// Send the notification.
//    	// We use the position because it is a unique number.  We use it later to cancel.
//    	notificationManager.notify(apkidHash, notification); 
//    	
////		Log.d("Aptoide-DownloadQueueService", "Notification Set");
//		
//	}


//-------------------
	
//TODO refactor
	

//	
//	
//	/*
//	 * Notification UI Handlers
//	 * 
//	 */
//	private Handler downloadHandler = new Handler() {
//        @Override
//        public void handleMessage(Message downloadArguments) {
//        	if(downloadArguments.arg1 == 1){
//        		int apkidHash = downloadArguments.arg2;
//        		String localPath =  (String) downloadArguments.obj;
////        		notificationManager.cancel(downloadArguments.arg2);
//        		setFinishedNotification(apkidHash, localPath);
////   			 	notifications.remove(apkidHash);
//        	}else{ }
//        }
//	};
//	
//	 protected Handler downloadProgress = new Handler(){
//
//		 @Override
//		 public void handleMessage(Message progressArguments) {
//			 super.handleMessage(progressArguments);
//			 	
//			 int apkidHash = progressArguments.arg1;
//			 int intermediateProgress = progressArguments.arg2;
//			 //Log.d("Aptoide","Progress: " + pd.getProgress() + " Other: " +  (pd.getMax()*0.96) + " Adding: " + msg.what);
//			 Log.d("Aptoide-downloadQueue", "apkidHash: "+apkidHash+" current progress - "+notifications.get(apkidHash).get("intProgress") + "  additional - "+ intermediateProgress);
//			 int progress = Integer.parseInt(notifications.get(apkidHash).get("intProgress"))+intermediateProgress;
//			 notifications.get(apkidHash).put("intProgress", Integer.toString(progress));
//			 setNotification(apkidHash, progress);
//		 }
//	 };
//	
//
//	 private Handler downloadErrorHandler = new Handler() {
//		 @Override
//		 public void handleMessage(Message downloadArguments) {
//			 int apkHash = downloadArguments.arg2;
//			 notificationManager.cancel(apkHash);
////			 notifications.remove(apkHash);
//			 if(downloadArguments.arg1 == 1){
//				 Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//			 }else{
//				 Toast.makeText(context, getString(R.string.md5_error), Toast.LENGTH_LONG).show();
//			 }
//		 }
//	 };
		
//-------------------

}
