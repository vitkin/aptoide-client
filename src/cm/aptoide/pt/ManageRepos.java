/**
 * ManageRepos,		part of Aptoide
 * 
 * from v3.0 Copyright (C) 2012  Duarte Silveira
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of ManageRepo from Aptoide's earlier versions with 
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

package cm.aptoide.pt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayListRepos;
import cm.aptoide.pt.data.display.ViewDisplayRepo;
import cm.aptoide.pt.data.model.ViewLogin;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.util.Constants;

/**
 * ManageRepos, interface class to manage and display the details
 * 			of managed repositories
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManageRepos extends ListActivity{
	
	private ViewDisplayListRepos repos = null;
	
	private HashMap<Integer, ViewDisplayRepo> reposInserting;
	
	private SimpleAdapter reposListAdapter;
	
	private ReposManager reposManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataIsBound = false;
	
	private boolean handlingMyappRepos = false;

	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			Log.v("Aptoide-AppInfo", "Connected to ServiceData");
	        
			
			try {
				serviceDataCaller.callRegisterReposObserver(serviceDataCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			handleIncomingIntent(getIntent());
			getReposList();
			
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataCaller = null;
			serviceDataIsBound = false;
			
			Log.v("Aptoide-AppInfo", "Disconnected from ServiceData");
		}
	};
	
	private AIDLReposInfo.Stub serviceDataCallback = new AIDLReposInfo.Stub() {

		@Override
		public void updateReposBasicInfo() throws RemoteException {
			interfaceTasksHandler.sendEmptyMessage(EnumReposInfoTasks.UPDATE_REPOS_INFO.ordinal());			
		}

		@Override
		public void insertedRepo(int repoHashid) throws RemoteException {
			reposInserting.remove(repoHashid);
			interfaceTasksHandler.sendEmptyMessage(EnumReposInfoTasks.UPDATE_REPOS_INFO.ordinal());				
		}
		
	};
	
	private Handler interfaceTasksHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
        	EnumReposInfoTasks task = EnumReposInfoTasks.reverseOrdinal(msg.what);
        	switch (task) {
        		case NO_MANAGED_REPOS:
        			askAddDefaultRepo();
        			break;
        	
				case UPDATE_REPOS_INFO:
					getReposList();
					break;
	
				default:
					break;
			}
        }
	};
    
    
    
    private class ReposManager{
    	private ExecutorService reposThreadPool;
    	
    	public ReposManager(){
    		reposThreadPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void addRepo(ViewRepository repo){
        	reposThreadPool.execute(new AddRepo(repo));
        }
    	
    	public void removeRepo(int repoHashid){
    		reposThreadPool.execute(new RemoveRepo(repoHashid));
    	}
    	
    	public void setInUseRepo(int repoHashid){
    		reposThreadPool.execute(new SetInUseRepo(repoHashid));
    	}
    	
    	public void unsetInUseRepo(int repoHashid){
    		reposThreadPool.execute(new UnsetInUseRepo(repoHashid));
    	}
    	
    	public void removeLogin(int repoHashid){
    		reposThreadPool.execute(new RemoveLogin(repoHashid));
    	}
    	
    	public void updateLogin(ViewRepository repo){
    		reposThreadPool.execute(new UpdateLogin(repo));
    	}
    	
    	private class AddRepo implements Runnable{
    		ViewRepository repo;
    		
    		public AddRepo(ViewRepository repo){
    			this.repo = repo;
    		}
    		
			@Override
			public void run() {
				try {
					serviceDataCaller.callLoadingRepos();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					serviceDataCaller.callAddRepo(repo);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	
    	private class RemoveRepo implements Runnable{
    		int repoHashid;
    		
    		public RemoveRepo(int repoHashid){
    			this.repoHashid = repoHashid;
    		}
    		
			@Override
			public void run() {
				boolean noUsableRepos = true;
				for (Map<String, Object> repo: repos.getList()) {
					if((Boolean)repo.get(Constants.KEY_REPO_IN_USE)){
						noUsableRepos = false;
					}
				}
				if(noUsableRepos){	
					try {
						serviceDataCaller.callNoRepos();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						serviceDataCaller.callLoadingRepos();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					serviceDataCaller.callRemoveRepo(repoHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		
    	}
    	
    	private class SetInUseRepo implements Runnable{
    		int repoHashid;
    		
    		public SetInUseRepo(int repoHashid){
    			this.repoHashid = repoHashid;
    		}
    		
			@Override
			public void run() {
				try {
					serviceDataCaller.callLoadingRepos();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					serviceDataCaller.callSetInUseRepo(repoHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	
    	private class UnsetInUseRepo implements Runnable{
    		int repoHashid;
    		
    		public UnsetInUseRepo(int repoHashid){
    			this.repoHashid = repoHashid;
    		}
    		
			@Override
			public void run() {
				boolean noUsableRepos = true;
				for (Map<String, Object> repo: repos.getList()) {
					if((Boolean)repo.get(Constants.KEY_REPO_IN_USE)){
						noUsableRepos = false;
					}
				}
				if(noUsableRepos){	
					try {
						serviceDataCaller.callNoRepos();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						serviceDataCaller.callLoadingRepos();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					serviceDataCaller.callUnsetInUseRepo(repoHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	
    	private class RemoveLogin implements Runnable{
    		int repoHashid;
    		
    		public RemoveLogin(int repoHashid){
    			this.repoHashid = repoHashid;
    		}

			@Override
			public void run() {
				try {
					serviceDataCaller.callRemoveLogin(repoHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
    	}
    	
    	private class UpdateLogin implements Runnable{
    		ViewRepository repo;
    		
    		public UpdateLogin(ViewRepository repo){
    			this.repo = repo;
    		}

			@Override
			public void run() {
				try {
					serviceDataCaller.callUpdateLogin(repo);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
    	}
    }
    
    
    private void getReposList(){
        try {
			repos = serviceDataCaller.callGetRepos();
			Log.d("Aptoide-ManageRepo", "Repos: "+repos);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if((repos == null || repos.getList().size() == 0) && !handlingMyappRepos){
        	interfaceTasksHandler.sendEmptyMessage(EnumReposInfoTasks.NO_MANAGED_REPOS.ordinal());
        }else{
        	initReposList();
        }
    }
    
    private void toggleDisplayRepoInUse(int repoHashid){
    	boolean repoInUse = true;

    	for (Map<String, Object> repo: repos.getList()) {
    		if(repoHashid == (Integer) repo.get(Constants.KEY_REPO_HASHID)){
    			repoInUse = !(Boolean)repo.get(Constants.KEY_REPO_IN_USE);
    			repo.put(Constants.KEY_REPO_IN_USE, repoInUse);
    			break;
    		}
    	}
    	if(repos.getHashMap().get(repoHashid).getInUse() != repoInUse){
    		if(repoInUse){
    			reposManager.setInUseRepo(repoHashid);
    		}else{
    			reposManager.unsetInUseRepo(repoHashid);
    		}
    	}
    }
    
    private void removeDisplayRepo(int repoHashid){
    	for (Map<String, Object> repo: repos.getList()) {
			if(repoHashid == (Integer) repo.get(Constants.KEY_REPO_HASHID)){
				repos.getList().remove(repo);
				break;
			}
		}

    	reposManager.removeRepo(repoHashid);
    }
    
    private void addDisplayRepo(ViewRepository repo){
    	reposManager.addRepo(repo);
    	ViewDisplayRepo newDisplayRepo = new ViewDisplayRepo(repo.getHashid(), repo.getUri(), true, 0);
    	reposInserting.put(newDisplayRepo.getRepoHashid(), newDisplayRepo);
    	repos.getList().add(newDisplayRepo.getDiplayMap());
    }
    
	
	private void askAddDefaultRepo(){
		final String uri = Constants.APPS_REPO;
		AlertDialog alrt = new AlertDialog.Builder(this).create();
		alrt.setTitle(getString(R.string.repos));
		alrt.setIcon(android.R.drawable.ic_dialog_alert);
		alrt.setMessage(getString(R.string.add_apps_repo_confirm) + uri);
		alrt.setButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  addDisplayRepo(new ViewRepository(uri));
		    	  initReposList();	
		    	  return;
		      } }); 
		alrt.setButton2(getText(R.string.no), new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  initReposList();		    	  
		    	  return;
		      }});
		alrt.show();
	}
	
	
	
	private String updt_repo;
	private AlertDialog alert;
	private AlertDialog alert2;
	private AlertDialog alert3;
	private Context ctx;
	private AlertDialog alrt = null;
	
	private enum returnStatus {OK, LOGIN_REQUIRED, BAD_LOGIN, FAIL, EXCEPTION};
	private enum popupOptions {EDIT_REPO, REMOVE_REPO};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_repos);
		
		this.ctx = this;
		
		this.reposInserting = new HashMap<Integer, ViewDisplayRepo>();
		
		this.reposManager = new ReposManager();
		
		if(!serviceDataIsBound){
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}
		
		registerForContextMenu(this.getListView());
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleIncomingIntent(intent);
		super.onNewIntent(intent);
	}
	
	private void handleIncomingIntent(Intent incomingIntent){
		if(incomingIntent.getExtras() != null && incomingIntent.getExtras().getBoolean(Constants.MYAPP_NEW_REPOS_WAITING)){
			Log.d("Aptoide-ManageRepos", "incoming Myapp Repos");
			handlingMyappRepos = true;
			receiveMyappRepos();
		}
	}
	
	private void receiveMyappRepos(){
		try {
			final ViewDisplayListRepos myappRepos = serviceDataCaller.callGetWaitingMyappRepos();
			
			//TODO checkServerConnection
		
			final CharSequence[] reposArray = new CharSequence[myappRepos.getList().size()];
			for(int i=0; i<myappRepos.getList().size(); i++){
					reposArray[i] = (String) myappRepos.getList().get(i).get(Constants.KEY_REPO_URI);
			}
	
			final ArrayList<String> addList = new ArrayList<String>();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.add_repo_choose));
			builder.setIcon(android.R.drawable.ic_menu_add);
			builder.setMultiChoiceItems(reposArray, null, new DialogInterface.OnMultiChoiceClickListener() {
	            		public void onClick(DialogInterface dialog,int whichButton, boolean isChecked) {
	            		        if(isChecked){
	            		        	addList.add(reposArray[whichButton].toString());
	            		        }else{
	            		        	addList.remove(reposArray[whichButton].toString());
	            		        }
	            		 }
	         }); 
			builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int	whichButton) {
	            			for (String repoUri : addList) {
								addDisplayRepo(new ViewRepository(repoUri));
							}
	            			alert.dismiss();
	            			handlingMyappRepos = false;
	            			refreshReposList();
	            		}
	        });
			builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int whichButton) {
	            			alert.dismiss();
	            			handlingMyappRepos = false;
	            			finish();
	            		}
	        }); 
			alert = builder.create();
			alert.show();
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isRepoManaged(String uri){
		if(repos.getHashMap().containsKey(uri.hashCode())){
			Log.d("Aptoide-ManageRepo", "Repo already exists");
			return true;
		}else{
			String existingUri;
			for (ViewDisplayRepo existingRepo : repos.getHashMap().values()) {
				existingUri = existingRepo.getUri();
				if(uri.length()== (existingUri.length()-1)){
					if(uri.equals(existingUri.substring(0, existingUri.length()-1))){
						Log.d("Aptoide-ManageRepo", "Repo equal to existant one but without final forward slash");
						return true;
					}
				}else if(uri.length()== (existingUri.length()-8)){ 
					if(uri.equals(existingUri.substring(7, existingUri.length()-1))){
						Log.d("Aptoide-ManageRepo", "Repo equal to existant one but without initial http:// and the final forward slash");
						return true;
					}
				}else{
					uri = uri+".bazaarandroid.com/";
					if(uri.equals(existingUri.substring(7, existingUri.length()))){
						Log.d("Aptoide-ManageRepo", "Repo equal to existant one but without initial http:// and without .bazaarandroid.com extension");
						return true;
					}
					if(uri.equals(existingUri.substring(7, existingUri.length()-1))){
						Log.d("Aptoide-ManageRepo", "Repo equal to existant one but without initial http:// , without .bazaarandroid.com extension, and the final forward slash");
						return true;
					}
				}
			}
		}
		Log.d("Aptoide-ManageRepo", "Repo is new");
		return false;
	}

	private String uriCheck(String uri_str) {
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

	
	private returnStatus checkServerConnection(String uri, String user, String pwd){
		Log.d("Aptoide-ManageRepo", "checking connection for: "+uri+"  with credentials: "+user+" "+pwd);
		
		int result;
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		           
		DefaultHttpClient mHttpClient = new DefaultHttpClient(httpParameters);
		
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
        
//        SharedPreferences sPref = this.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
//		String myid = sPref.getString("myId", "NoInfo");
//		String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
        
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
	
	
	private void validateRepo(final String originalUriString, final boolean editMode){

		final ViewDisplayRepo originalRepo;
		
		LayoutInflater li = LayoutInflater.from(ctx); 
		View view = li.inflate(R.layout.addrepo, null);

		final TextView sec_msg = (TextView) view.findViewById(R.id.sec_msg);
		final TextView sec_msg2 = (TextView) view.findViewById(R.id.sec_msg2);
		
		final EditText sec_user = (EditText) view.findViewById(R.id.sec_user);
		final EditText sec_pwd = (EditText) view.findViewById(R.id.sec_pwd);
		
		final EditText uri = (EditText) view.findViewById(R.id.edit_uri);
		
		
		final CheckBox sec = (CheckBox) view.findViewById(R.id.secure_chk);
		sec.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					sec_user.setEnabled(true);
					sec_pwd.setEnabled(true);
				}else{
					sec_user.setEnabled(false);
					sec_pwd.setEnabled(false);
				}
			}
		});
		
		
		Builder p = new AlertDialog.Builder(ctx).setView(view);
		alrt = p.create();
		CharSequence actionButtonString;
		if(editMode){
			
			originalRepo = repos.getRepo(originalUriString.hashCode());
			if(originalRepo.requiresLogin()){
				sec.setChecked(true);
				sec_user.setText(originalRepo.getLogin().getUsername());																																																				 
				sec_pwd.setText(originalRepo.getLogin().getPassword());
			}else{
				sec.setChecked(false);
			}
			
			alrt.setIcon(android.R.drawable.ic_menu_edit);
			alrt.setTitle(getText(R.string.edit_repo));
			actionButtonString = getText(R.string.edit);
		}else{
			
			originalRepo = null;
			sec.setChecked(false);
			
			alrt.setIcon(android.R.drawable.ic_menu_add);
			alrt.setTitle(getText(R.string.add_repo));
			actionButtonString = getText(R.string.add);
		}

		alrt.setButton(actionButtonString, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String uriString = uri.getText().toString();
				
				String user = null;
				String pwd = null;
				
				if(sec.isChecked()){
					user = sec_user.getText().toString();
					pwd = sec_pwd.getText().toString();
				}
					
				Message msg = new Message();
				uriString = uriCheck(uriString);
				sec_msg.setVisibility(View.GONE);
				sec_msg2.setVisibility(View.GONE);
				
				returnStatus result = checkServerConnection(uriString, user, pwd);
				switch (result) {
					case OK:
						Log.d("Aptoide-ManageRepo", "return ok");
						msg.obj = 0;
						if(isRepoManaged(uriString) && ((originalRepo != null && originalRepo.requiresLogin())?(originalRepo.getLogin().getUsername().equals(user) && originalRepo.getLogin().getPassword().equals(pwd)):true)){
							Toast.makeText(ctx, "Repo "+ uriString+ " already exists.", 5000).show();
//							finish();
						}else{
							ViewRepository newRepo = new ViewRepository(uriString);
							if(isRepoManaged(uriString)){
								if(user != null && pwd != null){
									reposManager.removeLogin(newRepo.getHashid());
								}else{
									newRepo.setLogin(new ViewLogin(user, pwd));
									reposManager.updateLogin(newRepo);
								}
							}else{
								if(user != null && pwd != null){
									newRepo.setLogin(new ViewLogin(user, pwd));	
								}
						    	if(originalUriString != null){
						    		removeDisplayRepo(originalUriString.hashCode());
						    	}
						    	addDisplayRepo(newRepo);
						    	refreshReposList();
							}
							
							alrt.dismiss();
						}
						break;
					
					case LOGIN_REQUIRED:
						Log.d("Aptoide-ManageRepo", "return login_required");
						sec_msg2.setText(getText(R.string.login_required));
						sec_msg2.setVisibility(View.VISIBLE);
						msg.obj = 1;
						
						break;
						
					case BAD_LOGIN:
						Log.d("Aptoide-ManageRepo", "return bad_login");
						sec_msg2.setText(getText(R.string.bad_login));
						sec_msg2.setVisibility(View.VISIBLE);
						msg.obj = 1;
						break;
						
					case FAIL:
						Log.d("Aptoide-ManageRepo", "return fail");
						uriString = uriString.substring(0, uriString.length()-1)+".bazaarandroid.com/";
						Log.d("Aptoide-ManageRepo", "repo uri: "+uriString);
						msg.obj = 1;
						break;

					default:
						Log.d("Aptoide-ManageRepo", "return exception");
						uriString = uriString.substring(0, uriString.length()-1)+".bazaarandroid.com/";
						Log.d("Aptoide-ManageRepo", "repo uri: "+uriString);
						msg.obj = 1;
						break;
				}
				if(result.equals(returnStatus.FAIL) || result.equals(returnStatus.EXCEPTION)){
					returnStatus result2 = checkServerConnection(uriString, user, pwd);
					switch (result2) {
						case OK:
							Log.d("Aptoide-ManageRepo", "return ok");
							msg.obj = 0;
							if(isRepoManaged(uriString) && ((originalRepo != null && originalRepo.requiresLogin())?(originalRepo.getLogin().getUsername().equals(user) && originalRepo.getLogin().getPassword().equals(pwd)):true)){
								Toast.makeText(ctx, "Repo "+ uriString+ " already exists.", 5000).show();
//								finish();
							}else{
								ViewRepository newRepo = new ViewRepository(uriString);
								if(isRepoManaged(uriString)){
									if(user != null && pwd != null){
										reposManager.removeLogin(newRepo.getHashid());
									}else{
										newRepo.setLogin(new ViewLogin(user, pwd));
										reposManager.updateLogin(newRepo);
									}
								}else{
									if(user != null && pwd != null){
										newRepo.setLogin(new ViewLogin(user, pwd));	
									}
							    	if(originalUriString != null){
							    		removeDisplayRepo(originalUriString.hashCode());
							    	}
							    	addDisplayRepo(newRepo);
							    	refreshReposList();
								}
								
								alrt.dismiss();
							}
							break;
						
						case LOGIN_REQUIRED:
							Log.d("Aptoide-ManageRepo", "return login_required");
							sec_msg2.setText(getText(R.string.login_required));
							sec_msg2.setVisibility(View.VISIBLE);
							msg.obj = 1;
							
							break;
							
						case BAD_LOGIN:
							Log.d("Aptoide-ManageRepo", "return bad_login");
							sec_msg2.setText(getText(R.string.bad_login));
							sec_msg2.setVisibility(View.VISIBLE);
							msg.obj = 1;
							break;
							
						case FAIL:
							Log.d("Aptoide-ManageRepo", "return fail");
							sec_msg.setText(getText(R.string.cant_connect));
							sec_msg.setVisibility(View.VISIBLE);
							msg.obj = 1;
								
							break;

						default:
							Log.d("Aptoide-ManageRepo", "return exception");
							msg.obj = 1;	
							break;
					}
				}				
				invalidRepo.sendMessage(msg);
			} });

		alrt.setButton2(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alrt.dismiss();
			} });
		alrt.show();
		if(originalUriString != null){
			uri.setText(originalUriString);
		}
	}
	
	private Handler invalidRepo = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if((Integer)msg.obj == 1)
				alrt.show();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
//		refreshReposList();
	}

	private void initReposList(){
        reposListAdapter = new SimpleAdapter(this, repos.getList(), R.layout.row_repo
        								,new String[] {Constants.KEY_REPO_HASHID, Constants.KEY_REPO_URI, Constants.KEY_REPO_IN_USE, Constants.KEY_REPO_SIZE}
        								,new int[] {R.id.repo_hashid, R.id.uri, R.id.in_use, R.id.size});
        reposListAdapter.setViewBinder(new ReposListBinder());
        setListAdapter(reposListAdapter);
	}
	
	private void refreshReposList(){
		reposListAdapter.notifyDataSetChanged();
	}



	class ReposListBinder implements ViewBinder		//TODO may need some improvements
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.ImageView")){
				ImageView tmpr = (ImageView)view;	
				boolean inUse = Boolean.parseBoolean(textRepresentation);
				if(inUse){
					tmpr.setImageResource(R.drawable.btn_check_on);
				}else{
					tmpr.setImageResource(R.drawable.btn_check_off);
				}
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
		}
	}
	
	
	
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		final int repoHashid = Integer.parseInt(view.getTag().toString());
		Log.d("Aptoide-ManageRepo", "Onclick position: "+position+" repoHashid: "+repoHashid);
		if(!reposInserting.containsKey(repoHashid)){
			toggleDisplayRepoInUse(repoHashid);
			refreshReposList();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, EnumOptionsMenu.ADD_REPO.ordinal(),1,R.string.new_repo)
			.setIcon(android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, EnumOptionsMenu.EDIT_REPO.ordinal(), 2, R.string.edit_repo)
			.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, EnumOptionsMenu.REMOVE_REPO.ordinal(), 3, R.string.remove_repo)
		.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		Log.d("Aptoide-ManageRepos", "oncreateContext view: "+view.toString());
		final int repoHashid = Integer.parseInt(view.findViewById(R.id.repo_hashid).getTag().toString());
		if(!reposInserting.containsKey(repoHashid)){
			super.onCreateContextMenu(menu, view, menuInfo);
			
			menu.setHeaderTitle(getString(R.string.options));  
			menu.add(0, popupOptions.EDIT_REPO.ordinal(), 0, getString(R.string.edit));  
			menu.add(0, popupOptions.REMOVE_REPO.ordinal(), 0, getString(R.string.remove)); 
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		TextView selectedView = (TextView) ((View)(info.targetView)).findViewById(R.id.uri);
		final String repo_selected = selectedView.getText().toString();
		popupOptions popupOption = popupOptions.values()[item.getItemId()];
		switch (popupOption) {
		case EDIT_REPO:
			validateRepo(repo_selected, true);
			refreshReposList();
			break;

		case REMOVE_REPO:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.remove_repo));
			builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			builder.setMessage(getString(R.string.remove_repo_confirm) + " " + repo_selected + " ?");
			builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int	whichButton) {
                			removeDisplayRepo(repo_selected.hashCode());
                			alert3.dismiss();
            				refreshReposList();
                		}
            });
			builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int whichButton) {
                			alert3.dismiss();
                			return;
                		}
            }); 
			alert3 = builder.create();
			alert3.show();
			
			
			break;
			
		default:
			break;
		}
		
		
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		EnumOptionsMenu option = EnumOptionsMenu.reverseOrdinal(item.getItemId());
		
		CharSequence[] reposArray = new CharSequence[repos.getList().size()-reposInserting.size()];
		int j=0;
		for(int i=0; i<repos.getList().size(); i++){
			if(!reposInserting.containsKey(repos.getList().get(i).get(Constants.KEY_REPO_HASHID))){
				reposArray[j] = (String) repos.getList().get(i).get(Constants.KEY_REPO_URI);
				j++;
			}
		}
		
		switch(option){
			case ADD_REPO:
				validateRepo(null, false);
				break;
			
				
				
			case REMOVE_REPO:
				if(reposArray.length == 0){
					return true;
				}
				final Vector<Integer> remList = new Vector<Integer>();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.remove_repo_choose));
				builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
				builder.setMultiChoiceItems(reposArray, null, new DialogInterface.OnMultiChoiceClickListener() {
	                		public void onClick(DialogInterface dialog,int whichButton, boolean isChecked) {
	                		        if(isChecked){
	                		        	remList.addElement((Integer)(repos.getList().get(whichButton).get(Constants.KEY_REPO_HASHID)));
	                		        }else{
	                		        	remList.removeElement((Integer)(repos.getList().get(whichButton).get(Constants.KEY_REPO_HASHID)));
	                		        }
	                		 }
	             }); 
				builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
	                		public void onClick(DialogInterface dialog, int	whichButton) {
	                			for (Integer repoHashid : remList) {
									removeDisplayRepo(repoHashid);
								}
	                			alert.dismiss();
	                			refreshReposList();
	                		}
	            });
				builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
	                		public void onClick(DialogInterface dialog, int whichButton) {
	                			alert.dismiss();
	                			return;
	                		}
	            }); 
				alert = builder.create();
				alert.show();
				break;
				
				
				
			case EDIT_REPO:		
				if(reposArray.length == 0){
					return true;
				}		
				AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
				builder2.setTitle(getString(R.string.edit_repo_choose));
				builder2.setIcon(android.R.drawable.ic_menu_edit);
				builder2.setSingleChoiceItems(reposArray, -1, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
							updt_repo = (String)(repos.getList().get(whichButton).get(Constants.KEY_REPO_URI));
					}
				}); 
				builder2.setPositiveButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int	whichButton) {
						alert2.dismiss();
						validateRepo(updt_repo, true);
						return;
					}
				});
				builder2.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						alert2.dismiss();
						return;
					}
				}); 
				alert2 = builder2.create();
				alert2.show();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	@Override
	public void finish() {
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);
		}
		super.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
