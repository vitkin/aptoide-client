package cm.aptoide.pt2;

import java.util.HashMap;

import cm.aptoide.pt2.views.ViewDownloadManagement;

public class DataStructureDownloads {

	private static DataStructureDownloads dataStructureDownloads;
	
	
	private HashMap<Integer, ViewDownloadManagement> ongoingDownloads = new HashMap<Integer, ViewDownloadManagement>();
	private HashMap<Integer, ViewDownloadManagement> completedDownloads = new HashMap<Integer, ViewDownloadManagement>();
	private HashMap<Integer, ViewDownloadManagement> failedDownloads = new HashMap<Integer, ViewDownloadManagement>();
	
	public HashMap<Integer, ViewDownloadManagement> getOngoingDownloads() {
		return ongoingDownloads;
	}

	public void setOngoingDownloads(HashMap<Integer, ViewDownloadManagement> ongoingDownloads) {
		this.ongoingDownloads = ongoingDownloads;
	}

	public HashMap<Integer, ViewDownloadManagement> getCompletedDownloads() {
		return completedDownloads;
	}

	public void setCompletedDownloads(HashMap<Integer, ViewDownloadManagement> completedDownloads) {
		this.completedDownloads = completedDownloads;
	}

	public HashMap<Integer, ViewDownloadManagement> getFailedDownloads() {
		return failedDownloads;
	}

	public void setFailedDownloads(HashMap<Integer, ViewDownloadManagement> failedDownloads) {
		this.failedDownloads = failedDownloads;
	}

	public static DataStructureDownloads getInstance(){
		if(dataStructureDownloads==null){
			dataStructureDownloads = new DataStructureDownloads();
		}
		return dataStructureDownloads;
	}
	
}
