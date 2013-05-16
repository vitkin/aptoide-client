/*
 * ApplicationAptoide, part of Aptoide
 * Copyright (C) 2012 Duarte Silveira
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.acra.*;
import org.acra.annotation.*;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.SharedPreferences;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import cm.aptoide.com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import cm.aptoide.com.nostra13.universalimageloader.core.DisplayImageOptions;
import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoader;
import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import cm.aptoide.com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import cm.aptoide.com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import cm.aptoide.com.nostra13.universalimageloader.core.download.ImageDownloader;
import cm.aptoide.pt.preferences.ManagerPreferences;
import cm.aptoide.pt.util.NetworkUtils;

/**
 * ApplicationAptoide, centralizes, statically, calls to instantiated objects
 *
 * @author dsilveira
 *
 */
//@ReportsCrashes(
//   		formKey = "",
//   		formUri = "http://www.backendofyourchoice.com/reportpath"
//)

public class ApplicationAptoide extends Application {

	private ManagerPreferences managerPreferences;
	private static Context context;
	public static boolean DEBUG_MODE = false;
	public static File DEBUG_FILE;
    public static String PARTNERID;
    public static String DEFAULTSTORE;
    public static String BRANDICON;
    public static boolean MATURECONTENTSWITCH = true;
    public static String SPLASHSCREEN;
    public static String SPLASHSCREENLAND;
    public static boolean MATURECONTENTSWITCHVALUE = true;
    public static boolean MULTIPLESTORES = true;
    public static boolean CUSTOMEDITORSCHOICE = false;
    public static boolean SEARCHSTORES = true;
    public static String APTOIDETHEME = "";
    public static String MARKETNAME = "";

    static enum Elements {PARTNERID, DEFAULTSTORENAME, BRAND, SPLASHSCREEN, MATURECONTENTSWITCH, MATURECONTENTSWITCHVALUE,SEARCHSTORES, MULTIPLESTORES, CUSTOMEDITORSCHOICE, APTOIDETHEME, SPLASHSCREENLAND, MARKETNAME }
    public static enum StoreElements { storeconf ,  theme, avatar, description, view, items, none };
    
    

	@Override
	public void onCreate() {
//		ACRA.init(this);
		AptoideThemePicker.setAptoideTheme(this);
		managerPreferences = new ManagerPreferences(getApplicationContext());
		setContext(getApplicationContext());
		 // Create global configuration and initialize ImageLoader with this configuration

		SharedPreferences sPref = getSharedPreferences("settings", MODE_PRIVATE);
        if(sPref.contains("PARTNERID")){

            PARTNERID = sPref.getString("PARTNERID",null);
            DEFAULTSTORE = sPref.getString("DEFAULTSTORE",null);
            BRANDICON = sPref.getString("BRANDICON",null);
            MATURECONTENTSWITCH = sPref.getBoolean("MATURECONTENTSWITCH", true);
            SPLASHSCREEN = sPref.getString("SPLASHSCREEN", null);
            SPLASHSCREENLAND = sPref.getString("SPLASHSCREEN_LAND", null);
            MATURECONTENTSWITCHVALUE = sPref.getBoolean("MATURECONTENTSWITCHVALUE", true);
            MULTIPLESTORES = sPref.getBoolean("MULTIPLESTORES",true);
            CUSTOMEDITORSCHOICE = sPref.getBoolean("CUSTOMEDITORSCHOICE",false);
            SEARCHSTORES = sPref.getBoolean("SEARCHSTORES",true);
            APTOIDETHEME = sPref.getString("APTOIDETHEME","DEFAULT");
            MARKETNAME = sPref.getString("MARKETNAME", getString(R.string.app_name));



        }else{
            try {
                InputStream file = getAssets().open("boot_config.xml");
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();

                parser.parse(file,new DefaultHandler(){
                    StringBuilder sb = new StringBuilder();

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        super.startElement(uri, localName, qName, attributes);
                        sb.setLength(0);
                    }


                    @Override
                    public void characters(char[] ch, int start, int length) throws SAXException {
                        super.characters(ch, start, length);    //To change body of overridden methods use File | Settings | File Templates.
                        sb.append(ch,start,length);
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName) throws SAXException {
                        super.endElement(uri, localName, qName);
                        try{
                        Elements element = Elements.valueOf(localName.toUpperCase(Locale.ENGLISH));
                            switch (element) {
                                case PARTNERID:
                                    PARTNERID = sb.toString();
                                    Log.d("Partner ID", PARTNERID + "");
                                    break;
                                case DEFAULTSTORENAME:
                                    DEFAULTSTORE = sb.toString();
                                    Log.d("Default store", DEFAULTSTORE + "");
                                    break;
                                case BRAND:
                                    BRANDICON = sb.toString();
                                    Log.d("Brand icon", BRANDICON + "");
                                    break;
                                case SPLASHSCREEN:
                                    SPLASHSCREEN = sb.toString();
                                    Log.d("Splashscreen", SPLASHSCREEN+ "");
                                    break;
                                case SPLASHSCREENLAND:
                                    SPLASHSCREENLAND = sb.toString();
                                    Log.d("Splashscreen landscape", SPLASHSCREENLAND+ "");
                                    break;
                                case MATURECONTENTSWITCH:
                                    MATURECONTENTSWITCH = Boolean.parseBoolean(sb.toString());
                                    Log.d("Mature content Switch", MATURECONTENTSWITCH+ "");
                                    break;
                                case MATURECONTENTSWITCHVALUE:
                                    MATURECONTENTSWITCHVALUE = Boolean.parseBoolean(sb.toString());
                                    Log.d("Mature content value", MATURECONTENTSWITCHVALUE+ "");
                                    break;
                                case MULTIPLESTORES:
                                    MULTIPLESTORES = Boolean.parseBoolean(sb.toString());
                                    Log.d("Multiple stores", MULTIPLESTORES+ "");
                                    break;
                                case CUSTOMEDITORSCHOICE:
                                    CUSTOMEDITORSCHOICE = Boolean.parseBoolean(sb.toString());
                                    Log.d("Custom editors choice", CUSTOMEDITORSCHOICE+ "");
                                    break;
                                case APTOIDETHEME:
                                	APTOIDETHEME = sb.toString();
                                	Log.d("Aptoide theme", APTOIDETHEME+ "");
                                    break;
                                case MARKETNAME:
                                	MARKETNAME = sb.toString();
                                	Log.d("Market name", MARKETNAME+ "");
                                	break;
                                case SEARCHSTORES:
                                    SEARCHSTORES = Boolean.parseBoolean(sb.toString());
                                    Log.d("Search stores", SEARCHSTORES+ "");
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });



            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ParserConfigurationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (SAXException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            sPref.edit().putString("PARTNERID",PARTNERID)
            .putString("DEFAULTSTORE",DEFAULTSTORE)
            .putString("BRANDICON",BRANDICON)
            .putBoolean("MATURECONTENTSWITCH", MATURECONTENTSWITCH)
            .putString("SPLASHSCREEN", SPLASHSCREEN)
            .putBoolean("MATURECONTENTSWITCHVALUE", MATURECONTENTSWITCHVALUE)
            .putBoolean("MULTIPLESTORES",MULTIPLESTORES)
            .putBoolean("CUSTOMEDITORSCHOICE",CUSTOMEDITORSCHOICE)
            .putBoolean("SEARCHSTORES",SEARCHSTORES)
            .putString("APTOIDETHEME", APTOIDETHEME)
            .commit();

        }

        DEBUG_FILE = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.aptoide/debug.log");

		if(DEBUG_FILE.exists()){
			DEBUG_MODE = true;
		}

		DisplayImageOptions options = new DisplayImageOptions.Builder()
															 .displayer(new FadeInBitmapDisplayer(1000))
															 .showStubImage(android.R.drawable.sym_def_app_icon)
															 .resetViewBeforeLoading()
															 .cacheInMemory()
															 .cacheOnDisc()
															 .build();

		ImageLoaderConfiguration config;

		if(DEBUG_MODE){
			config = new ImageLoaderConfiguration.Builder(getApplicationContext())
																				  .defaultDisplayImageOptions(options)
																				  .discCache(new UnlimitedDiscCache(new File(Environment.getExternalStorageDirectory().getPath()+"/.aptoide/icons/")))
																				  .enableLogging()
																				  .imageDownloader(new ImageDownloaderWithPermissions())
																				  .build();
		}else{
			config = new ImageLoaderConfiguration.Builder(getApplicationContext())
																				  .defaultDisplayImageOptions(options)
																				  .discCache(new UnlimitedDiscCache(new File(Environment.getExternalStorageDirectory().getPath()+"/.aptoide/icons/")))
																				  .imageDownloader(new ImageDownloaderWithPermissions())
																				  .build();
		}

        ImageLoader.getInstance().init(config);


		super.onCreate();
	}

	public ManagerPreferences getManagerPreferences(){
		return managerPreferences;
	}

	/**
	 * @return the context
	 */
	public static Context getContext() {
		return context;
	}

	public class ImageDownloaderWithPermissions extends ImageDownloader{

		/** {@value} */
		public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
		/** {@value} */
		public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

		private int connectTimeout;
		private int readTimeout;

		public ImageDownloaderWithPermissions() {
			this(DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
		}

		public ImageDownloaderWithPermissions(int connectTimeout, int readTimeout) {
			this.connectTimeout = connectTimeout;
			this.readTimeout = readTimeout;
		}

		@Override
		public InputStream getStreamFromNetwork(URI imageUri) throws IOException {

	        boolean download = NetworkUtils.isPermittedConnectionAvailable(context, managerPreferences.getIconDownloadPermissions());
	        
	        Log.d("AplicationAptoide", ""+download);
	        Log.d("AplicationAptoide", ""+managerPreferences.getIconDownloadPermissions());
	        
	        if(download){
	        	URLConnection conn = imageUri.toURL().openConnection();
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
				return new FlushedInputStream(new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE));
	        }else{
	        	return null;
	        }
		}

	}

	/**
	 * @param context the context to set
	 */
	public static void setContext(Context context) {
		ApplicationAptoide.context = context;
	}
}
