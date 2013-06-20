/**
 * AIDLServiceDownloadManager,		part of Aptoide
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
package cm.aptoide.pt.services;

import cm.aptoide.pt.views.ViewListDownloads;
import cm.aptoide.pt.views.ViewDownloadManagement;
import cm.aptoide.pt.views.ViewCache;
import cm.aptoide.pt.AIDLDownloadManager;
import cm.aptoide.pt.AIDLDownloadObserver;

/**
 * AIDLServiceDownloadManager, IPC Interface definition for Aptoide's ServiceDownloadManager
 *
 * @author dsilveira
 *
 */
interface AIDLServiceDownloadManager {

	void callRegisterDownloadManager(in AIDLDownloadManager downloadManager);
	void callUnregisterDownloadManager();
	void callRegisterDownloadObserver(in int appHashId, in AIDLDownloadObserver downloadObserver);
	void callUnregisterDownloadObserver(in int appHashId);
	void callInstallApp(in ViewDownloadManagement apk);
	ViewDownloadManagement callGetAppDownloading(in int appHashId);
	void callStartDownload(in ViewDownloadManagement download);
	void callStartDownloadAndObserve(in ViewDownloadManagement download, in AIDLDownloadObserver downloadObserver);
	void callPauseDownload(in int appHashId);
	void callResumeDownload(in int appHashId);
	void callStopDownload(in int appHashId);
	void callRestartDownload(in int appHashId);
	boolean callAreDownloadsOngoing();
	ViewListDownloads callGetDownloadsOngoing();
	boolean callAreDownloadsCompleted();
	ViewListDownloads callGetDownloadsCompleted();
	boolean callAreDownloadsFailed();
	ViewListDownloads callGetDownloadsFailed();
	void callClearDownloads();

}
