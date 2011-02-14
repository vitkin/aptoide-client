package cm.aptoide.pt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

public class NetworkApis {

	public static HttpResponse getHttpResponse(String url, String srv, Context mctx){
		try{
			DbHandler db = new DbHandler(mctx);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);

			DefaultHttpClient mHttpClient = new DefaultHttpClient(httpParameters);
			mHttpClient.setRedirectHandler(new RedirectHandler() {

				public boolean isRedirectRequested(HttpResponse response,
						HttpContext context) {
					return false;
				}

				public URI getLocationURI(HttpResponse response, HttpContext context)
				throws ProtocolException {
					return null;
				}
			});
			
			HttpGet mHttpGet = new HttpGet(url);

			String[] logins = null; 
			logins = db.getLogin(srv);
			if(logins != null){
				URL mUrl = new URL(url);
				mHttpClient.getCredentialsProvider().setCredentials(
						new AuthScope(mUrl.getHost(), mUrl.getPort()),
						new UsernamePasswordCredentials(logins[0], logins[1]));
			}

			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			
			
			// Redirect used... 
			Header[] azz = mHttpResponse.getHeaders("Location");
			if(azz.length > 0){
				String newurl = azz[0].getValue();
				mHttpGet = null;
				mHttpGet = new HttpGet(newurl);
				
				if(logins != null){
	    			URL mUrl = new URL(newurl);
	    			mHttpClient.getCredentialsProvider().setCredentials(
	                        new AuthScope(mUrl.getHost(), mUrl.getPort()),
	                        new UsernamePasswordCredentials(logins[0], logins[1]));
	    		}
				
				mHttpResponse = null;
				mHttpResponse = mHttpClient.execute(mHttpGet);
				
				
			}
			return mHttpResponse;
		}catch(IOException e) {return null; }
		
	}
	
	
	public static HttpResponse getHttpResponse(String url, String usr, String pwd, Context mctx){
		try{
			//DbHandler db = new DbHandler(mctx);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);

			DefaultHttpClient mHttpClient = new DefaultHttpClient(httpParameters);
			mHttpClient.setRedirectHandler(new RedirectHandler() {

				public boolean isRedirectRequested(HttpResponse response,
						HttpContext context) {
					return false;
				}

				public URI getLocationURI(HttpResponse response, HttpContext context)
				throws ProtocolException {
					return null;
				}
			});
			
			HttpGet mHttpGet = new HttpGet(url);

			//String[] logins = null; 
			//logins = db.getLogin(srv);
			if(usr != null || pwd != null){
				URL mUrl = new URL(url);
				mHttpClient.getCredentialsProvider().setCredentials(
						new AuthScope(mUrl.getHost(), mUrl.getPort()),
						new UsernamePasswordCredentials(usr, pwd));
			}

			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			
			
			// Redirect used... 
			Header[] azz = mHttpResponse.getHeaders("Location");
			if(azz.length > 0){
				String newurl = azz[0].getValue();
				mHttpGet = null;
				mHttpGet = new HttpGet(newurl);
				
				if(usr != null || pwd != null){
	    			URL mUrl = new URL(newurl);
	    			mHttpClient.getCredentialsProvider().setCredentials(
	                        new AuthScope(mUrl.getHost(), mUrl.getPort()),
	                        new UsernamePasswordCredentials(usr, pwd));
	    		}
				
				mHttpResponse = null;
				mHttpResponse = mHttpClient.execute(mHttpGet);
				
				
			}
			return mHttpResponse;
		}catch(IOException e) {return null; }
		
	}
	
	
	
}
