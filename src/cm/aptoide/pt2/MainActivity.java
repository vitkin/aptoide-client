package cm.aptoide.pt2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt2.Server.State;
import cm.aptoide.pt2.adapters.InstalledAdapter;
import cm.aptoide.pt2.adapters.ViewPagerAdapter;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.contentloaders.SimpleCursorLoader;
import cm.aptoide.pt2.services.MainService;
import cm.aptoide.pt2.services.MainService.LocalBinder;
import cm.aptoide.pt2.util.Algorithms;
import cm.aptoide.pt2.util.Base64;
import cm.aptoide.pt2.util.RepoUtils;
import cm.aptoide.pt2.views.ViewApk;

import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	private final static int AVAILABLE_LOADER = 0;
	private final static int INSTALLED_LOADER = 1;
	private final static int UPDATES_LOADER   = 2;

	private final Dialog.OnClickListener addRepoListener = new Dialog.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			String url = ((EditText) alertDialog.findViewById(R.id.edit_uri))
					.getText().toString();
			dialogAddStore(url, null, null);
		}

	};

	private void dialogAddStore(final String url, final String username,
			final String password) {
		final ProgressDialog pd = new ProgressDialog(mContext);
		pd.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					addStore(url, username, password);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							refreshAvailableList(true);
						}
					});

				}

			}
		}).start();
	}

	private View addStoreButton;

	private final OnClickListener addStoreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			showAddStoreDialog();
		}

	};

	private AlertDialog alertDialog;

	private View alertDialogView;
	private HashMap<String, Long> serversToParse = new HashMap<String, Long>();
	private AvailableListAdapter availableAdapter;
	private ListView availableListView;
	private Loader<Cursor> availableLoader;
	private View availableView;
	private long category_id;
	private long category2_id;
	private final ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MainActivity.this.service = ((LocalBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	private Database db;

	private ListDepth depth = ListDepth.STORES;

	private ListView featuredView;

	private InstalledAdapter installedAdapter;

	private Loader<Cursor> installedLoader;
	private ListView installedView;

	private CheckBox joinStores;
	private boolean joinStores_boolean = false;
	private Context mContext;

	private TextView pb;
	private boolean refreshClick = true;
	private final Dialog.OnClickListener searchStoresListener = new Dialog.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			Uri uri = Uri.parse("http://m.aptoide.com/more/toprepos");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

	};

	private MainService service;
	private final BroadcastReceiver statusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (depth.equals(ListDepth.STORES)) {
				availableLoader.forceLoad();
				System.out.println("Status broadcast received");
			}
		}
	};
	private long store_id;

	private CursorAdapter updatesAdapter;

	private Loader<Cursor> updatesLoader;

	private final BroadcastReceiver updatesReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			installedLoader.forceLoad();
			updatesLoader.forceLoad();
			if (!depth.equals(ListDepth.STORES)) {
				Long server_id = intent.getExtras().getLong("server");
				if (refreshClick && server_id == store_id) {
					refreshClick = false;
					availableView.findViewById(R.id.refresh_view_layout)
							.setVisibility(View.VISIBLE);
					availableView
							.findViewById(R.id.refresh_view_layout)
							.findViewById(R.id.refresh_view)
							.startAnimation(
									AnimationUtils.loadAnimation(mContext,
											android.R.anim.fade_in));
				}
			}
		}
	};

	private ListView updatesView;

	public class AddStoreCredentialsListener implements
			DialogInterface.OnClickListener {
		private String url;
		private View dialog;

		public AddStoreCredentialsListener(String string,
				View credentialsDialogView) {
			this.url = string;
			this.dialog = credentialsDialogView;
		}

		@Override
		public void onClick(DialogInterface arg0, int which) {
			dialogAddStore(url, ((EditText) dialog.findViewById(R.id.username))
					.getText().toString(),
					((EditText) dialog.findViewById(R.id.password)).getText()
							.toString());
		}

	}

	public void getAllRepoStatus() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String repos = "";
				String hashes = "";
				Cursor cursor = db.getStores();
				int i = 0;
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					String repo;
					if (i > 0) {
						repos = repos + ",";
						hashes = hashes + ",";
					}
					repo = cursor.getString(1);
					repo = RepoUtils.split(repo);
					repos = repos + repo;
					hashes = hashes + cursor.getString(2);
					i++;
					serversToParse.put(repo, cursor.getLong(0));

				}
				cursor.close();

				if (!serversToParse.isEmpty()) {

					String url = "https://www.aptoide.com/webservices/listRepositoryChange/"
							+ repos + "/" + hashes + "/json";
					System.out.println(url);
					try {
						HttpURLConnection connection = (HttpURLConnection) new URL(
								url).openConnection();
						connection.connect();
						int rc = connection.getResponseCode();
						if (rc == 200) {
							String line = null;
							BufferedReader br = new BufferedReader(
									new java.io.InputStreamReader(connection
											.getInputStream()));
							StringBuilder sb = new StringBuilder();
							while ((line = br.readLine()) != null)
								sb.append(line + '\n');

							JSONObject json = new JSONObject(sb.toString());

							JSONArray array = json.getJSONArray("listing");

							for (int o = 0; o != array.length(); o++) {
								boolean b = Boolean.parseBoolean(array
										.getJSONObject(o).getString(
												"hasupdates"));
								long id = serversToParse.get(array
										.getJSONObject(o).getString("repo"));
								if (b) {
									service.parseServer(db, db.getServer(id));
								} else {
									service.parseTop(db, db.getServer(id));
								}

							}

						}
						connection.disconnect();

					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	protected void addStore(String uri_str, String username, String password) {

		if (uri_str.contains("http//")) {
			uri_str = uri_str.replaceFirst("http//", "http://");
		}

		if (uri_str.length() != 0
				&& uri_str.charAt(uri_str.length() - 1) != '/') {
			uri_str = uri_str + '/';
			Log.d("Aptoide-ManageRepo", "repo uri: " + uri_str);
		}
		if (!uri_str.startsWith("http://")) {
			uri_str = "http://" + uri_str;
			Log.d("Aptoide-ManageRepo", "repo uri: " + uri_str);
		}
		if (username != null && username.contains("@")) {
			try {
				password = Algorithms.computeSHA1sum(password);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		final int response = checkServerConnection(uri_str, username, password);
		final String uri = uri_str;
		switch (response) {
		case 0:
			service.addStore(db, uri, username, password);
			break;
		case 401:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showAddStoreCredentialsDialog(uri);
				}
			});

			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(mContext, response + "", Toast.LENGTH_LONG)
							.show();
					showAddStoreDialog();
				}
			});
			break;
		}

	}

	private int checkServerConnection(final String string,
			final String username, final String password) {
		try {

			HttpURLConnection client = (HttpURLConnection) new URL(string
					+ "info.xml").openConnection();
			if (username != null && password != null) {
				String basicAuth = "Basic "
						+ new String(Base64.encode(
								(username + ":" + password).getBytes(),
								Base64.NO_WRAP));
				client.setRequestProperty("Authorization", basicAuth);
			}
			client.setConnectTimeout(10000);
			client.setReadTimeout(10000);
			if (client.getContentType().equals("application/xml")) {
				return 0;
			} else {
				return client.getResponseCode();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void getInstalled() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<PackageInfo> system_installed_list = getPackageManager()
						.getInstalledPackages(0);
				List<String> database_installed_list = db.getStartupInstalled();
				for (PackageInfo pkg : system_installed_list) {
					if (!database_installed_list.contains(pkg.packageName)) {
						try {
							ViewApk apk = new ViewApk();
							apk.setApkid(pkg.packageName);
							apk.setVercode(pkg.versionCode);
							apk.setVername(pkg.versionName);
							apk.setName((String) pkg.applicationInfo
									.loadLabel(getPackageManager()));
							db.insertInstalled(apk);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {

						}
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						installedLoader = getSupportLoaderManager().initLoader(
								INSTALLED_LOADER, null, MainActivity.this);
						installedView.setAdapter(installedAdapter);
						getUpdates();
					}
				});
			}
		}).start();
	}

	private void getUpdates() {
		updatesLoader = getSupportLoaderManager().initLoader(UPDATES_LOADER,null, MainActivity.this);
		updatesView.setAdapter(updatesAdapter);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final ProgressDialog pd;
		switch (item.getItemId()) {
		case 0:
			pd = new ProgressDialog(mContext);
			pd.show();
			pd.setCancelable(false);
			new Thread(new Runnable() {

				private boolean result = false;

				@Override
				public void run() {
					try {
						result = service
								.deleteStore(db, ((AdapterContextMenuInfo) item
										.getMenuInfo()).id);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();
								if (result) {
									refreshAvailableList(false);
									installedLoader.forceLoad();
								} else {
									Toast.makeText(mContext,
											"Unable to delete store. Parsing",
											Toast.LENGTH_LONG).show();
								}

							}
						});
					}
				}
			}).start();
			break;
		case 1:
			pd = new ProgressDialog(mContext);
			pd.show();
			pd.setCancelable(false);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						service.parseServer(db, db
								.getServer(((AdapterContextMenuInfo) item
										.getMenuInfo()).id));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();
								refreshAvailableList(false);
							}
						});

					}
				}
			}).start();

			break;
		}

		return super.onContextItemSelected(item);
	}

	LinearLayout breadcrumbs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		db = Database.getInstance(mContext);
		Intent i = new Intent(mContext, MainService.class);
		startService(i);
		bindService(i, conn, Context.BIND_AUTO_CREATE);
		registerReceiver(updatesReceiver, new IntentFilter("update"));
		registerReceiver(statusReceiver, new IntentFilter("status"));
		setContentView(R.layout.activity_aptoide);
		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		ViewPager pager = (ViewPager) findViewById(R.id.viewpager);

		featuredView = new ListView(mContext);

		availableView = LayoutInflater.from(mContext).inflate(R.layout.available_page, null);
		breadcrumbs = (LinearLayout) availableView.findViewById(R.id.breadcrumb_container);
		installedView = new ListView(mContext);
		updatesView = new ListView(mContext);

		availableListView = (ListView) availableView.findViewById(R.id.available_list);
		availableView.findViewById(R.id.refresh_view_layout)
				.findViewById(R.id.refresh_view)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						refreshClick = true;
						availableView.findViewById(R.id.refresh_view_layout)
								.setVisibility(View.GONE);
						refreshAvailableList(false);

					}
				});

		joinStores = (CheckBox) availableView.findViewById(R.id.join_stores);
		joinStores.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					depth = ListDepth.CATEGORY1;
				} else {
					depth = ListDepth.STORES;
				}
				joinStores_boolean = isChecked;
				if (isChecked) {
					addBreadCrumb("All Stores", depth);
				} else {
					breadcrumbs.removeAllViews();
				}
				refreshAvailableList(true);
			}
		});

		availableAdapter = new AvailableListAdapter(mContext, null,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		installedAdapter = new InstalledAdapter(mContext, null,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, db);
		updatesAdapter = new InstalledAdapter(mContext, null,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, db);

		pb = (TextView) availableView.findViewById(R.id.loading_pb);
		addStoreButton = availableView.findViewById(R.id.add_store);
		addStoreButton.setOnClickListener(addStoreListener);

		availableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i ;
				switch (depth) {
				case STORES:
					depth = ListDepth.CATEGORY1;
					store_id = id;
					break;
				case CATEGORY1:
					String category = ((Cursor) parent
							.getItemAtPosition(position)).getString(1);
					if (category.equals("Top Apps") || category.equals("Most Recent Apps")) {
						depth = ListDepth.TOPAPPS;
						System.out.println("TopApps");
					} else {
						depth = ListDepth.CATEGORY2;
					}
					category_id = id;
					break;
				
				case CATEGORY2:
					depth = ListDepth.APPLICATIONS;
					category2_id = id;
					break;
				case TOPAPPS:
					i = new Intent(MainActivity.this, ApkInfo.class);
					i.putExtra("_id", id);
					i.putExtra("top", true);
					startActivity(i);
					return;
				case APPLICATIONS:
					i = new Intent(MainActivity.this, ApkInfo.class);
					i.putExtra("_id", id);
					i.putExtra("top", false);
					startActivity(i);
					return;
				default:
					return;
				}
				addBreadCrumb(((Cursor) parent.getItemAtPosition(position)).getString(1), depth);
				refreshAvailableList(true);
			}
		});
		installedView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				Intent i = new Intent(MainActivity.this, ApkInfo.class);
				i.putExtra("_id", id);
				i.putExtra("top", false);
				startActivity(i);
			}
		});
		
		updatesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				Intent i = new Intent(MainActivity.this, ApkInfo.class);
				i.putExtra("_id", id);
				i.putExtra("top", false);
				startActivity(i);
			}
		});
//		LoaderManager.enableDebugLogging(true);
		availableLoader = getSupportLoaderManager().initLoader(AVAILABLE_LOADER, null, this);
		
		ArrayList<View> views = new ArrayList<View>();
		views.add(featuredView);
		views.add(availableView);
		views.add(installedView);
		views.add(updatesView);

		pager.setAdapter(new ViewPagerAdapter(mContext, views));
		indicator.setViewPager(pager, 1);
		refreshAvailableList(true);
		getInstalled();
		getAllRepoStatus();

	}

	private class BreadCrumb {
		ListDepth depth;
		int i;

		public BreadCrumb(ListDepth depth, int i) {
			this.depth = depth;
			this.i = i;
		}
	}

	protected void addBreadCrumb(String itemAtPosition, ListDepth depth2) {
		if (itemAtPosition.contains("http://")) {
			itemAtPosition = itemAtPosition.split("http://")[1];
			itemAtPosition = itemAtPosition.split(".store")[0];
		}
		Button bt = (Button) LayoutInflater.from(mContext).inflate(
				R.layout.breadcrumb, null);
		bt.setText(itemAtPosition);
		bt.setTag(new BreadCrumb(depth, breadcrumbs.getChildCount() + 1));
		System.out.println(breadcrumbs.getChildCount() + 1);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				depth = ((BreadCrumb) v.getTag()).depth;
				breadcrumbs.removeViews(((BreadCrumb) v.getTag()).i,
						breadcrumbs.getChildCount()
								- ((BreadCrumb) v.getTag()).i);
				refreshAvailableList(true);
			}
		});
		breadcrumbs.addView(bt, new LinearLayout.LayoutParams(-2,
				LayoutParams.WRAP_CONTENT, 1f));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Integer tag = (Integer) ((AdapterContextMenuInfo) menuInfo).targetView
				.getTag();
		if (tag != null && tag == 1) {
			menu.add(0, 1, 0, "reparse");
		}
		menu.add(0, 0, 0, "remove");

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		SimpleCursorLoader a = null;
		switch (id) {
		case AVAILABLE_LOADER:
			a = new SimpleCursorLoader(mContext) {

				@Override
				public Cursor loadInBackground() {
					switch (depth) {
					case STORES:
						return db.getStores();
					case CATEGORY1:
						return db.getCategory1(store_id, joinStores_boolean);
					case CATEGORY2:
						return db.getCategory2(category_id, store_id,
								joinStores_boolean);
					case APPLICATIONS:
						return db.getApps(category2_id, store_id,
								joinStores_boolean);
					case TOPAPPS:
						return db.getTopApps(category_id, store_id,
								joinStores_boolean);
					default:
						return null;
					}
				}
			};
			return a;
		case INSTALLED_LOADER:
			a = new SimpleCursorLoader(mContext) {

				@Override
				public Cursor loadInBackground() {
					return db.getInstalledApps();
				}
			};
			return a;
		case UPDATES_LOADER:
			a = new SimpleCursorLoader(mContext) {

				@Override
				public Cursor loadInBackground() {
					return db.getUpdates();
				}
			};
			return a;
		default:
			break;
		}
		return null;

	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		unregisterReceiver(updatesReceiver);
		unregisterReceiver(statusReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!depth.equals(ListDepth.STORES)) {
				if (depth.equals(ListDepth.TOPAPPS)) {
					depth = ListDepth.CATEGORY1;
				} else {
					depth = ListDepth.values()[depth.ordinal() - 1];
				}
				removeLastBreadCrumb();
				refreshAvailableList(true);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void removeLastBreadCrumb() {
		breadcrumbs.removeViewAt(breadcrumbs.getChildCount() - 1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		((CursorAdapter) availableListView.getAdapter()).swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case AVAILABLE_LOADER:
			availableAdapter.swapCursor(data);
			break;
		case INSTALLED_LOADER:
			installedAdapter.swapCursor(data);
			break;
		case UPDATES_LOADER:
			updatesAdapter.swapCursor(data);
			break;
		default:
			break;
		}
		pb.setVisibility(View.GONE);
		if (availableListView.getAdapter().getCount() > 1) {
			joinStores.setVisibility(View.VISIBLE);
		} else {
			joinStores.setVisibility(View.INVISIBLE);
		}

	}

	private void refreshAvailableList(boolean setAdapter) {
		if (depth.equals(ListDepth.STORES)) {
			availableView.findViewById(R.id.add_store_layout).setVisibility(
					View.VISIBLE);
			registerForContextMenu(availableListView);
		} else {
			unregisterForContextMenu(availableListView);
			availableListView.setLongClickable(false);
			if (!joinStores_boolean) {
				availableView.findViewById(R.id.add_store_layout)
						.setVisibility(View.GONE);
			} else if (depth.equals(ListDepth.CATEGORY2)
					|| depth.equals(ListDepth.APPLICATIONS)
					|| depth.equals(ListDepth.TOPAPPS)) {
				availableView.findViewById(R.id.add_store_layout)
						.setVisibility(View.GONE);
			} else if (depth.equals(ListDepth.CATEGORY1)) {
				availableView.findViewById(R.id.add_store_layout)
						.setVisibility(View.VISIBLE);
			}

		}
		availableView.findViewById(R.id.refresh_view_layout).setVisibility(
				View.GONE);
		refreshClick = true;
		availableAdapter.changeCursor(null);
		pb.setVisibility(View.VISIBLE);
		if (setAdapter) {
			availableListView.setAdapter(availableAdapter);
		}
		availableLoader.forceLoad();
	}

	private void showAddStoreDialog() {
		alertDialogView = LayoutInflater.from(mContext).inflate(
				R.layout.add_store_dialog, null);
		alertDialog = new AlertDialog.Builder(mContext)
				.setView(alertDialogView).create();
		alertDialog.setTitle(getString(R.string.new_store));
		alertDialog.setButton(Dialog.BUTTON_NEGATIVE,
				getString(R.string.new_store), addRepoListener);
		alertDialog.setButton(Dialog.BUTTON_POSITIVE,
				getString(R.string.search_for_stores), searchStoresListener);
		((EditText) alertDialogView.findViewById(R.id.edit_uri))
				.setText("savou.store.aptoide.com");
		alertDialog.show();
	}

	private void showAddStoreCredentialsDialog(String string) {
		View credentialsDialogView = LayoutInflater.from(mContext).inflate(
				R.layout.add_store_creddialog, null);
		AlertDialog credentialsDialog = new AlertDialog.Builder(mContext)
				.setView(credentialsDialogView).create();
		credentialsDialog.setTitle(getString(R.string.new_store));
		credentialsDialog.setButton(Dialog.BUTTON_NEUTRAL,
				getString(R.string.new_store), new AddStoreCredentialsListener(
						string, credentialsDialogView));
		credentialsDialog.show();
	}

	public class AvailableListAdapter extends CursorAdapter {

		ImageLoader loader;

		public AvailableListAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			loader = new ImageLoader(mContext, db);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			switch (depth) {
			case STORES:
				((TextView) view.findViewById(R.id.store_name)).setText(cursor
						.getString(1));
				((TextView) view.findViewById(R.id.store_dwn_number))
						.setText(cursor.getString(6));
				if (cursor.getString(6).equals(State.FAILED.name())
						|| cursor.getString(6).equals(State.PARSED.name())) {
					view.setTag(1);
				}
				break;
			case TOPAPPS:
			case APPLICATIONS:
				ViewHolder holder = (ViewHolder) view.getTag();
				if (holder == null) {
					holder = new ViewHolder();
					holder.name = (TextView) view.findViewById(R.id.app_name);
					holder.icon = (ImageView) view.findViewById(R.id.app_icon);
					holder.vername = (TextView) view
							.findViewById(R.id.installed_versionname);
					 holder.downloads= (TextView) view.findViewById(R.id.downloads);
			            holder.rating= (RatingBar) view.findViewById(R.id.stars);
					view.setTag(holder);
				}
				holder.name.setText(cursor.getString(1));
				loader.DisplayImage(cursor.getLong(3), cursor.getString(4),
						holder.icon, context, depth == ListDepth.TOPAPPS ? true
								: false);
				holder.vername.setText(cursor.getString(2));
				 try{
			        	holder.rating.setRating(Float.parseFloat(cursor.getString(5)));	
			        }catch (Exception e) {
			        	holder.rating.setRating(0);
					}
				 holder.downloads.setText(cursor.getString(6));
				break;
			case CATEGORY1:
				((TextView) view.findViewById(R.id.category_name))
						.setText(cursor.getString(1));
				break;
			case CATEGORY2:
				((TextView) view.findViewById(R.id.category_name))
						.setText(cursor.getString(1));
				break;
			default:
				break;
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View v = null;
			switch (depth) {
			case STORES:
				v = LayoutInflater.from(context).inflate(R.layout.stores_row,
						null);
				break;
			case CATEGORY1:
				v = LayoutInflater.from(context).inflate(R.layout.catg_list,
						null);
				break;
			case CATEGORY2:
				v = LayoutInflater.from(context).inflate(R.layout.catg_list,
						null);
				break;
			case TOPAPPS:
			case APPLICATIONS:
				v = LayoutInflater.from(context)
						.inflate(R.layout.app_row, null);
				break;
			default:
				break;
			}
			Animation animation = AnimationUtils.loadAnimation(mContext,
					android.R.anim.fade_in);
			v.startAnimation(animation);
			return v;
		}
	}

	static class ViewHolder {
		ImageView icon;
		TextView name;
		TextView vername;
		RatingBar rating;
		TextView downloads;
	}

}
