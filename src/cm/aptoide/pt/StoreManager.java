package cm.aptoide.pt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
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
	ArrayList<String> repos;
	private ArrayList<Integer> cleanRepos = new ArrayList<Integer>();
	
	
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
				int repo_id= c.getInt(0);
				final boolean checked = c.getInt(10)==1;
				System.out.println(c.getInt(10));
				if(number_apks>0){
					((TextView) v.findViewById(R.id.numberapks)).setText(getText(R.string.number_apks)+" "+number_apks);
				}else{
					((TextView) v.findViewById(R.id.numberapks)).setText("No information");
				}
				((CheckBox) v.findViewById(R.id.CheckBox01)).setChecked(checked);
				((CheckBox) v.findViewById(R.id.CheckBox01)).setTag(repo_id);
				((CheckBox) v.findViewById(R.id.CheckBox01)).setOnClickListener(new View.OnClickListener() {
					
					

					public void onClick(View v) {
						int repo_id = (Integer)v.getTag();
						if(checked){
							db.setRepoInUse(repo_id,0);
							cleanRepos.add(repo_id);
							
						}else{
							if(cleanRepos.contains(repo_id)){
								cleanRepos.remove((Object)repo_id);
							}
							update=true;
							
							db.setRepoInUse(repo_id,1);
						}
						
						redraw();
					}
				});
				
				
			}
			
		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(storeListener);
		
		redraw();
		registerForContextMenu(lv);
		if(getIntent().hasExtra("newrepo")){
			repos = (ArrayList<String>) getIntent().getSerializableExtra("newrepo");
			for(final String uri2 : repos){
				
				AlertDialog alertDialog = new AlertDialog.Builder(context).create();
				alertDialog.setMessage("Add server: "+uri2+" ?");
				alertDialog.setButton(Dialog.BUTTON_POSITIVE,"yes", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						pd=new ProgressDialog(context);
						pd.setMessage(getString(R.string.please_wait));
						pd.show();
						new Thread(new Runnable() {
							private boolean containsRepo;
							private String uri;
							public void run() {
								try {
									
									this.uri = (checkServer(serverCheck(uri2), username, password));
									containsRepo = serverContainsRepo(uri);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if(uri!=null&&!containsRepo){
										System.out.println(uri);
										db.insertRepository(uri,username,password);
									}
									
									runOnUiThread(new Runnable() {
										
										public void run() {
											redraw();
											pd.dismiss();
											if(uri==null||containsRepo){
												runOnUiThread(new Runnable() {
													
													public void run() {
														Toast.makeText(context, "Repo insertion failed", 1).show();
													}
												});
											}
										}
									});
									
								}
							}
						}).start();
						update=true;
					}
				});
				alertDialog.show();
			}
			
		}else if(getIntent().hasExtra("norepos")){
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setMessage(getString(R.string.myrepo_alrt));
			alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Yes", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					pd=new ProgressDialog(context);
					pd.setMessage(getString(R.string.please_wait));
					pd.show();
					new Thread(new Runnable() {
						private boolean containsRepo;
						private String uri;
						public void run() {
							try {
								
								this.uri = (checkServer(serverCheck("http://apps.bazaarandroid.com/"), username, password));
								containsRepo = serverContainsRepo(uri);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if(uri!=null&&!containsRepo){
									System.out.println(uri);
									db.insertRepository(uri,username,password);
								}
								
								runOnUiThread(new Runnable() {
									
									public void run() {
										redraw();
										pd.dismiss();
										if(uri==null||containsRepo){
											runOnUiThread(new Runnable() {
												
												public void run() {
													Toast.makeText(context, "Repo insertion failed", 1).show();
												}
											});
										}
									}
								});
								
							}
						}
					}).start();
					update=true;
				}
			});
			
			
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.btn_no),new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alertDialog.show();
		}
		
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
					runOnUiThread(new Runnable() {
						public void run() {
							redraw();
						}
					});
				}
			}
		}).start();
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0,0,0,getString(R.string.menu_add_repo)).setIcon(android.R.drawable.ic_menu_add);
		menu.add(0,1,0,getString(R.string.menu_rem_repo)).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0,2,0,getString(R.string.menu_edit_repo)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onPrepareOptionsMenu(menu);
	}
	CharSequence[] b;
	CharSequence[] b2;
	String updt_repo;
	AlertDialog alert2;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==0){
			alertDialogView = LayoutInflater.from(context).inflate(R.layout.addrepo, null);
			final CheckBox sec = (CheckBox) alertDialogView.findViewById(R.id.secure_chk);
			final EditText sec_user = (EditText) alertDialogView.findViewById(R.id.sec_user);
			final EditText sec_pwd = (EditText) alertDialogView.findViewById(R.id.sec_pwd);
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
			
			alertDialog = new AlertDialog.Builder(this).setView(alertDialogView).create();
			alertDialog.setButton(Dialog.BUTTON_POSITIVE,getString(R.string.btn_add_repo), addRepoListener);
			alertDialog.show();
			
		}else if(item.getItemId()==1){
			final Vector<Integer> rem_lst = new Vector<Integer>();
			Cursor c = db.getRepositories();
			b = new CharSequence[c.getCount()];
			int i=0;
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
				b[i] = c.getString(1);
				i++;
			}
			c.close();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.repo_del_msg2));
			builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			builder.setMultiChoiceItems(b, null, new DialogInterface.OnMultiChoiceClickListener() {
                		public void onClick(DialogInterface dialog,int whichButton, boolean isChecked) {
                		        if(isChecked){
                		        	rem_lst.addElement(db.getRepoId(b[whichButton].toString()));
                		        }else{
                		        	rem_lst.removeElement(db.getRepoId(b[whichButton].toString()));
                		        }
                		        System.out.println(whichButton);
                		 }
             }); 
			builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int	whichButton) {
                			final ProgressDialog pd = new ProgressDialog(context);
                			pd.setCancelable(false);
                			pd.setMessage(getString(R.string.please_wait));
                			pd.show();
                			new Thread(new Runnable() {
								public void run() {
									try{
										for(int i : rem_lst){
			                				db.beginTransation();
			            					db.removeRepo(i,true);
			            					db.endTransation();
			                			}					
									}catch (Exception e) {
										e.printStackTrace();
									}finally{
										runOnUiThread(new Runnable() {
											public void run() {
												redraw();
												pd.dismiss();
											}
										});
									}
								}
							}).start();
                			redraw=true;
                		}
            });
			builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int whichButton) {
                			return;
                		}
            }); 
			AlertDialog alert = builder.create();
			alert.show();
		}else if(item.getItemId()==2){
			Cursor c = db.getRepositories();
			b2 = new CharSequence[c.getCount()];
			int i=0;
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
				b2[i] = c.getString(1);
				i++;
			}
			c.close();
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle(getString(R.string.repo_edt_msg1));
			builder2.setIcon(android.R.drawable.ic_menu_edit);
			builder2.setSingleChoiceItems(b2, -1, new DialogInterface.OnClickListener() {
				

				public void onClick(DialogInterface dialog, int which) {
						updt_repo = b2[which].toString();
				}
			}); 
			builder2.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					alert2.dismiss();
					editRepo(updt_repo);
					return;
				}
			});
			builder2.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					return;
				}
			}); 
			alert2 = builder2.create();
			alert2.show();
		}
		return super.onOptionsItemSelected(item);
	}
	String uri;
	String password = "";
	String username = ""; 
	AlertDialog alertDialog;
	
	private void editRepo(final String repo){
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.addrepo, null);
		Builder p = new AlertDialog.Builder(this).setView(view);
		final AlertDialog alrt = p.create();
		final EditText uri = (EditText) view.findViewById(R.id.edit_uri);
		uri.setText(repo);
		uri.setEnabled(false);
		final EditText sec_user = (EditText) view.findViewById(R.id.sec_user);
		final EditText sec_pwd = (EditText) view.findViewById(R.id.sec_pwd);
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
		
		String[] logins = null; 
		logins = db.getLogin(repo);
		
		if(logins != null){
			sec.setChecked(true);
			sec_user.setText(logins[0]);																																																				 
			sec_pwd.setText(logins[1]);
		}else{
			sec.setChecked(false);
		}
		alrt.setIcon(android.R.drawable.ic_menu_add);
		alrt.setTitle(getString(R.string.repo_edt_msg2));
		alrt.setButton(getText(R.string.btn_done), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String new_repo = uri.getText().toString();
				if(sec.isChecked()){
					db.addLogin(sec_user.getText().toString(), sec_pwd.getText().toString(), new_repo);
				}else{
					db.disableLogin(new_repo);
				}
				update = true;
				redraw();
			} });

		alrt.setButton2(getText(R.string.btn_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} });
		//alert2.dismiss();
		alrt.show();
	}
	
	private OnItemClickListener storeListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			int repo_id=(int) arg0.getItemIdAtPosition(arg2);
			if(((CheckBox) arg1.findViewById(R.id.CheckBox01)).isChecked()){
				db.setRepoInUse(repo_id,0);
				cleanRepos.add(repo_id);
				
			}else{
				if(cleanRepos.contains(repo_id)){
					cleanRepos.remove((Object)repo_id);
				}
				db.setRepoInUse(repo_id,1);
				update=true;
			}
			
			redraw();
		}
		
	};
	
	
	
	private OnClickListener addRepoListener = new OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			password = ((TextView) alertDialogView.findViewById(R.id.sec_pwd)).getText().toString();
			username = ((TextView) alertDialogView.findViewById(R.id.sec_user)).getText().toString();
			
			uri = serverCheck(((TextView) alertDialogView.findViewById(R.id.edit_uri)).getText().toString());
			
			pd=new ProgressDialog(context);
			pd.setCancelable(false);
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
							System.out.println(uri);
							db.insertRepository(uri,username,password);

						}
						
						runOnUiThread(new Runnable() {
							
							public void run() {
								redraw();
								pd.dismiss();
								if(uri==null||containsRepo){
									alertDialog.show();
								}
								
								
								
								
								
							}
						});
						
						
					}
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
				c.close();
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
        File newxmlfile = new File(Environment.getExternalStorageDirectory()+"/.aptoide/servers.xml");
        try{
                newxmlfile.createNewFile();
        }catch(IOException e){
                Log.e("IOException", "exception in createNewFile() method");
        }
        //we have to bind the new file with a FileOutputStream
        FileOutputStream fileos = null;        
        try{
                fileos = new FileOutputStream(newxmlfile);
        }catch(FileNotFoundException e){
                Log.e("FileNotFoundException", "can't create FileOutputStream");
        }
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        try {
                //we set the FileOutputStream as output for the serializer, using UTF-8 encoding
                        serializer.setOutput(fileos, "UTF-8");
                        //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
                        serializer.startDocument(null, Boolean.valueOf(true));
                        //set indentation option
//                        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                        //start a tag called "root"
                        serializer.startTag(null, "myapp");
                        //i indent code just to have a view similar to xml-tree
                        for(int i = 0; i!=adapter.getCount();i++){
                        	serializer.startTag(null, "newserver");
//                            serializer.endTag(null, "child1");
                           
                            serializer.startTag(null, "server");
                            serializer.text(((Cursor) lv.getItemAtPosition(i)).getString(1));
                            serializer.endTag(null, "server");
                   
                            //write some text inside <child3>
                            
                            serializer.endTag(null, "newserver");
                        }
                                
                               
                        serializer.endTag(null, "myapp");
                        serializer.endDocument();
                        //write xml data into the FileOutputStream
                        serializer.flush();
                        //finally we close the file stream
                        fileos.close();
                        
//                        <newserver><server>http://islafenice.bazaarandroid.com/</server></newserver></myapp>
                       
//                TextView tv = (TextView)this.findViewById(R.id.result);
//                        tv.setText("file has been created on SD card");
                } catch (Exception e) {
                        Log.e("Exception","error occurred while creating xml file");
                }
    

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
		if(arg1.getCount()>0){
			findViewById(android.R.id.empty).setVisibility(View.GONE);
		}
		
		
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(!cleanRepos.isEmpty()){
				pd=new ProgressDialog(context);
				pd.setMessage(getString(R.string.please_wait));
				pd.show();
				new Thread(new Runnable() {

					public void run() {
						try{
							for(int i = 0; i!=cleanRepos.size();i++){
								db.resetRepo(cleanRepos.get(i));
							}
						}catch (Exception e) {

						}finally{
							runOnUiThread(new Runnable() {

								public void run() {
									pd.dismiss();
									redraw=true;
									finish();

								}
							});

						}
					}
				}).start();
			}else{
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
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
		
		new Thread(new Runnable() {
			
			public void run() {
				generateXML();
				
			}
		}).start();
		
		super.finish();
		
	}
	
private returnStatus checkServerConnection(String uri, String user, String pwd){
		
		int result;
		
		           
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
        
        mHttpGet.setHeader("User-Agent", "aptoide-" + this.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));
        
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
    HttpConnectionParams.setConnectionTimeout(params, 5000);
    HttpConnectionParams.setSoTimeout(params, 5000);
 
    client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, 
            mgr.getSchemeRegistry()), params);
 
    return client;
}
	
}
