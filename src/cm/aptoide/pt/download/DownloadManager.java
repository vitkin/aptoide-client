package cm.aptoide.pt.download;

import android.util.Log;
import cm.aptoide.pt.download.event.DownloadStatusEvent;
import cm.aptoide.pt.download.state.ActiveState;
import cm.aptoide.pt.download.state.PendingState;
import cm.aptoide.pt.events.BusProvider;
import com.squareup.otto.Subscribe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * The download manager keeps track of download objects and moves them around its lists.
 * [singleton]
 * @author Edward Larsson (edward.larsson@gmx.com)
 */
public class DownloadManager {

	/** The singleton instance of this DownloadManager. */
	public static final DownloadManager INSTANCE = new DownloadManager();

	/** List of active download objects. */
	private ArrayList<DownloadInfo> mActiveList;
	/** List of inactive download objects. */
	private ArrayList<DownloadInfo> mInactiveList;
	/** List of pending download objects. */
	private ArrayList<DownloadInfo> mPendingList;
	/** List of completed download objects. */
	private ArrayList<DownloadInfo> mCompletedList;
	/** List of download objects with errors. */
	private ArrayList<DownloadInfo> mErrorList;
	/** List of URLs in the manager. */
	private ArrayList<URL> mURLs;

    public void removeAllActiveDownloads() {
        while(mActiveList.iterator().hasNext()){
            mActiveList.get(0).pause();
        }
    }


    /**
	 * Enum for default file exist behaviors.
	 */
	public enum DefaultFileExistsBehavior {
		/** ASK what to do. */
		ASK,
		/** Rename the file. */
		RENAME,
		/** Replace the file. */
		REPLACE,
		/** Resume the file. */
		RESUME;
	}

	/**
	 * Construct a download manager. Initializes lists.
	 */
	private DownloadManager() {
		mActiveList = new ArrayList<DownloadInfo>();
		mInactiveList = new ArrayList<DownloadInfo>();
		mPendingList = new ArrayList<DownloadInfo>();
		mCompletedList = new ArrayList<DownloadInfo>();
		mErrorList = new ArrayList<DownloadInfo>();
		mURLs = new ArrayList<URL>();

        BusProvider.getInstance().register(this);
	}

    @Subscribe
    public void onDownloadStatus(DownloadStatusEvent event){
        Log.d("TAG", "onDownloadStatus");
        updatePendingList();
    }

	/**
	 * Initialize settings.
	 */
	public void init() {
//		XMLConfigFile file = null;
//		try {
//			file = XMLConfigFile.loadFile();
//			if (file == null) {
				// If the config.xml file does not exist, these settings will be used and written to that file.
				int maxDownloads = 3;
//				DefaultFileExistsBehavior defaultFileExistsBehavior = DefaultFileExistsBehavior.RESUME;
//				String defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory() + File.separator + "Downloads";
//				file = new XMLConfigFile(defaultDirectory, maxDownloads, defaultFileExistsBehavior);
//				file.saveFile();
//			}
//		} catch (Exception e) {
//			DownloadLogger.LOGGER.log(Level.SEVERE, e.toString());
//		}

//		mConfigFile = file;
	}

	/**
	 * Check if there is room in the active list.
	 * @return <tt>true</tt> if there is room, <tt>false</tt> otherwise.
	 */
	private boolean activeListHasRoom() {
		int maxDownloads = 3;
		return maxDownloads == 0 || mActiveList.size() < maxDownloads;
	}

//	/**
//	 * Save settings
//	 * @param configFile The xml config file with the settings.
//	 */
//	public void saveSettings(XMLConfigFile configFile) {
//		mConfigFile = configFile;
//		try {
//			mConfigFile.saveFile();
//		} catch (Exception e) {
//			DownloadLogger.LOGGER.log(Level.SEVERE, e.toString());
//		}
//	}

//	/**
//	 * @return The xml config file with the settings of this download manager.
//	 */
//	public XMLConfigFile getSettings() {
//		return mConfigFile;
//	}

	/**
	 * Tries to add a download object to the active list.
	 * Will not work if the max number of downloads is already reached.
	 * @param downloadInfo The download object to be added to the list.
	 * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
	 */
	public boolean addToActiveList(DownloadInfo downloadInfo) {
		if (activeListHasRoom()) {
			return mActiveList.add(downloadInfo);
		}

		return false;
	}

	/**
	 * @return The default directory for new downloads.
	 */
//	public String getDefaultDirectory() {
//		return mConfigFile.getDefaultDirectory();
//	}

	/**
	 * Tries to add a download object to the inactive list.
	 * @param downloadInfo The download object to be added to the list.
	 * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
	 */
	public boolean addToInactiveList(DownloadInfo downloadInfo) {
		return mInactiveList.add(downloadInfo);
	}

	/**
	 * Tries to add a download object to the pending list.
	 * @param downloadInfo The download object to be added to the list.
	 * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
	 */
	public boolean addToPendingList(DownloadInfo downloadInfo) {
		return mPendingList.add(downloadInfo);
	}

	/**
	 * Tries to add a download object to the completed list.
	 * @param downloadInfo The download object to be added to the list.
	 * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
	 */
	public boolean addToCompletedList(DownloadInfo downloadInfo) {
		return mCompletedList.add(downloadInfo);
	}

	/**
	 * Tries to add a download object to the error list.
	 * @param downloadInfo The download object to be added to the list.
	 * @return <tt>true</tt> if the download could be added, <tt>false</tt> otherwise.
	 */
	public boolean addToErrorList(DownloadInfo downloadInfo) {
		return mErrorList.add(downloadInfo);
	}

	/**
	 * Removes a download from the active list.
	 * @param downloadInfo The download object to be removed from the list.
	 */
	public void removeFromActiveList(DownloadInfo downloadInfo) {
		mActiveList.remove(downloadInfo);
	}

	/**
	 * Removes a download from the inactive list.
	 * @param downloadInfo The download object to be removed from the list.
	 */
	public void removeFromInactiveList(DownloadInfo downloadInfo) {
		mInactiveList.remove(downloadInfo);
	}

	/**
	 * Removes a download from the pending list.
	 * @param downloadInfo The download object to be removed from the list.
	 */
	public void removeFromPendingList(DownloadInfo downloadInfo) {
		mPendingList.remove(downloadInfo);
	}

	/**
	 * Removes a download from the completed list.
	 * @param downloadInfo The download object to be removed from the list.
	 */
	public void removeFromCompletedList(DownloadInfo downloadInfo) {
		mCompletedList.remove(downloadInfo);
	}

	/**
	 * Removes a download from the error list.
	 * @param downloadInfo The download object to be removed from the list.
	 */
	public void removeFromErrorList(DownloadInfo downloadInfo) {
		mErrorList.remove(downloadInfo);
	}

	/**
	 * Add a download to the download manager.
	 * @param downloadInfo The object to add.
	 * @return The download object that was added.
	 * @throws java.net.MalformedURLException if the URL is not a valid URL.
	 * @throws URLAlreadyExistsException if the URL already exists in the download manager in some other download object.
	 */
//	public DownloadInfo addDownload(DownloadInfo downloadInfo) throws MalformedURLException, URLAlreadyExistsException {
//		URL verifiedURL = downloadInfo.getConnection().getURL();
//		for (URL url : mURLs) {
//			if (url.equals(verifiedURL)) {
//				throw new URLAlreadyExistsException("This URL already exists.");
//			}
//		}
//
//		mURLs.add(verifiedURL);
////		downloadInfo.addListener(this);
//		mInactiveList.add(downloadInfo);
////		saveDownloadsFile();
//
//		return downloadInfo;
//	}

	/**
	 * Add a download to the download manager.
	 * @param URLString The url at which the download is located.
	 * @param directory The directory to download to.
	 * @return The download object that was added.
	 * @throws java.net.MalformedURLException if the URL is not a valid URL.
	 * @throws URLAlreadyExistsException if the URL already exists in the download manager in some other download object.
	 */
//	public DownloadInfo addDownload(String URLString, String directory)
//			throws MalformedURLException, URLAlreadyExistsException {
//		DownloadInfo downloadInfo = createDownloadInfo(URLString, directory);
//		return addDownload(downloadInfo);
//	}

	/**
	 * Create a new download object.
	 * @param URLString The url at which the download is located.
	 * @param directory The directory to download to.
	 * @return The download object that was added.
	 * @throws java.net.MalformedURLException if the URL is not a valid URL.
	 */
//	public DownloadInfo createDownloadInfo(String URLString, String directory) throws MalformedURLException {
//		URL verifiedURL = verifyUrl(URLString);
//		DownloadInfo downloadInfo = new DownloadInfo(new DownloadConnection(verifiedURL));
////		downloadInfo.setDirectory(directory);
//		return downloadInfo;
//	}

	/**
	 * Create a new download object.
	 * @param URLString The url at which the download is located.
	 * @param path The full path (directory+filename+extension) to download to.
	 * @return The download object that was added.
	 * @throws java.net.MalformedURLException if the URL is not a valid URL.
	 */
//	public DownloadInfo createDownloadInfoWithPath(String URLString, String path) throws MalformedURLException {
//		URL verifiedURL = verifyUrl(URLString);
//		DownloadInfo downloadInfo = new DownloadInfo(new HTTPDownloadConnection(verifiedURL));
//		downloadInfo.setPath(path);
//		return downloadInfo;
//	}

	/**
	 * Save downloads file.
	 * Synchronized to avoid potential file write errors across multiple threads.
	 */
//	private synchronized void saveDownloadsFile() {
//		try {
//			ArrayList<DownloadInfo> allDownloads = new ArrayList<DownloadInfo>();
//			allDownloads.addAll(mActiveList);
//			allDownloads.addAll(mInactiveList);
//			allDownloads.addAll(mPendingList);
//			allDownloads.addAll(mCompletedList);
//			allDownloads.addAll(mErrorList);
//
//			new XMLDownloadsFile(allDownloads).saveFile();
//		} catch (Exception e) {
//			DownloadLogger.LOGGER.severe(e.toString());
//		}
//	}

	/**
	 * Verify an URL.
	 * @param url The String to verify as an url.
	 * @return A new URL.
	 * @throws java.net.MalformedURLException if the URL is not a valid URL.
	 */
	private URL verifyUrl(String url) throws MalformedURLException {
		// Only allow HTTP URLs.
		if (!url.toLowerCase().startsWith("http://")) {
			throw new MalformedURLException("Must start with http://");
		}

		URL verifiedUrl = new URL(url);
		// Make sure URL specifies a file.
		if (verifiedUrl.getFile().length() < 2) {
			throw new MalformedURLException("URL has to specify a file.");
		}
		return verifiedUrl;
	}

//	public void downloadEventPerformed(DownloadProgressEvent downloadProgressEvent) {
//		if (downloadProgressEvent.getPercentDownloaded() == 100) {
//			DownloadInfo downloadInfo = downloadProgressEvent.getDownloadInfo();
//			downloadInfo.changeStatusState(new CompletedState(downloadInfo));
//		}
//	}
//
//	public void downloadEventPerformed(DownloadStatusStateEvent downloadStatusStateEvent) {
//		updatePendingList();
//	}
//
//	public void downloadEventPerformed(DownloadConnectedEvent downloadConnectedEvent) {
//		saveDownloadsFile();
//	}

	/**
	 * Check if there are pending downloads and if so move the top one up to the active list.
	 */
	private void updatePendingList() {
		// if an active download has stopped downloading, activate top pending download.
		while (mPendingList.size() > 0 && activeListHasRoom()) {
			DownloadInfo pending = mPendingList.get(0);
			pending.changeStatusState(new ActiveState(pending));
		}

	}

	/**
	 * Get the queue position of an active download.
	 * @param downloadInfo The download object to get the queue position of.
	 * @return the download's queue position.
	 */
	public int getActiveQueuePosition(DownloadInfo downloadInfo) {
		return mActiveList.indexOf(downloadInfo) + 1;
	}

	/**
	 * Get the queue position of a pending download.
	 * @param downloadInfo The download object to get the queue position of.
	 * @return the download's queue position.
	 */
	public int getPendingQueuePosition(DownloadInfo downloadInfo) {
		return mActiveList.size() + mPendingList.indexOf(downloadInfo) + 1;
	}

	/**
	 * @return The number of active and pending downloads.
	 */
	public int getNumberOfQueuedDownloads() {
		return mActiveList.size() + mPendingList.size();
	}

	/**
	 * Move an active download up the active queue.
	 * @param downloadInfo The download object to move.
	 */
	public void moveActiveUp(DownloadInfo downloadInfo) {
		int index = mActiveList.indexOf(downloadInfo);
		if (index != -1 && index != 0) {
			DownloadInfo d = mActiveList.get(index - 1);
			mActiveList.set(index - 1, downloadInfo);
			mActiveList.set(index, d);
		}
	}

	/**
	 * Move a pending download down the pending queue.
	 * @param downloadInfo The download object to move.
	 */
	public void movePendingDown(DownloadInfo downloadInfo) {
		int index = mPendingList.indexOf(downloadInfo);
		if (index != -1 && index != mPendingList.size() - 1) {
			DownloadInfo d = mPendingList.get(index + 1);
			mPendingList.set(index + 1, downloadInfo);
			mPendingList.set(index, d);
		}
	}

	/**
	 * Move a pending download up the pending queue,
	 * or up to the active queue if it's at the top of the pending queue.
	 * @param downloadInfo The download object to move.
	 */
	public void movePendingUp(DownloadInfo downloadInfo) {
		int index = mPendingList.indexOf(downloadInfo);
		if (index != -1) {
			if (index == 0) {
				switchBottomActiveWithTopPending();
			} else {
				DownloadInfo d = mPendingList.get(index - 1);
				mPendingList.set(index - 1, downloadInfo);
				mPendingList.set(index, d);
			}
		}
	}

	/**
	 * Move an active download down the active queue,
	 * or down to the pending queue if it's at the bottom of the active queue.
	 * @param downloadInfo The download object to move.
	 */
	public void moveActiveDown(DownloadInfo downloadInfo) {
		int index = mActiveList.indexOf(downloadInfo);
		if (index != -1) {
			if (index == mActiveList.size() - 1) {
				if (mPendingList.size() > 0) {
					switchBottomActiveWithTopPending();
				}
			} else {
				DownloadInfo d = mActiveList.get(index + 1);
				mActiveList.set(index + 1, downloadInfo);
				mActiveList.set(index, d);
			}
		}
	}

	/**
	 * Switch the bottommost active download object with the topmost pending download object.
	 */
	private void switchBottomActiveWithTopPending() {
		// move active first by setting it to pending.
		DownloadInfo bottomActive = mActiveList.get(mActiveList.size() - 1);
		bottomActive.changeStatusState(new PendingState(bottomActive));
		// now the newly pending download will be added to the bottom of the pending queue,
		// as per the setStatusState method, so insert it at the top of the pending list.
		DownloadInfo bottomPending = mPendingList.get(mPendingList.size() - 1);
		mPendingList.add(0, bottomPending);
		// Now there're two of the same, so remove the one left at the bottom.
		mPendingList.remove(mPendingList.size() - 1);
	}

	/**
	 * Rmove a download from the download manager.
	 * Actually only removes the URL from the manager's list of URLs.
	 * @param downloadInfo Which download object to remove.
	 */
	public void removeDownload(DownloadInfo downloadInfo) {
//		mURLs.remove(downloadInfo.getConnection().getURL());
//		saveDownloadsFile();
        BusProvider.getInstance().post(new DownloadRemoveEvent(downloadInfo.getId()));
	}
}
