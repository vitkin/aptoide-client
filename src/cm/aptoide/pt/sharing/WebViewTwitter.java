/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.sharing;

import cm.aptoide.pt2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewTwitter extends Activity {

	private String url;
	private WebView TwitterWebView;
	private TextView waitingText;
	private ProgressBar waitingBar;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_twitter);

        url = "http://mobile.twitter.com/aptoide";

        try {
        	waitingText = (TextView) findViewById(R.id.waiting_text);
        	waitingBar = (ProgressBar) findViewById(R.id.waiting_bar);
        	TwitterWebView = (WebView) findViewById(R.id.wvTwitter);

            TwitterWebView.setWebViewClient(new WebViewClient() {
    			public boolean shouldOverrideUrlLoading (WebView view, String url) {
    				view.loadUrl(url);
    				return true;
    			}

    		});
            TwitterWebView.getSettings().setJavaScriptEnabled(true);
            TwitterWebView.getSettings().setDomStorageEnabled(true);
            TwitterWebView.getSettings().setSavePassword(false);
            TwitterWebView.getSettings().setSaveFormData(false);
            TwitterWebView.getSettings().setSupportZoom(false);
            
            TwitterWebView.setWebChromeClient(new WebChromeClient() {
    			public void onProgressChanged(WebView view, int progress)
    			{
                    setProgress(progress * 100);
                    
                    if(progress == 100){
                    	waitingText.setVisibility(View.GONE);
                    	waitingBar.setVisibility(View.GONE);
                    }
    			}
    		});
            
            TwitterWebView.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

