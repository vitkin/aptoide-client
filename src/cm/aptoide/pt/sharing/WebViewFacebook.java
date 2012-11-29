/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.sharing;

import java.net.MalformedURLException;
import java.net.URL;

import cm.aptoide.pt.R;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewFacebook extends Activity {  
	
	private String url;
	private WebView webView;  
	private TextView waitingText;
	private ProgressBar waitingProgress;
	
	protected static String OAUTH_ENDPOINT = "http://www.facebook.com/aptoide";  
	public static final String REDIRECT_URI = "fbconnect://success";  
	public static final String CANCEL_URI = "fbconnect:cancel";  
	static final String DISPLAY_STRING = "touch";  

	/** Called when the activity is first created. */  
	@Override  
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.webview_facebook);  
		Bundle parameters = new Bundle();  
//		parameters.putString("client_id", APP_ID);        
		parameters.putString("type", "user_agent");  
		parameters.putString("redirect_uri", REDIRECT_URI);  
		url = OAUTH_ENDPOINT + "?" + encodeUrl(parameters);  
		Log.d(this.getClass().getName(), "url: " + url);  
		
		waitingText = (TextView) findViewById(R.id.waiting_text);
		waitingProgress = (ProgressBar) findViewById(R.id.waiting_bar);
		
		webView = (WebView) findViewById(R.id.wvFacebook);  
		webView.getSettings().setJavaScriptEnabled(true);  
		webView.setVerticalScrollBarEnabled(false);  
		webView.setHorizontalScrollBarEnabled(false);  
		webView.setWebViewClient(new FbWebViewClient());  
		
		webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                setProgress(progress * 100);
  
                if(progress == 100){
                	waitingText.setVisibility(View.GONE);
                	waitingProgress.setVisibility(View.GONE);
                }
            }
        });
		
		webView.loadUrl(url);  
	}  
	
	private class FbWebViewClient extends WebViewClient {  
		@Override  
		public boolean shouldOverrideUrlLoading(WebView view, String url) {  
			Log.d(this.getClass().getName(), "return url: " + url);  
			if (url.startsWith(REDIRECT_URI)) {  
				Bundle values = parseUrl(url);  
				Log.d(this.getClass().getName(), "values: " + printBundle(values));  
				String error = values.getString("error_reason");  
				if (error == null) {  
					Log.d(this.getClass().getName(), "error was null");   
				} else {  
					Log.d(this.getClass().getName(), "error was not null");  
				}  
				return true;  
			} else if (url.startsWith(CANCEL_URI)) {  
				Log.d(this.getClass().getName(), "cancelled");  
				return true;  
			} else if (url.contains(DISPLAY_STRING)) {  
				Log.d(this.getClass().getName(), "display string");  
				return false;  
			}   
			Log.d(this.getClass().getName(), "nothing");  
			return false;  
		}

	}  
	
	public static String encodeUrl(Bundle parameters) {  
		if (parameters == null) {  
			return "";  
		}  
		StringBuilder sb = new StringBuilder();  
		boolean first = true;  
		for (String key : parameters.keySet()) {  
			if (first) first = false; else sb.append("&");  
			sb.append(key + "=" + parameters.getString(key));  
		}  
		return sb.toString();  
	}  
	
	public static Bundle parseUrl(String url) {  
		// hack to prevent MalformedURLException  
		url = url.replace("fbconnect", "http");   
		try {  
			URL u = new URL(url);  
			Bundle b = decodeUrl(u.getQuery());  
			b.putAll(decodeUrl(u.getRef()));  
			return b;  
		} catch (MalformedURLException e) {  
			return new Bundle();  
		}  
	}  
	
	public static Bundle decodeUrl(String s) {  
		Bundle params = new Bundle();  
		if (s != null) {  
			String array[] = s.split("&");  
			for (String parameter : array) {  
				String v[] = parameter.split("=");  
				params.putString(v[0], v[1]);  
			}  
		}  
		return params;  
	}  
	
	public static String printBundle(Bundle b) {  
		StringBuilder sb = new StringBuilder();  
		boolean first = true;  
		for (String key : b.keySet()) {  
			if (first) first = false; else sb.append("\n");  
			sb.append(key + "=" + b.getString(key));  
		}  
		return sb.toString();  
	}  
}  
