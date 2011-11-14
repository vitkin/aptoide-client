/**
 * ManagerDatabase,		part of Aptoide's Test 
 * 
 * Copyright (C) 2011  Duarte Silveira
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
package cm.aptoide.pt.data.database.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.test.AndroidTestCase;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.views.ViewApplication;
import cm.aptoide.pt.data.views.ViewDisplayListApps;
import cm.aptoide.pt.data.views.ViewDisplayListRepos;
import cm.aptoide.pt.data.views.ViewLogin;
import cm.aptoide.pt.data.views.ViewRepository;

/**
 * @author dsilveira
 * @since 3.0
 *
 */
public class TestManagerDatabase extends AndroidTestCase {
	
	ManagerDatabase db;
	
	ViewRepository repo;
	ViewLogin login;
	ArrayList<ViewApplication> apps;
	ViewApplication app1;
	ViewApplication app2;
	ViewApplication appInstalled;
	
	final String USER1 = "user1";
	final String PASS1 = "pass1";
	final String URI = "http://apps.bazaarandroid.com/";
	final int REPO_SIZE = 2;
	final String NAME1 = "app1";
	final String PACKAGE_NAME1 = "pt.app1";	
	final String VERSION_NAME1 = "1.0";
	final int VERSION_CODE1 = 1;
	final String VERSION_NAME2 = "2.0";
	final int VERSION_CODE2 = 2;
	final String VERSION_NAME3 = "3.0";
	final int VERSION_CODE3 = 3;

	public TestManagerDatabase() {
		super();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		db = new ManagerDatabase(getContext());
		login = new ViewLogin(USER1, PASS1);
		repo = new ViewRepository(URI);
		repo.setSize(REPO_SIZE);
		repo.setInUse(true);
		repo.setLogin(login);
		
		apps = new ArrayList<ViewApplication>(2);
		app1 = new ViewApplication(NAME1, PACKAGE_NAME1, VERSION_NAME1, VERSION_CODE1, false);
		apps.add(app1);
		app2 = new ViewApplication(NAME1, PACKAGE_NAME1,VERSION_NAME3, VERSION_CODE3, false);
		apps.add(app2);
		appInstalled = new ViewApplication(NAME1, PACKAGE_NAME1, VERSION_NAME2, VERSION_CODE2, true);
		
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		File db_file = new File("/data/data/cm.aptoide.pt.test/databases/aptoide_db");
		db.closeDB();
		db_file.delete();
	}

	/**
	 * Test method for {@link cm.aptoide.pt.data.database.ManagerDatabase#getReposDisplayInfo()}.
	 */
	public final void testGetReposDisplayInfo() {
		db.insertRepository(repo);
		ViewDisplayListRepos displayRepos = db.getReposDisplayInfo();
		
		assertEquals("wrong size reposList",1, displayRepos.getList().size());
		Map<String,Object> displayRepo = displayRepos.getRepo(0);
		
		int hashid = (Integer)displayRepo.get(Constants.KEY_REPO_HASHID);
		assertEquals("wrong repoHashid", URI.hashCode(), hashid);
		String uri = (String)displayRepo.get(Constants.KEY_REPO_URI);
		assertEquals("Wrong repo uri", URI, uri);
		int size = (Integer)displayRepo.get(Constants.KEY_REPO_SIZE);
		assertEquals("Wrong repo size", REPO_SIZE, size);
		boolean inUse = (Boolean)displayRepo.get(Constants.KEY_REPO_IN_USE);
		assertTrue("Repo not in use!?", inUse);
		boolean loginRequired = (Boolean)displayRepo.get(Constants.DISPLAY_REPO_REQUIRES_LOGIN);
		assertTrue("Repo does not require login!?", loginRequired);
		ViewLogin login = (ViewLogin)displayRepo.get(Constants.DISPLAY_REPO_LOGIN);
		assertEquals("Wrong username", USER1, login.getUsername());
		assertEquals("Wrong password", PASS1, login.getPassword());
	}

	/**
	 * Test method for {@link cm.aptoide.pt.data.database.ManagerDatabase#getInstalledAppsDisplayInfo(int, int)}.
	 */
	public final void testGetInstalledAppsDisplayInfo() {
		db.insertApplications(apps);
		db.insertInstalledApplication(appInstalled);
		ViewDisplayListApps displayApps = db.getInstalledAppsDisplayInfo(0, 10);
		
		assertEquals("wrong size appsList",1, displayApps.getList().size());
		Map<String,Object> displayApp = displayApps.getApp(0);
		
		int hashid = (Integer)displayApp.get(Constants.KEY_APPLICATION_HASHID);
		assertEquals("wrong appHashid", (PACKAGE_NAME1+"|"+VERSION_CODE2).hashCode(), hashid);
		String iconCachePath = (String)displayApp.get(Constants.DISPLAY_APP_ICON_CACHE_PATH);
		assertEquals("Wrong app cache path", Constants.PATH_CACHE_ICONS+hashid, iconCachePath);
		String appName = (String)displayApp.get(Constants.KEY_APPLICATION_NAME);
		assertEquals("Wrong app name", NAME1, appName);
		String installedVersionName = (String)displayApp.get(Constants.DISPLAY_APP_INSTALLED_VERSION_NAME);
		assertEquals("Wrong installed version name", VERSION_NAME2, installedVersionName);
		boolean isUpdatable = (Boolean)displayApp.get(Constants.DISPLAY_APP_IS_UPDATABLE);
		assertTrue("App not updatable!?", isUpdatable);
		String uptodateVersionName = (String)displayApp.get(Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME);
		assertEquals("Wrong up to date version name", VERSION_NAME3, uptodateVersionName);
		boolean isDowngradable = (Boolean)displayApp.get(Constants.DISPLAY_APP_IS_DOWNGRADABLE);
		assertTrue("App not downgradable!?", isDowngradable);
		String downgradeVersionName = (String)displayApp.get(Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME);
		assertEquals("Wrong downgrade version name", VERSION_NAME1, downgradeVersionName);
	}

}
