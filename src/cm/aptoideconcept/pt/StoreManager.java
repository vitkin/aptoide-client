package cm.aptoideconcept.pt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class StoreManager extends FragmentActivity implements LoaderCallbacks<Cursor> {

	ListView lv;
	private Context context;
	private DBHandler db;
	View alertDialogView;
	private boolean update = false;
	private boolean redraw = false;
	private CursorAdapter adapter;
	private enum returnStatus {OK, LOGIN_REQUIRED, BAD_LOGIN, FAIL, EXCEPTION};
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context=this;
		db= new DBHandler(context);
		db.open();
		setContentView(R.layout.storemanager);
		
		
		lv = (ListView) findViewById(android.R.id.list);
		adapter = new CursorAdapter(this,null,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
			
			@Override
			public View newView(Context context, Cursor arg1, ViewGroup arg2) {
				return LayoutInflater.from(context).inflate(R.layout.storemanager_row, null);
			}
			
			@Override
			public void bindView(View v, Context arg1, Cursor c) {
				((TextView) v.findViewById(R.id.rowTextView)).setText(c.getString(1));
				int number_apks= c.getInt(9);
				if(number_apks>0){
					((TextView) v.findViewById(R.id.numberapks)).setText(getText(R.string.number_apks)+" "+number_apks);
				}
				
			}
		};
		lv.setAdapter(adapter);
		redraw();
		registerForContextMenu(lv);
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		menu.add(0,0,0,getString(R.string.menu_rem_repo));
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	
	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		
		new Thread(new Runnable() {
			public void run() {
				try{
					AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
					db.beginTransation();
					db.removeRepo(info.id,true);
					db.endTransation();
					
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					System.out.println("Removed");
					redraw=true;
					redraw();
				}
				
			}
		}).start();
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0,0,0,"Add repo");
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==0){
			alertDialogView = LayoutInflater.from(context).inflate(R.layout.addrepo, null);
			alertDialog = new AlertDialog.Builder(this).setView(alertDialogView).create();
			alertDialog.setButton(getString(R.string.btn_add_repo), addRepoListener);
			alertDialog.show();
			
		}
		return super.onOptionsItemSelected(item);
	}
	String uri;
	AlertDialog alertDialog;
	
	
	private OnClickListener addRepoListener = new OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			uri = serverCheck(((TextView) alertDialogView.findViewById(R.id.edit_uri)).getText().toString());
			final String password = ((TextView) alertDialogView.findViewById(R.id.sec_pwd)).getText().toString();
			final String username = ((TextView) alertDialogView.findViewById(R.id.sec_user)).getText().toString();
			pd=new ProgressDialog(context);
			pd.setMessage(getString(R.string.please_wait));
			pd.show();
			
			new Thread(new Runnable() {
				private boolean containsRepo;

				public void run() {
					try {
						uri = checkServer(uri, username, password);
						containsRepo = serverContainsRepo(uri);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(uri!=null&&!containsRepo){
							db.insertRepository(uri,username,password);
						}
						
						runOnUiThread(new Runnable() {
							
							public void run() {
								redraw();
								pd.dismiss();
								if(uri==null&&containsRepo){
									alertDialog.show();
								}
								
								
							}
						});
						
					}
				}

				
			}).start();
			
			new Thread(new Runnable() {
				
				public void run() {
					generateXML();
				}
			}).start();
			
			update=true;
		}
	};
	
	private void redraw() {
		getSupportLoaderManager().restartLoader(0x10, null, this);
	}
	
	private boolean serverContainsRepo(String uri) {
		
		Cursor c = db.getRepositories();
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			if(c.getString(1).equals(uri)){
				return true;
			}
		}
		c.close();
		
		return false;
	}

	protected String checkServer(String uri_str, String user, String pwd) {
		returnStatus result = checkServerConnection(uri_str, user, pwd);
		switch (result) {
		case OK:
			return uri_str;

		case LOGIN_REQUIRED:

			break;

		case BAD_LOGIN:
			break;

		case FAIL:
			Log.d("Aptoide-ManageRepo", "return fail");
			uri_str = uri_str.substring(0, uri_str.length()-1)+".bazaarandroid.com/";
			Log.d("Aptoide-ManageRepo", "repo uri: "+uri_str);
			break;

		default:
			Log.d("Aptoide-ManageRepo", "return exception");
			uri_str = uri_str.substring(0, uri_str.length()-1)+".bazaarandroid.com/";
			Log.d("Aptoide-ManageRepo", "repo uri: "+uri_str);
			break;
		}
		if(result.equals(returnStatus.FAIL) || result.equals(returnStatus.EXCEPTION)){
			result = checkServerConnection(uri_str, user, pwd);
			switch (result) {
			case OK:
				return uri_str;

			case LOGIN_REQUIRED:

				break;

			case BAD_LOGIN:
				break;

			case FAIL:

				break;

			default:
				break;
			}
		}				
		return null;
		
	}

	protected void generateXML() {
			
	}

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader a = new SimpleCursorLoader(context) {
			
			@Override
			public Cursor loadInBackground() {
				return db.getRepositories();
			}
		};
		return a;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.changeCursor(arg1);				
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		((CursorAdapter) lv.getAdapter()).changeCursor(null);	
	}
	
	private String serverCheck(String uri_str) {
		if(uri_str.length()!=0 && uri_str.charAt(uri_str.length()-1)!='/'){
			uri_str = uri_str+'/';
			Log.d("Aptoide-ManageRepo", "repo uri: "+uri_str);
		}
		if(!uri_str.startsWith("http://")){
			uri_str = "http://"+uri_str;
			Log.d("Aptoide-ManageRepo", "repo uri: "+uri_str);
		}
		return uri_str;
	}
	
	@Override
	public void finish() {
		if(update){
			Intent intent = new Intent();
			intent.putExtra("update", update);
			setResult(Activity.RESULT_OK, intent);
		}else if(redraw){
			Intent intent = new Intent();
			intent.putExtra("redraw", redraw);
			setResult(Activity.RESULT_OK, intent);
		}
		super.finish();
	}
	
private returnStatus checkServerConnection(String uri, String user, String pwd){
		
		int result;
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		           
		DefaultHttpClient mHttpClient = getThreadSafeClient();
		
//		DefaultHttpClient mHttpClient = Threading.getThreadSafeHttpClient();
		
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
		
        HttpGet mHttpGet = new HttpGet(uri+"/info.xml");
        
        SharedPreferences sPref = this.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		String myid = sPref.getString("myId", "NoInfo");
		String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
        
//        mHttpGet.setHeader("User-Agent", "aptoide-" + this.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));
        
        try {
        	if(user != null && pwd != null){
        		URL mUrl = new URL(uri);
        		mHttpClient.getCredentialsProvider().setCredentials(
        				new AuthScope(mUrl.getHost(), mUrl.getPort()),
        				new UsernamePasswordCredentials(user, pwd));
        	}
        	
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			
			Header[] azz = mHttpResponse.getHeaders("Location");
			if(azz.length > 0){
				String newurl = azz[0].getValue();

				mHttpGet = null;
				mHttpGet = new HttpGet(newurl);
				
				if(user != null && pwd != null){
	        		URL mUrl = new URL(newurl);
	        		mHttpClient.getCredentialsProvider().setCredentials(
	        				new AuthScope(mUrl.getHost(), mUrl.getPort()),
	        				new UsernamePasswordCredentials(user, pwd));
	        	}
				
				mHttpResponse = null;
				mHttpResponse = mHttpClient.execute(mHttpGet);
			}

			result = mHttpResponse.getStatusLine().getStatusCode();
			
			
			if(result == 200){
				return returnStatus.OK;
			}else if (result == 401){
				return returnStatus.BAD_LOGIN;
			}else{
				return returnStatus.FAIL;
			}
		} catch (ClientProtocolException e) { return returnStatus.EXCEPTION;} 
		catch (IOException e) { return returnStatus.EXCEPTION;}
		catch (IllegalArgumentException e) { return returnStatus.EXCEPTION;}
		catch (Exception e) {return returnStatus.EXCEPTION;	}
	}

public static DefaultHttpClient getThreadSafeClient() {
    DefaultHttpClient client = new DefaultHttpClient();
    ClientConnectionManager mgr = client.getConnectionManager();
    HttpParams params = client.getParams();
 
    client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, 
            mgr.getSchemeRegistry()), params);
 
    return client;
}
	
}
