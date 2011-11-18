/**
 * Splash, part of Aptoide
 * Copyright (C) 2011 Duarte Silveira
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
package cm.aptoide.pt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class SelfUpdate extends Activity {
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//    	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//    	alertBuilder.setCancelable(false)
//    				.setPositiveButton(R.string.dialog_yes , new DialogInterface.OnClickListener() {
//    					public void onClick(DialogInterface dialog, int id) {
//    						dialog.cancel();
//    						setContentView(R.layout.auto_updating);
//    						new DownloadSelfUpdate().execute();
//    					}
//    				})    	
//    				.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
//    					public void onClick(DialogInterface dialog, int id) {
//    						dialog.cancel();
//    						finish();
//    					}
//    				})
//    				.setMessage(R.string.update_self_msg)
//    				;
//    	
//    	AlertDialog alert = alertBuilder.create();
//    	
//    	alert.setTitle(R.string.update_self_title);
//    	alert.setIcon(R.drawable.icon);
//    	
//    	alert.show();
//    	
//		super.onCreate(savedInstanceState);
//	}
//	
//	private String getXmlElement(String name) throws ParserConfigurationException, MalformedURLException, SAXException, IOException{
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//        Document dom = builder.parse( new InputSource(new URL(LATEST_VERSION_CODE_URI).openStream()) );
//        dom.getDocumentElement().normalize();
//        NodeList items = dom.getElementsByTagName(name);
//        if(items.getLength()>0){
//        	Node item = items.item(0);
//        	Log.d("Aptoide-XmlElement Name", item.getNodeName());
//        	Log.d("Aptoide-XmlElement Value", item.getFirstChild().getNodeValue().trim());
//        	return item.getFirstChild().getNodeValue().trim();
//        }
//        return "0";
//	}
//	
//	private class DownloadSelfUpdate extends AsyncTask<Void, Void, Void>{
//		private final ProgressDialog dialog = new ProgressDialog(SelfUpdate.this);
//	
//		String latestVersionUri;
//		String referenceMd5;
//		
//		
//		void retrieveUpdateParameters(){
//			try{
//				latestVersionUri = getXmlElement("uri");
//				referenceMd5 = getXmlElement("md5");
//			}catch (Exception e) {
//				e.printStackTrace();
//				Log.d("Aptoide-Auto-Update", "Update connection failed!  Keeping current version.");
//			}
//		}
//		
//		@Override
//		protected void onPreExecute() {
//			this.dialog.setMessage("Retrieving update...");
//			this.dialog.show();
//			super.onPreExecute();
//			retrieveUpdateParameters();
//		}
//
//		@Override
//		protected Void doInBackground(Void... paramArrayOfParams) {
//			try{
//				if(latestVersionUri==null){
//					retrieveUpdateParameters();
//				}
////				Message msg_al = new Message();
//				// If file exists, removes it...
//				 File f_chk = new File(TMP_UPDATE_FILE);
//				 if(f_chk.exists()){
//					 f_chk.delete();
//				 }
//				 f_chk = null;
//				
//				FileOutputStream saveit = new FileOutputStream(TMP_UPDATE_FILE);
//				DefaultHttpClient mHttpClient = new DefaultHttpClient();
//				HttpGet mHttpGet = new HttpGet(latestVersionUri);
//	
//				HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
//				
//				if(mHttpResponse == null){
//					 Log.d("Aptoide","Problem in network... retry...");	
//					 mHttpResponse = mHttpClient.execute(mHttpGet);
//					 if(mHttpResponse == null){
//						 Log.d("Aptoide","Major network exception... Exiting!");
//						 /*msg_al.arg1= 1;
//						 download_error_handler.sendMessage(msg_al);*/
//						 throw new TimeoutException();
//					 }
//				 }
//				
//				if(mHttpResponse.getStatusLine().getStatusCode() == 401){
//					throw new TimeoutException();
//				}else{
//					InputStream getit = mHttpResponse.getEntity().getContent();
//					byte data[] = new byte[8096];
//					int bytesRead;
//					bytesRead = getit.read(data, 0, 8096);
//					while(bytesRead != -1) {
//	//							download_tick.sendEmptyMessage(readed);
//						saveit.write(data,0,bytesRead);
//						bytesRead = getit.read(data, 0, 8096);
//					}
//					Log.d("Aptoide","Download done!");
//					saveit.flush();
//					saveit.close();
//					getit.close();
//				}
//			}catch (Exception e) { 
////						download_error_handler.sendMessage(msg_al);
//				e.printStackTrace();
//				Toast.makeText(mctx, mctx.getString(R.string.network_auto_update_error), Toast.LENGTH_LONG);
//				Log.d("Aptoide-Auto-Update", "Update connection failed!  Keeping current version.");
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			
//			if (this.dialog.isShowing()) {
//				this.dialog.dismiss();
//			}
//			super.onPostExecute(result);
//			
//			if(!(referenceMd5==null)){
//				try{
//					File apk = new File(TMP_UPDATE_FILE);
//					Md5Handler hash = new Md5Handler();
//					if( referenceMd5.equalsIgnoreCase(hash.md5Calc(apk))){
//		//				msg_al.arg1 = 1;
//		//						download_handler.sendMessage(msg_al);
//						
//						doUpdateSelf();
//				    	
//					}else{
//						Log.d("Aptoide",referenceMd5 + " VS " + hash.md5Calc(apk));
//		//				msg_al.arg1 = 0;
//		//						download_error_handler.sendMessage(msg_al);
//						throw new Exception(referenceMd5 + " VS " + hash.md5Calc(apk));
//					}
//				}catch (Exception e) {
//					e.printStackTrace();
//					Toast.makeText(mctx, mctx.getString(R.string.md5_auto_update_error), Toast.LENGTH_LONG);
//					Log.d("Aptoide-Auto-Update", "Update package checksum failed!  Keeping current version.");
//					if (this.dialog.isShowing()) {
//						this.dialog.dismiss();
//					}
//					proceed();
//					super.onPostExecute(result);
//					
//				}
//			}
//			
//		}
//		
//	}
//	
//	private void doUpdateSelf(){
//		Intent intent = new Intent();
//    	intent.setAction(android.content.Intent.ACTION_VIEW);
//    	intent.setDataAndType(Uri.parse("file://" + TMP_UPDATE_FILE), "application/vnd.android.package-archive");
//    	
//    	startActivityForResult(intent, UPDATE_SELF);
//	}
	
}
