/**
 * AIDLAptoideServiceData,		part of Aptoide's ServiceData
 * Copyright (C) 2011 Duarte Silveira
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
package cm.aptoide.pt.data;

import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.AIDLAptoideInterface;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.display.ViewDisplayListRepos;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.AIDLAppInfo;
import cm.aptoide.pt.AIDLReposInfo;

/**
 * AIDLAptoideServiceData, IPC Interface definition for Aptoide's ServiceData
 *
 * @author dsilveira
 * @since 3.0
 *
 */
interface AIDLAptoideServiceData {

	void callStoreScreenDimensions(in ViewScreenDimensions screenDimensions);
	
	void callSyncInstalledApps();
	
	void callRegisterReposObserver(in AIDLReposInfo reposInfoObserver);
	ViewDisplayListRepos callGetRepos();
	void callAddRepo(in ViewRepository repo);
	void callRemoveRepo(in int repoHashid);
	void callSetInUseRepo(in int repoHashid);
	void callUnsetInUseRepo(in int repoHashid);
	void callRemoveLogin(in int repoHashid);
	void callUpdateLogin(in ViewRepository repo);
	void callNoRepos();
	void callLoadingRepos();
	
	void callRegisterInstalledAppsObserver(in AIDLAptoideInterface installedAppsObserver);
	void callRegisterAvailableAppsObserver(in AIDLAptoideInterface availableAppsObserver);
	
	boolean callAreListsByCategory();
	void callSetListsBy(in boolean byCategory);
	
	ViewDisplayCategory callGetCategories();
	
	ViewDisplayListApps callGetInstalledApps();
	ViewDisplayListApps callGetAvailableApps(in int offset, in int range);
	ViewDisplayListApps callGetAvailableAppsByCategory(in int offset, in int range, in int categoryHashid);
	ViewDisplayListApps callGetUpdatableApps();
	
	ViewDisplayListApps callGetAppSearchResults(in String searchString);
	
	void callRegisterAppInfoObserver(in AIDLAppInfo appInfoObserver, in int appHashid);
	void CallFillAppInfo(in int appHashid);
	ViewDisplayAppVersionsInfo callGetAppInfo(in int appHashid);
	
	void callInstallApp(in int appHashid);
	void callUninstallApp(in int appHashid);
	void callScheduleInstallApp(in int appHashid);
	void callUnscheduleInstallApp(in int appHashid);
	boolean callIsAppScheduledToInstall(in int appHashid);
	
}
