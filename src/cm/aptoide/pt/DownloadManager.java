/*
 * DownloadManager, part of Aptoide
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

import android.content.*;
import android.content.DialogInterface.OnDismissListener;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cm.aptoide.com.actionbarsherlock.view.MenuItem;
import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoader;
import cm.aptoide.com.viewpagerindicator.TitlePageIndicator;
import cm.aptoide.pt.adapters.DownloadedListAdapter;
import cm.aptoide.pt.adapters.DownloadingListAdapter;
import cm.aptoide.pt.adapters.NotDownloadedListAdapter;
import cm.aptoide.pt.adapters.ViewPagerAdapter;
import cm.aptoide.pt.download.DownloadInfo;
import cm.aptoide.pt.download.Utils;
import cm.aptoide.pt.download.event.DownloadStatusEvent;
import cm.aptoide.pt.events.BusProvider;
import cm.aptoide.pt.services.AIDLServiceDownloadManager;
import cm.aptoide.pt.services.ServiceManagerDownload;
import cm.aptoide.pt.sharing.DialogShareOnFacebook;
import cm.aptoide.pt.util.quickaction.ActionItem;
import cm.aptoide.pt.util.quickaction.EnumQuickActions;
import cm.aptoide.pt.util.quickaction.QuickAction;
import cm.aptoide.pt.views.EnumDownloadStatus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * DownloadManager
 *
 * @author dsilveira
 *
 */
public class DownloadManager extends FragmentActivity /*SherlockActivity */{
	private boolean isRunning = false;

	private LinearLayout downloading;
	private DownloadingListAdapter downloadingAdapter;

	private LinearLayout downloaded;
	private DownloadedListAdapter downloadedAdapter;

	private LinearLayout notDownloaded;
	private NotDownloadedListAdapter notDownloadedAdapter;

	private Button exitButton;

	private TextView noDownloads;

	private AIDLServiceDownloadManager serviceManager = null;

	private boolean serviceManagerIsBound = false;

    private ArrayList<DownloadInfo> notOnGoingArrayList;
    private ServiceConnection serviceManagerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
//			serviceManager = AIDLServiceDownloadManager.Stub.asInterface(service);
//			serviceManagerIsBound = true;
//
//			Log.v("Aptoide-DownloadManager", "Connected to ServiceDownloadManager");
//
//			try {
//				serviceManager.callRegisterDownloadManager(serviceManagerCallback);
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//
//			continueLoading();

            dm = ((ServiceManagerDownload.LocalBinder) service).getService();


            onGoingArrayList = dm.getOngoingDownloads();
            notOnGoingArrayList = dm.getNotOngoingDownloads();
//            sort(onGoingArrayList);
            onGoingadapter = new MyAdapter(DownloadManager.this, onGoingArrayList);
            notOngoingAdapter = new MyAdapter(DownloadManager.this, notOnGoingArrayList);

            sort(onGoingArrayList);

            onGoingList.setAdapter(onGoingadapter);
            notOngoingList.setAdapter(notOngoingAdapter);

        }



		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceManagerIsBound = false;
			serviceManager = null;

			Log.v("Aptoide-DownloadManager", "Disconnected from ServiceDownloadManager");
		}
	};

    ServiceManagerDownload dm;
    ArrayList<DownloadInfo> onGoingArrayList;
    MyAdapter onGoingadapter;
    MyAdapter notOngoingAdapter;


    private AIDLDownloadManager.Stub serviceManagerCallback = new AIDLDownloadManager.Stub() {

		@Override
		public void updateDownloadStatus(int status) throws RemoteException {
        	if (serviceManagerIsBound) {
    			EnumDownloadStatus task = EnumDownloadStatus.reverseOrdinal(status);
				switch (task) {
					case RESTARTING:
					case FAILED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
							notDownloadedAdapter.updateList(serviceManager.callGetDownloadsFailed());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case DOWNLOADING:
					case PAUSED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case RESUMING:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case STOPPED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case COMPLETED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
							downloadedAdapter.updateList(serviceManager.callGetDownloadsCompleted());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					default:
						break;
				}
			}
        	interfaceTasksHandler.sendEmptyMessage(status);
		}

	};


	private Handler interfaceTasksHandler = new Handler(){

		public void handleMessage(Message msg) {
			switch (EnumDownloadStatus.reverseOrdinal(msg.what)) {
				case RESTARTING:
				case FAILED:
					if (downloadingAdapter.isEmpty()) {
						downloading.setVisibility(View.GONE);
					} else {
						downloading.setVisibility(View.VISIBLE);
					}
					if(downloadedAdapter.isEmpty()){
						downloaded.setVisibility(View.GONE);
					}
					notDownloaded.setVisibility(View.VISIBLE);
					break;

				case DOWNLOADING:
				case PAUSED:
				case RESUMING:
				case STOPPED:
					downloading.setVisibility(View.VISIBLE);
					if(notDownloadedAdapter.isEmpty()){
						notDownloaded.setVisibility(View.GONE);
					}
					if(downloadedAdapter.isEmpty()){
						downloaded.setVisibility(View.GONE);
					}
					break;

				case COMPLETED:
					if (downloadingAdapter.isEmpty()) {
						downloading.setVisibility(View.GONE);
					} else {
						downloading.setVisibility(View.VISIBLE);
					}
					if(notDownloadedAdapter.isEmpty()){
						notDownloaded.setVisibility(View.GONE);
					}
					downloaded.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}

		}
	};
    private ListView onGoingList;
    private ListView notOngoingList;


    private void prePopulateLists(){
		try {

			if(serviceManager.callAreDownloadsOngoing()){
				noDownloads.setVisibility(View.GONE);
				downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
//				if(!downloadingAdapter.isEmpty()){
					downloading.setVisibility(View.VISIBLE);
//				}else{
//					downloading.setVisibility(View.GONE);
//				}
			}else{
				downloading.setVisibility(View.GONE);
			}
			if(serviceManager.callAreDownloadsCompleted()){
				noDownloads.setVisibility(View.GONE);
				downloadedAdapter.updateList(serviceManager.callGetDownloadsCompleted());
//				if (!downloadedAdapter.isEmpty()) {
					downloaded.setVisibility(View.VISIBLE);
//				}else{
//					downloaded.setVisibility(View.GONE);
//				}
			}else{
				downloaded.setVisibility(View.GONE);
			}
			if(serviceManager.callAreDownloadsFailed()){
				noDownloads.setVisibility(View.GONE);
				notDownloadedAdapter.updateList(serviceManager.callGetDownloadsFailed());
//				if(!notDownloadedAdapter.isEmpty()){
					notDownloaded.setVisibility(View.VISIBLE);
//				}else{
//					notDownloaded.setVisibility(View.GONE);
//				}
			}else{
				notDownloaded.setVisibility(View.GONE);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


    @Subscribe
    public void onDownloadTick(DownloadInfo download){

        try{
            ListView list = onGoingList;
            int start = list.getFirstVisiblePosition();
            for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
                if (download == list.getItemAtPosition(i)) {
                    View view = list.getChildAt(i - start);
                    list.getAdapter().getView(i, view, list);
                    break;
                }
        }catch (Exception e){
            ListView list = notOngoingList;
            int start = list.getFirstVisiblePosition();
            for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
                if (download == list.getItemAtPosition(i)) {
                    View view = list.getChildAt(i - start);
                    list.getAdapter().getView(i, view, list);
                    break;
                }
        }

    }

    @Subscribe public void onDownloadEvent(DownloadStatusEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                onGoingadapter.clear();
//                notOngoingAdapter.clear();

                onGoingArrayList.clear();
                onGoingArrayList.addAll(dm.getOngoingDownloads());

                notOnGoingArrayList.clear();
                notOnGoingArrayList.addAll(dm.getNotOngoingDownloads());

                sort(onGoingArrayList);

                onGoingadapter.notifyDataSetChanged();
                notOngoingAdapter.notifyDataSetChanged();
            }
        });


    }

    private void sort(ArrayList<DownloadInfo> list) {


        Collections.sort(list, new Comparator<DownloadInfo>() {
            @Override
            public int compare(DownloadInfo lhs, DownloadInfo rhs) {
                return lhs.getStatusState().getEnumState().ordinal() - rhs.getStatusState().getEnumState().ordinal();  //To change body of implemented methods use File | Settings | File Templates.
            }
        });


    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		AptoideThemePicker.setAptoideTheme(this);
		super.onCreate(savedInstanceState);



        BusProvider.getInstance().register(this);
        setContentView(R.layout.download_manager);
        ViewPager vp = (ViewPager) findViewById(R.id.downloadManagerViewPager);

        ArrayList<View> views = new ArrayList<View>();
        onGoingList = new ListView(this);
        notOngoingList = new ListView(this);

        views.add(onGoingList);
        views.add(notOngoingList);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, views);
        vp.setAdapter(adapter);
        adapter.setTitles(new String[]{getString(R.string.downloading_apps), getString(R.string.downloaded_apps)});
        TitlePageIndicator pageIndicator = (TitlePageIndicator) findViewById(R.id.downloadManagerPageIndicator);
        pageIndicator.setViewPager(vp);



        registerForContextMenu(onGoingList);

        notOngoingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((DownloadInfo)parent.getItemAtPosition(position)).download();
            }
        });

//		if(!isRunning){
//			isRunning = true;
//
//			if(!serviceManagerIsBound){
	    bindService(new Intent(this, ServiceManagerDownload.class), serviceManagerConnection, Context.BIND_AUTO_CREATE);
//	    	}
//
//			setContentView(R.layout.download_manager);
////			getSupportActionBar().setIcon(R.drawable.brand_padding);
////			getSupportActionBar().setTitle(getString(R.string.download_manager));
////			getSupportActionBar().setHomeButtonEnabled(true);
////			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//			downloading = (LinearLayout) findViewById(R.id.downloading_apps);
//			downloading.setVisibility(View.GONE);
//
//			downloaded = (LinearLayout) findViewById(R.id.downloaded_apps);
//			downloaded.setVisibility(View.GONE);
//
//			notDownloaded = (LinearLayout) findViewById(R.id.failed_apps);
//			notDownloaded.setVisibility(View.GONE);
//
//			exitButton = (Button) findViewById(R.id.exit);
//			exitButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					finish();
//				}
//			});
//
//			noDownloads = (TextView) findViewById(R.id.no_downloads);
//			noDownloads.setVisibility(View.VISIBLE);
//		}

	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 0, 0, "Pause");
        menu.add(0, 1, 1, "Resume");

    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){

            case 0:
                onGoingadapter.getItem(info.position).pause();
                break;
            case 1:
                onGoingadapter.getItem(info.position).download();
                break;

        }

        return super.onContextItemSelected(item);
    }



    public static class DownloadingRowViewHolder{
        TextView app_name;
        ImageView app_icon;
        ProgressBar app_download_progress;
        TextView app_progress;
        TextView app_speed;
        TextView app_eta;
        ImageView manageDownloadsButton;

    }

    public class MyAdapter extends ArrayAdapter<DownloadInfo> {


        private final ActionItem playItem;
        private final ActionItem pauseItem;
        private final ActionItem stopItem;
        private final ActionItem deleteItem;
        private final ActionItem shareItem;

        public MyAdapter(Context context, ArrayList<DownloadInfo> list) {
            super(context, 0, list);
            playItem = new ActionItem(EnumQuickActions.PLAY.ordinal(), getString(R.string.resume), context.getResources().getDrawable(R.drawable.ic_media_play));
            pauseItem = new ActionItem(EnumQuickActions.PAUSE.ordinal(), getString(R.string.pause), context.getResources().getDrawable(R.drawable.ic_media_pause));
            stopItem = new ActionItem(EnumQuickActions.STOP.ordinal(), getString(R.string.stop), context.getResources().getDrawable(R.drawable.ic_media_stop));
            deleteItem = new ActionItem(EnumQuickActions.STOP.ordinal(), getString(R.string.clear), context.getResources().getDrawable(R.drawable.ic_menu_close_clear_cancel));
            shareItem = new ActionItem(EnumQuickActions.SHARE.ordinal(), getString(R.string.share), context.getResources().getDrawable(R.drawable.ic_menu_share));
        }


        @Override
        public long getItemId(int position) {
            return getItem(position).getId();  //To change body of implemented methods use File | Settings | File Templates.
        }





        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DownloadingRowViewHolder rowViewHolder;

            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.row_app_downloading, null);
                rowViewHolder = new DownloadingRowViewHolder();
                rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.downloading_name);
                rowViewHolder.app_download_progress = (ProgressBar) convertView.findViewById(R.id.downloading_progress);
                rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.downloading_icon);
                rowViewHolder.app_progress = (TextView) convertView.findViewById(R.id.progress);
                rowViewHolder.app_speed = (TextView) convertView.findViewById(R.id.speed);
                rowViewHolder.app_eta = (TextView) convertView.findViewById(R.id.eta);
                rowViewHolder.manageDownloadsButton = (ImageView) convertView.findViewById(R.id.icon_manage);
                convertView.setTag(rowViewHolder);
            }else{
                rowViewHolder = (DownloadingRowViewHolder) convertView.getTag();
            }

            final DownloadInfo download = getItem(position);

            rowViewHolder.app_name.setText(download.getViewApk().getName()+"  "+download.getViewApk().getVername());
            rowViewHolder.app_progress.setText(download.getPercentDownloaded() + "%");

            rowViewHolder.app_eta.setText(Utils.formatEta(download.getEta(), getString(R.string.time_left)));

            if(download.getPercentDownloaded()==0){
            	 rowViewHolder.app_download_progress.setIndeterminate(true);
            	 rowViewHolder.app_speed.setText(R.string.starting);
             }else{
            	 rowViewHolder.app_download_progress.setIndeterminate(false);
            	 rowViewHolder.app_speed.setText(Utils.formatBits((long) download.getSpeed()) + "ps ");
            }

            switch (download.getStatusState().getEnumState()){
                case ERROR:
                    rowViewHolder.app_speed.setText(getString(R.string.download_failed_due_to) +": "+ download.getFailReason().toString(getContext()));

                    rowViewHolder.app_progress.setVisibility(View.GONE);
                    rowViewHolder.app_eta.setVisibility(View.GONE);
                    break;
                case COMPLETE:
                	rowViewHolder.app_speed.setText(getString(R.string.completed));

                    rowViewHolder.app_download_progress.setVisibility(View.GONE);
                    rowViewHolder.app_progress.setVisibility(View.GONE);
                    rowViewHolder.app_eta.setVisibility(View.GONE);
                    break;
                case PENDING:
                    rowViewHolder.app_speed.setText(getString(R.string.waiting));

                    rowViewHolder.app_eta.setVisibility(View.GONE);
                    break;
                case INACTIVE:
                    rowViewHolder.app_speed.setText(getString(R.string.paused));

                    rowViewHolder.app_eta.setVisibility(View.GONE);
                    break;
                case ACTIVE:
                    rowViewHolder.app_eta.setVisibility(View.VISIBLE);
                    rowViewHolder.app_progress.setVisibility(View.VISIBLE);
                    rowViewHolder.app_download_progress.setVisibility(View.VISIBLE);
                    break;
            }

//            if(download.getProgress() != 0 && download.getProgress() < 99){
//                rowViewHolder.app_download_progress.setIndeterminate(false);
//                rowViewHolder.app_download_progress.setMax(100);
//            }else{
//                rowViewHolder.app_download_progress.setIndeterminate(true);
//            }
            rowViewHolder.app_download_progress.setProgress(download.getPercentDownloaded());
            String iconUrl = download.getViewApk().getIcon();
            ImageLoader.getInstance().displayImage(iconUrl, rowViewHolder.app_icon, (download.getViewApk().getApkid() + "|" + download.getViewApk().getVercode()));

            rowViewHolder.manageDownloadsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuickAction actionBar  = new QuickAction(getContext());

                    switch (download.getStatusState().getEnumState()) {

                    case ACTIVE:
                    	actionBar.addActionItem(pauseItem);
                    	actionBar.addActionItem(stopItem);
                    	break;
                    case PENDING:
                        actionBar.addActionItem(stopItem);
                        break;
                    case COMPLETE:
                    	actionBar.addActionItem(deleteItem);
                    	actionBar.addActionItem(shareItem);
                    	break;
                    case ERROR:
                    	actionBar.addActionItem(playItem);
                    	actionBar.addActionItem(stopItem);
                    	break;
                    default:
                    	actionBar.addActionItem(playItem);
                    	break;
                    }

                    actionBar.show(view);
                    actionBar.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                        @Override
                        public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                            switch (EnumQuickActions.reverseOrdinal(actionId)) {
                                    case PLAY:
                                        download.download();
                                        break;

                                    case PAUSE:
                                        download.pause();
                                        break;

                                    case DELETE:
                                    case STOP:
                                        download.remove();
                                        break;
                                    case SHARE:
                                    	shareOnFacebook(download);
                                    	break;

                                    default:
                                        break;
                            }
                        }





                    });

                }
            });

            return convertView;
        }
    }

    private void shareOnFacebook(DownloadInfo download) {
    	String facebookShareName = download.getViewApk().getName()+"  "+download.getViewApk().getVername();
    	String facebookShareIcon = download.getViewApk().getIcon();
    	String facebookShareMessage = getString(R.string.i_downloaded_to_install, facebookShareName);
    	String facebookShareDescription;
    	String facebookShareStoreLink;
    	if(download.getViewApk().getRepoName().equals("Aptoide")){
    		facebookShareDescription = getString(R.string.visit_and_install_the_best_apps, ApplicationAptoide.MARKETNAME);
    		facebookShareStoreLink = getString(R.string.aptoide_url_topapps);
    	}else{
    		facebookShareDescription = getString(R.string.visit_and_install, download.getViewApk().getRepoName());
    		facebookShareStoreLink = "http://"+download.getViewApk().getRepoName()+".store.aptoide.com";
    	}

    	Log.d("Aptoide-sharing", "NameToPost: "+facebookShareName+", IconToPost: "+facebookShareIcon +", DescriptionToPost: "+facebookShareDescription+", MessageToPost: "+facebookShareMessage+", StoreLinkToPost: "+facebookShareStoreLink);

    	final DialogShareOnFacebook shareFacebook = new DialogShareOnFacebook(this, facebookShareName, facebookShareIcon, facebookShareMessage, facebookShareDescription, facebookShareStoreLink);

    	shareFacebook.setOnDismissListener(new OnDismissListener() {
    		@Override
    		public void onDismiss(DialogInterface dialog) {
    			shareFacebook.dismiss();
    		}
    	});

    	shareFacebook.show();
    }

//	private void continueLoading(){
//		ListView uploadingList = (ListView) findViewById(R.id.downloading_list);
//		downloadingAdapter = new DownloadingListAdapter(this, serviceManager, ImageLoader.getInstance());
//		uploadingList.setAdapter(downloadingAdapter);
//
//		ListView uploadedList = (ListView) findViewById(R.id.downloaded_list);
//		downloadedAdapter = new DownloadedListAdapter(this, ImageLoader.getInstance());
//		uploadedList.setAdapter(downloadedAdapter);
//
//		uploadedList.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				try {
//					serviceManager.callInstallApp(downloadedAdapter.getItem(position));
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
//
//		});
//
//		ListView notUploadedList = (ListView) findViewById(R.id.failed_list);
//		notDownloadedAdapter = new NotDownloadedListAdapter(this, ImageLoader.getInstance());
//		notUploadedList.setAdapter(notDownloadedAdapter);
//		notUploadedList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int position, long positionLong) {
//				try {
//					serviceManager.callRestartDownload(notDownloadedAdapter.getItem(position).hashCode());
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//
//		prePopulateLists();
//		registerForContextMenu(uploadedList);
//	}

	@Override
	protected void onDestroy() {
//		try {
//			serviceManager.callUnregisterDownloadManager();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
		unbindService(serviceManagerConnection);
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
	}


//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//
//		menu.add(0, 0, 0, getString(R.string.export_apk));
//
//		super.onCreateContextMenu(menu, v, menuInfo);
//	}
//
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
//		case 0:
//
//			break;
//
//		default:
//			break;
//		}
//
//		return super.onContextItemSelected(item);
//	}

//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		menu.clear();
//		menu.add(Menu.NONE, 0, 0, R.string.clear_all).setIcon(android.R.drawable.ic_notification_clear_all);
//
//		return super.onPrepareOptionsMenu(menu);
//	}

//	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
		switch (item.getItemId()) {
		case 0:
			Log.d("Aptoide-DownloadManager", "clear all");

			if(downloadedAdapter.getCount()>0 || notDownloadedAdapter.getCount()>0){
				try {
					serviceManager.callClearDownloads();
					downloadedAdapter.updateList(serviceManager.callGetDownloadsCompleted());
					notDownloadedAdapter.updateList(serviceManager.callGetDownloadsFailed());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}

		return true;
	}
}
