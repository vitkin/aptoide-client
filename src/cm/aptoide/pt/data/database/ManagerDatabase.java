/**
 * ManagerDatabase,		part of Aptoide
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

package cm.aptoide.pt.data.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionExtras;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionInfo;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionStats;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.display.ViewDisplayListRepos;
import cm.aptoide.pt.data.display.ViewDisplayRepo;
import cm.aptoide.pt.data.downloads.EnumDownloadType;
import cm.aptoide.pt.data.downloads.ViewDownload;
import cm.aptoide.pt.data.downloads.ViewDownloadInfo;
import cm.aptoide.pt.data.model.ViewAppComment;
import cm.aptoide.pt.data.model.ViewAppDownloadInfo;
import cm.aptoide.pt.data.model.ViewApplication;
import cm.aptoide.pt.data.model.ViewCategory;
import cm.aptoide.pt.data.model.ViewExtraInfo;
import cm.aptoide.pt.data.model.ViewIconInfo;
import cm.aptoide.pt.data.model.ViewListIds;
import cm.aptoide.pt.data.model.ViewLogin;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.model.ViewScreenInfo;
import cm.aptoide.pt.data.model.ViewStatsInfo;

/**
 * ManagerDatabase, manages aptoide's sqlite data persistence
 * 					regarding concurrency, the usage of SQLiteDatabase enforces a readers/writers model
 * 					so we don't need to worry about that in this manager.
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerDatabase {

	private AptoideServiceData serviceData;
	private static SQLiteDatabase db = null;
	private ArrayList<Integer> categories_hashids;
	
	/**
	 * ManagerDatabase Constructor, opens Aptoide's database or creates it if it doens't exist yet 
	 *
	 * @param Context context, Aptoide's activity context
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ManagerDatabase(AptoideServiceData serviceData) {
		this.serviceData = serviceData;
		if(db == null){
			db = serviceData.openOrCreateDatabase(Constants.DATABASE, 0, null);
			db.beginTransaction();
			
			db.execSQL(Constants.CREATE_TABLE_REPOSITORY);
			db.execSQL(Constants.CREATE_TABLE_LOGIN);
			db.execSQL(Constants.CREATE_TABLE_APPLICATION);
			db.execSQL(Constants.CREATE_TABLE_CATEGORY);
			db.execSQL(Constants.CREATE_TABLE_SUB_CATEGORY);
			db.execSQL(Constants.CREATE_TABLE_APP_CATEGORY);
			db.execSQL(Constants.CREATE_TABLE_APP_INSTALLED);
			db.execSQL(Constants.CREATE_TABLE_ICON_INFO);
			db.execSQL(Constants.CREATE_TABLE_SCREEN_INFO);
			db.execSQL(Constants.CREATE_TABLE_DOWNLOAD_INFO);
			db.execSQL(Constants.CREATE_TABLE_STATS_INFO);
			db.execSQL(Constants.CREATE_TABLE_EXTRA_INFO);
			db.execSQL(Constants.CREATE_TABLE_APP_COMMENTS);
			
			db.execSQL(Constants.CREATE_TRIGGER_REPO_DELETE_REPO);
			db.execSQL(Constants.CREATE_TRIGGER_REPO_UPDATE_REPO_HASHID_STRONG);
			db.execSQL(Constants.CREATE_TRIGGER_LOGIN_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_LOGIN_UPDATE_REPO_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_APPLICATION_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_APPLICATION_UPDATE_REPO_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_APPLICATION_DELETE);
			db.execSQL(Constants.CREATE_TRIGGER_APPLICATION_UPDATE_APP_FULL_HASHID_STRONG);
			db.execSQL(Constants.CREATE_TRIGGER_CATEGORY_DELETE);
			db.execSQL(Constants.CREATE_TRIGGER_CATEGORY_UPDATE_CATEGORY_HASHID_STRONG);
			db.execSQL(Constants.CREATE_TRIGGER_SUB_CATEGORY_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_SUB_CATEGORY_UPDATE_PARENT_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_SUB_CATEGORY_UPDATE_CHILD_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_APP_CATEGORY_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_APP_CATEGORY_UPDATE_APP_FULL_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_APP_CATEGORY_UPDATE_CATEGORY_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_ICON_INFO_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_ICON_INFO_UPDATE_APP_FULL_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_SCREEN_INFO_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_SCREEN_INFO_UPDATE_APP_FULL_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_DOWNLOAD_INFO_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_DOWNLOAD_INFO_UPDATE_APP_FULL_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_STATS_INFO_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_STATS_INFO_UPDATE_APP_FULL_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_EXTRA_INFO_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_EXTRA_INFO_UPDATE_APP_FULL_HASHID_WEAK);
			db.execSQL(Constants.CREATE_TRIGGER_APP_COMMENT_INSERT);
			db.execSQL(Constants.CREATE_TRIGGER_APP_COMMENT_UPDATE_APP_FULL_HASHID_WEAK);
			
			db.setTransactionSuccessful();
			db.endTransaction();
			
			hammerCategories();
		}else if(!db.isOpen()){
			db = serviceData.openOrCreateDatabase(Constants.DATABASE, 0, null);
		}
		db.execSQL(Constants.PRAGMA_FOREIGN_KEYS_OFF);
		db.execSQL(Constants.PRAGMA_RECURSIVE_TRIGGERS_OFF);
	}
	
	/**
	 * CloseDB, handles closing of db
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void closeDB(){
		db.close();
	}
	
	
	/* ******************************************************** *
	 * 															*
	 *                     Dirty methods						*
	 * 															*
	 * ******************************************************** */
	
	
	
	
	/**
	 * prepareToTorchDB, handles smooth transition from old database schema
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public HashMap<String,Boolean> prepareToTorchDB(){
								/* hard coded strings that represent old names */
		Cursor cursor = db.query("servers", new String[]{"uri", "inuse"}, null, null, null, null, null);
		HashMap<String, Boolean> repositories = new HashMap<String, Boolean>(cursor.getCount());
		final int oldIndexRepoUri = 0;
		final int oldIndexRepoInUse = 1;
		cursor.moveToFirst();
		do{		//TODO refactor to return Repository objects + Login Objects
			repositories.put(cursor.getString(oldIndexRepoUri), cursor.getInt(oldIndexRepoInUse) == Constants.DB_TRUE?true:false);
		} while(cursor.moveToNext());
		cursor.close();
		
		return repositories; 
		
		//TODO close db, delete db file
		
		//TODO call constructor
		
		//TODO re-populate repositories
		
	}
	
	
	
	/**
	 * hammerCategories, inserts a hammered list of categories 
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void hammerCategories(){
		categories_hashids = new ArrayList<Integer>();
		ArrayList<ViewCategory> categories = new ArrayList<ViewCategory>(2);
		
		ViewCategory applications = new ViewCategory(Constants.CATEGORY_APPLICATIONS);
		for (String subCategory : Constants.SUB_CATEGORIES_APPLICATIONS) {
			applications.addChild(new ViewCategory(subCategory));
			categories_hashids.add(subCategory.hashCode());
		}

		categories.add(applications);
		
		ViewCategory games = new ViewCategory(Constants.CATEGORY_GAMES);
		for (String subCategory : Constants.SUB_CATEGORIES_GAMES) {
			games.addChild(new ViewCategory(subCategory));
			categories_hashids.add(subCategory.hashCode());
		}
		
		categories.add(games);
		
		ViewCategory others = new ViewCategory(Constants.CATEGORY_OTHERS);
		categories.add(others);
		
		insertCategories(categories);
		
	}
	
	
	/* ******************************************************** *
	 * 															*
	 *                     Writer methods						*
	 * 															*
	 * ******************************************************** */
	
	
	
	/**
	 * insertCategories, handles multiple categories insertion
	 * 
	 * @param ArrayList<Category> categories
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertCategories(ArrayList<ViewCategory> categories){
		db.beginTransaction();
		try{
			InsertHelper insertCategory = new InsertHelper(db, Constants.TABLE_CATEGORY);
			ArrayList<ContentValues> subCategoriesRelations = new ArrayList<ContentValues>(categories.size());
			ContentValues subCategoryRelation;
			
			for (ViewCategory category : categories) {
				if(insertCategory.insert(category.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
				if(category.hasChilds()){
					for (ViewCategory subCategory : category.getSubCategories()) {
						if(insertCategory.insert(subCategory.getValues()) == Constants.DB_ERROR){
							//TODO throw exception;
						}
						subCategoryRelation = new ContentValues(Constants.NUMBER_OF_COLUMNS_SUB_CATEGORY); //TODO check if this implicit object recycling works 
						subCategoryRelation.put(Constants.KEY_SUB_CATEGORY_PARENT, category.getHashid());
						subCategoryRelation.put(Constants.KEY_SUB_CATEGORY_CHILD, subCategory.getHashid());
						subCategoriesRelations.add(subCategoryRelation);
					}
				}
			}
			insertCategory.close();
			
			InsertHelper insertCategoryRelation = new InsertHelper(db, Constants.TABLE_SUB_CATEGORY);
			for (ContentValues categoryRelationValues : subCategoriesRelations) {
				if(insertCategoryRelation.insert(categoryRelationValues) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertCategoryRelation.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertCategory, handles single category insertion
	 * 
	 * @param ViewCategory category
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertCategory(ViewCategory category){
		db.beginTransaction();
		try{
			ContentValues parentCategoryRelation;
			
			if(db.insert(Constants.TABLE_CATEGORY, null, category.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			if(!(category.getParentHashid() == Constants.TOP_CATEGORY)){
				parentCategoryRelation = new ContentValues(Constants.NUMBER_OF_COLUMNS_SUB_CATEGORY); //TODO check if this implicit object recycling works 
				parentCategoryRelation.put(Constants.KEY_SUB_CATEGORY_PARENT, category.getParentHashid());
				parentCategoryRelation.put(Constants.KEY_SUB_CATEGORY_CHILD, category.getHashid());

				if(db.insert(Constants.TABLE_SUB_CATEGORY, null, parentCategoryRelation) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	
	
	/**
	 * insertRepositories, handles multiple repositories insertion
	 * 
	 * @param ArrayList<Repository> repositories
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertRepositories(ArrayList<ViewRepository> repositories){
		db.beginTransaction();
		try{
			InsertHelper insertRepository = new InsertHelper(db, Constants.TABLE_REPOSITORY);
			ArrayList<ContentValues> loginsValues = new ArrayList<ContentValues>(repositories.size());
			
			for (ViewRepository repository : repositories) {
				if(insertRepository.replace(repository.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
				if(repository.isLoginRequired()){
					loginsValues.add(repository.getLogin().getValues());
				}
			}
			insertRepository.close();
			
			InsertHelper insertLogin = new InsertHelper(db, Constants.TABLE_LOGIN);
			for (ContentValues loginValues : loginsValues) {
				if(insertLogin.replace(loginValues) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertLogin.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
//			serviceData.updateReposLists();
		}
	}
	
	/**
	 * insertRepository, handles single repository insertion
	 * 
	 * @param ViewRepository repository
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertRepository(ViewRepository repository){
		db.beginTransaction();
		try{
			if(db.replace(Constants.TABLE_REPOSITORY, null, repository.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			if(repository.isLoginRequired()){
				if(db.replace(Constants.TABLE_LOGIN, null, repository.getLogin().getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
			e.printStackTrace();
		}finally{
			db.endTransaction();
			serviceData.updateReposLists();
		}
	}
	
	/**
	 * removeRepositories, handles multiple repositories removal
	 * 
	 * @param ViewListIds repoHashids
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void removeRepositories(ViewListIds repoHashids){	//TODO manually cascade triggers, because they don't automatically do it
		db.beginTransaction();
		try{
			StringBuilder deleteWhere = new StringBuilder();
			boolean firstWhere = true;
			
			deleteWhere.append(Constants.KEY_REPO_HASHID+" IN (");
			for (int repoHashid : repoHashids.getList()) {
				
				if(firstWhere){
					firstWhere = false;
				}else{
					deleteWhere.append(",");					
				}
				
				deleteWhere.append(repoHashid);
			}
			deleteWhere.append(")");	
			
			
			if(db.delete(Constants.TABLE_REPOSITORY, deleteWhere.toString(), null) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * toggleRepositoriesInUse, handles multiple repositories inUse toggle
	 * 
	 * @param ViewListIds repoHashids
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void toggleRepositoriesInUse(ViewListIds repoHashids, boolean setInUse){
		db.beginTransaction();
		try{
			ContentValues setTrue = new ContentValues();
			setTrue.put(Constants.KEY_REPO_IN_USE, (setInUse?Constants.DB_TRUE:Constants.DB_FALSE) );
			
			StringBuilder updateWhere = new StringBuilder();
			boolean firstWhere = true;

			updateWhere.append(Constants.KEY_REPO_HASHID+" IN (");
			for (int repoHashid : repoHashids.getList()) {
				
				if(firstWhere){
					firstWhere = false;
				}else{
					updateWhere.append(",");					
				}
				
				updateWhere.append(repoHashid);
			}
			updateWhere.append(")");
			
			
			if(db.update(Constants.TABLE_REPOSITORY, setTrue, updateWhere.toString(), null) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
			
//			serviceData.resetAvailableLists();
			
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * toggleRepositoryInUse, handles single repository inUse toggle
	 * 
	 * @param int repoHashids
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void toggleRepositoryInUse(int repoHashid, boolean setInUse){
		db.beginTransaction();
		try{
			ContentValues setTrue = new ContentValues();
			setTrue.put(Constants.KEY_REPO_IN_USE, (setInUse?Constants.DB_TRUE:Constants.DB_FALSE) );
			
			String updateWhere = Constants.KEY_REPO_HASHID+" IN ("+repoHashid+")";
			
			if(db.update(Constants.TABLE_REPOSITORY, setTrue, updateWhere, null) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
			serviceData.updateReposLists();
		}
	}
		
	
	
	
	/**
	 * insertApplications, handles multiple applications insertion
	 * 
	 * @param ArrayList<Application> applications
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertApplications(ArrayList<ViewApplication> applications){
		ArrayList<ContentValues> appCategoriesRelations = new ArrayList<ContentValues>(applications.size());
		db.beginTransaction();
		try{
			InsertHelper insertApplication = new InsertHelper(db, Constants.TABLE_APPLICATION);
			ContentValues appCategoryRelation;
			
			for (ViewApplication application : applications) {
				if(insertApplication.insert(application.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
				appCategoryRelation = new ContentValues(Constants.NUMBER_OF_COLUMNS_APP_CATEGORY);
				appCategoryRelation.put(Constants.KEY_APP_CATEGORY_APP_FULL_HASHID, application.getFullHashid());
				appCategoryRelation.put(Constants.KEY_APP_CATEGORY_CATEGORY_HASHID
										, (categories_hashids.contains(application.getCategoryHashid())?application.getCategoryHashid():Constants.CATEGORY_HASHID_OTHERS));
				appCategoriesRelations.add(appCategoryRelation);
				
			}
			insertApplication.close();
			
			db.setTransactionSuccessful();
			
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
			e.printStackTrace();
		}finally{
			if(db.inTransaction()){
				db.endTransaction();
			}
		}
		
		db.beginTransaction();
		try{
			InsertHelper insertAppCategoryRelation = new InsertHelper(db, Constants.TABLE_APP_CATEGORY);
			for (ContentValues appCategoryRelationValues : appCategoriesRelations) {
				if(insertAppCategoryRelation.insert(appCategoryRelationValues) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertAppCategoryRelation.close();
			db.setTransactionSuccessful();
			
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(db.inTransaction()){
				db.endTransaction();
			}
		}
	}
	
		
	/**
	 * insertApplication, handles single application insertion
	 * 
	 * @param ViewApplication application
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertApplication(ViewApplication application){
		ContentValues appCategoryRelation = null;

		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_APPLICATION ,null, application.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			appCategoryRelation = new ContentValues(Constants.NUMBER_OF_COLUMNS_APP_CATEGORY); //TODO check if this implicit object recycling works 
			appCategoryRelation.put(Constants.KEY_APP_CATEGORY_APP_FULL_HASHID, application.getFullHashid());
			appCategoryRelation.put(Constants.KEY_APP_CATEGORY_CATEGORY_HASHID
									, (categories_hashids.contains(application.getCategoryHashid())?application.getCategoryHashid():Constants.CATEGORY_HASHID_OTHERS));
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
		
		db.beginTransaction();
		try{

			if(db.insert(Constants.TABLE_APP_CATEGORY ,null, appCategoryRelation) == Constants.DB_ERROR){
				//TODO throw exception;
			}

			db.setTransactionSuccessful();
			
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(db.inTransaction()){
				db.endTransaction();
			}
		}
	}
	
	/**
	 * removeApplications, handles multiple applications removal
	 * 
	 * @param ViewListIds appsFullHashid
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void removeApplications(ViewListIds appsFullHashid){
		db.beginTransaction();
		try{
			StringBuilder deleteWhere = new StringBuilder();
			boolean firstWhere = true;
			
			deleteWhere.append(Constants.KEY_APPLICATION_FULL_HASHID+" IN (");
			for (int appFullHashid : appsFullHashid.getList()) {
				
				if(firstWhere){
					firstWhere = false;
				}else{
					deleteWhere.append(",");
				}
				
				deleteWhere.append(appFullHashid);
			}
			deleteWhere.append(")");
			
			if(db.delete(Constants.TABLE_APPLICATION, deleteWhere.toString(), null) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
		}
	}
	
	
	
	/**
	 * insertIconsInfo, handles multiple application's icon info insertion
	 * 
	 * @param ArrayList<IconInfo> iconsInfo
	 */
	public void insertIconsInfo(ArrayList<ViewIconInfo> iconsInfo){
		db.beginTransaction();
		try{
			InsertHelper insertIconInfo = new InsertHelper(db, Constants.TABLE_ICON_INFO);
			
			for (ViewIconInfo iconInfo : iconsInfo) {
				if(insertIconInfo.insert(iconInfo.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertIconInfo.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertIconInfo, handles single application's icon info insertion
	 * 
	 * @param ViewIconInfo iconInfo
	 */
	public void insertIconInfo(ViewIconInfo iconInfo){
		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_ICON_INFO, null, iconInfo.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	
	
	/**
	 * insertScreensInfo, handles multiple application's screen info insertion
	 * 
	 * @param ArrayList<ScreenInfo> screensInfo
	 */
	public void insertScreensInfo(ArrayList<ViewScreenInfo> screensInfo){
		db.beginTransaction();
		try{
			InsertHelper insertScreenInfo = new InsertHelper(db, Constants.TABLE_SCREEN_INFO);
			
			for (ViewScreenInfo screenInfo : screensInfo) {
				if(insertScreenInfo.insert(screenInfo.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertScreenInfo.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertScreenInfo, handles single application's screen info insertion
	 * 
	 * @param ViewScreenInfo screenInfo
	 */
	public void insertScreenInfo(ViewScreenInfo screenInfo){
		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_SCREEN_INFO, null, screenInfo.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertDownloadsInfo, handles multiple application's download info insertion
	 * 
	 * @param ArrayList<DownloadInfo> downloadsInfo
	 */
	public void insertDownloadsInfo(ArrayList<ViewAppDownloadInfo> downloadsInfo){
		db.beginTransaction();
		try{
			InsertHelper insertDownloadInfo = new InsertHelper(db, Constants.TABLE_DOWNLOAD_INFO);
			
			for (ViewAppDownloadInfo downloadInfo : downloadsInfo) {
				if(insertDownloadInfo.insert(downloadInfo.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertDownloadInfo.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertDownloadInfo, handles single application's download info insertion
	 * 
	 * @param ViewAppDownloadInfo downloadInfo
	 */
	public void insertDownloadInfo(ViewAppDownloadInfo downloadInfo){
		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_DOWNLOAD_INFO, null, downloadInfo.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertExtraInfos, handles multiple application's extra info insertion
	 * 
	 * @param ArrayList<ViewExtraInfo> extraInfos
	 */
	public void insertExtras(ArrayList<ViewExtraInfo> extraInfos){
		db.beginTransaction();
		try{
			InsertHelper insertExtraInfo = new InsertHelper(db, Constants.TABLE_EXTRA_INFO);
			
			for (ViewExtraInfo extraInfo : extraInfos) {
				if(insertExtraInfo.insert(extraInfo.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertExtraInfo.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertExtraInfo, handles single application's extra info insertion
	 * 
	 * @param ViewExtraInfo extraInfo
	 */
	public void insertExtra(ViewExtraInfo extraInfo){
		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_EXTRA_INFO, null, extraInfo.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	
	
	
	/**
	 * insertAppComments, handles multiple application's comments insertion
	 * 
	 * @param ArrayList<AppComment> appComment
	 */
	public void insertAppComments(ArrayList<ViewAppComment> appComments){
		db.beginTransaction();
		try{
			InsertHelper insertAppComments = new InsertHelper(db, Constants.TABLE_APP_COMMENTS);
			
			for (ViewAppComment appComment : appComments) {
				if(insertAppComments.insert(appComment.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertAppComments.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	
	
	
	/**
	 * insertOrReplaceStatsInfos, handles multiple application's stats info insertion
	 * 							  if already present replaces with new ones
	 * 
	 * @param ArrayList<StatsInfo> statsInfos
	 */
	public void insertOrReplaceStatsInfos(ArrayList<ViewStatsInfo> statsInfos){
		db.beginTransaction();
		try{
			InsertHelper insertStatsInfo = new InsertHelper(db, Constants.TABLE_STATS_INFO);
			
			for (ViewStatsInfo statsInfo : statsInfos) {
				if(insertStatsInfo.replace(statsInfo.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertStatsInfo.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertOrReplaceStatsInfo, handles single application's stats info insertion
	 * 							 if already present replaces with new ones
	 * 
	 * @param ViewStatsInfo statsInfo
	 */
	public void insertOrReplaceStatsInfo(ViewStatsInfo statsInfo){
		db.beginTransaction();
		try{
			if(db.replace(Constants.TABLE_STATS_INFO, null, statsInfo.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
		}
	}
		
	
	
	
	/**
	 * insertInstalledApplications, handles multiple installed applications insertion
	 * 										 
	 * 
	 * @param ArrayList<Application> installedApplications
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertInstalledApplications(ArrayList<ViewApplication> installedApplications){
		db.beginTransaction();
		try{
			db.execSQL(Constants.DROP_TABLE_APP_INSTALLED);
			db.execSQL(Constants.CREATE_TABLE_APP_INSTALLED);
			
			InsertHelper insertInstalledApplication = new InsertHelper(db, Constants.TABLE_APP_INSTALLED);
			for (ViewApplication application : installedApplications) {
				if(insertInstalledApplication.insert(application.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertInstalledApplication.close();
			
			db.setTransactionSuccessful();
			serviceData.updateInstalledLists();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * insertInstalledApplication, handles single installed application insertion
	 * 										 
	 * 
	 * @param ViewApplication installedApplication
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void insertInstalledApplication(ViewApplication installedApplication){
		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_APP_INSTALLED ,null, installedApplication.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
			serviceData.updateInstalledLists();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * removeInstalledApplication, handles single application removal
	 * 
	 * @param String appHashid
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void removeInstalledApplication(int appHashid){
		db.beginTransaction();
		try{
			if(db.delete(Constants.TABLE_APP_INSTALLED, Constants.KEY_APP_INSTALLED_HASHID+"="+appHashid, null) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
			serviceData.updateInstalledLists();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * removeInstalledApplication, handles single application removal
	 * 
	 * @param String packageName
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void removeInstalledApplication(String packageName){
		db.beginTransaction();
		try{
			if(db.delete(Constants.TABLE_APP_INSTALLED, Constants.KEY_APP_INSTALLED_PACKAGE_NAME+"=?",new String[]{packageName}) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
			serviceData.updateInstalledLists();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
		}
	}
	
	
		
	/* ******************************************************** *
	 * 															*
	 *                     Reader methods						*
	 * 															*
	 * ******************************************************** */
	
	
	
	/**
	 * aptoideNonAtomicQuery, serves as a basis for all aptoide db's queries,
	 * 				does not handle transactions
	 * 
	 * @param String sqlQuery
	 * @param String[] queryArgs
	 * 
	 * @return Cursor querysResults
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	private Cursor aptoideNonAtomicQuery(String sqlQuery, String[] queryArgs){
		return db.rawQuery(sqlQuery, queryArgs);
	}
	
	/**
	 * aptoideAtomicQuery, serves as a basis for all aptoide db's queries,
	 * 				handles transactions
	 * 
	 * @param String sqlQuery
	 * @param String[] queryArgs
	 * 
	 * @return Cursor querysResults
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	private Cursor aptoideAtomicQuery(String sqlQuery, String[] queryArgs){
		Cursor resultsCursor = null;
		
		db.beginTransaction();
		try{
			resultsCursor = db.rawQuery(sqlQuery, queryArgs);
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			db.endTransaction();
		}
		
		return resultsCursor;
	}
	
	/**
	 * aptoideNonAtomicQuery, serves as a basis for all aptoide db's queries with no arguments,
	 * 				does not handle transactions
	 * 
	 * @param String sqlQuery
	 * 
	 * @return Cursor querysResults
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	private Cursor aptoideNonAtomicQuery(String sqlQuery){
		return aptoideNonAtomicQuery(sqlQuery, null);
	}
	
	/**
	 * aptoideAtomicQuery, serves as a basis for all aptoide db's queries with no arguments,
	 * 				handles transactions
	 * 
	 * @param String sqlQuery
	 * 
	 * @return Cursor querysResults
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	private Cursor aptoideAtomicQuery(String sqlQuery){
		return aptoideAtomicQuery(sqlQuery, null);
	}
	
	
	
	/**
	 * anyReposInUse, checks if there are any managed repos in use
	 * 
	 * @return boolean
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public boolean anyReposInUse(){
		
		String selectReposInUse = "SELECT count(*)"
							+" FROM "+Constants.TABLE_REPOSITORY
							+" WHERE "+Constants.KEY_REPO_IN_USE+"='"+Constants.DB_TRUE+"';";
		
		boolean anyReposInUse = false;
		
		db.beginTransaction();
		Cursor reposInUseCursor = null;
		try{
			reposInUseCursor = aptoideNonAtomicQuery(selectReposInUse);
			db.setTransactionSuccessful();
			db.endTransaction();
			
			reposInUseCursor.moveToFirst();
			anyReposInUse = (reposInUseCursor.getInt(Constants.COLUMN_FIRST) > 0?true:false);
			reposInUseCursor.close();
			return anyReposInUse;
		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
			e.printStackTrace();
			return false;
			
		}
		
	}
	
	
	
	/**
	 * repoIsManaged, checks if repo referenced by this hashid is already managed
	 * 
	 * @param int repoHashid
	 * 
	 * @return boolean
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public boolean repoIsManaged(int repoHashid){
		
		String selectRepo = "SELECT count(*)"
							+" FROM "+Constants.TABLE_REPOSITORY
							+" WHERE "+Constants.KEY_REPO_HASHID+"='"+repoHashid+"';";
		
		boolean repoIsManaged = false;
		
		db.beginTransaction();
		Cursor repoCursor = null;
		try{
			repoCursor = aptoideNonAtomicQuery(selectRepo);
			db.setTransactionSuccessful();
			db.endTransaction();
			
			repoCursor.moveToFirst();
			repoIsManaged = (repoCursor.getInt(Constants.COLUMN_FIRST) == Constants.DB_TRUE?true:false);
			repoCursor.close();
			return repoIsManaged;
		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
			e.printStackTrace();
			return false;
			
		}
		
	}
	
	
	
	/**
	 * getReposDisplayInfo, retrieves a list of all known repos
	 * 					   with display relevant information (uri, inUse, Login if required)
	 * 
	 * @return ViewDisplayListRepos list of stores with it's logins
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayListRepos getReposDisplayInfo(){

		final int REPO_HASHID = Constants.COLUMN_FIRST;
		final int URI = Constants.COLUMN_SECOND;
		final int IN_USE = Constants.COLUMN_THIRD;
		final int SIZE = Constants.COLUMN_FOURTH;
		
		final int LOGIN_REPO_HASHID = Constants.COLUMN_FIRST;
		final int USERNAME = Constants.COLUMN_SECOND;
		final int PASSWORD = Constants.COLUMN_THIRD;

		ViewDisplayListRepos listRepos = null;
		
		String selectRepos = "SELECT "+Constants.KEY_REPO_HASHID+","+Constants.KEY_REPO_URI
									  +","+Constants.KEY_REPO_IN_USE+","+Constants.KEY_REPO_SIZE
							+" FROM "+Constants.TABLE_REPOSITORY+";";
		ViewDisplayRepo repo;
		Cursor reposCursor;
		
		String selectLogins = "SELECT *"
							 +" FROM "+Constants.TABLE_LOGIN+";";
		ViewLogin login;
		Cursor loginsCursor;

		
		db.beginTransaction();
		try{
			reposCursor = aptoideNonAtomicQuery(selectRepos);
			loginsCursor = aptoideNonAtomicQuery(selectLogins);

			db.setTransactionSuccessful();
			db.endTransaction();
			
			listRepos = new ViewDisplayListRepos(reposCursor.getCount());
			/**  storeHashid, arrayIndex */
			HashMap<Integer,Integer> indexMap = new HashMap<Integer, Integer>(reposCursor.getCount());

			reposCursor.moveToFirst();
			
			if(reposCursor.getCount() == 0){
				reposCursor.close();
				loginsCursor.close();
				return listRepos;
			}
			
			do{
				repo = new ViewDisplayRepo(reposCursor.getInt(REPO_HASHID), reposCursor.getString(URI), (reposCursor.getInt(IN_USE)==Constants.DB_TRUE?true:false), reposCursor.getInt(SIZE) );				
				listRepos.addRepo(repo);
				indexMap.put(reposCursor.getInt(REPO_HASHID), reposCursor.getPosition());
			}while(reposCursor.moveToNext());
			reposCursor.close();

			loginsCursor.moveToFirst();
			
			if(loginsCursor.getCount() == 0){
				loginsCursor.close();
				return listRepos;
			}
			
			do{
				login = new ViewLogin(loginsCursor.getString(USERNAME),loginsCursor.getString(PASSWORD));
				listRepos.getRepo(indexMap.get(loginsCursor.getInt(LOGIN_REPO_HASHID))).setLogin(login);
			}while(loginsCursor.moveToNext());
			loginsCursor.close();

		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
		}
		return listRepos;		
	}
	
	/**
	 * getCategoriesDisplayInfo, retrieves a hierarquical list of categories
	 *  
	 * @return ViewDisplayCategory top category with its childs
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayCategory getCategoriesDisplayInfo(){
		
		final int CATEGORY_HASHID = Constants.COLUMN_FIRST;
		final int CATEGORY_NAME = Constants.COLUMN_SECOND;
		final int CATEGORY_APPS = Constants.COLUMN_THIRD;
		
		final int OTHERS_APPS = Constants.COLUMN_SECOND;
		
		String selectApplicationsSubCategories = "SELECT C."+Constants.KEY_CATEGORY_HASHID+", C."+Constants.KEY_CATEGORY_NAME+", A."+Constants.DISPLAY_CATEGORY_APPS
												+" FROM (SELECT * FROM "+Constants.TABLE_CATEGORY
														+" WHERE "+Constants.KEY_CATEGORY_HASHID
														+" IN (SELECT "+Constants.KEY_SUB_CATEGORY_CHILD
															+" FROM "+Constants.TABLE_SUB_CATEGORY
															+" WHERE "+Constants.KEY_SUB_CATEGORY_PARENT+"="+Constants.CATEGORY_HASHID_APPLICATIONS+")) C"
												+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID
																			+", count("+Constants.KEY_APP_CATEGORY_APP_FULL_HASHID+") AS "+Constants.DISPLAY_CATEGORY_APPS
																		+" FROM "+Constants.TABLE_APP_CATEGORY
																		+" WHERE "+Constants.KEY_APP_CATEGORY_APP_FULL_HASHID
																				+" IN (SELECT "+Constants.KEY_APPLICATION_FULL_HASHID
																					+" FROM "+Constants.TABLE_APPLICATION
																					+" WHERE "+Constants.KEY_APPLICATION_REPO_HASHID
																						+" IN "+"(SELECT "+Constants.KEY_REPO_HASHID
																								+" FROM "+Constants.TABLE_REPOSITORY
																								+" WHERE "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+")) "
																		+" GROUP BY "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID+") A;";
		
		String selectGamesSubCategories = "SELECT C."+Constants.KEY_CATEGORY_HASHID+", C."+Constants.KEY_CATEGORY_NAME+", A."+Constants.DISPLAY_CATEGORY_APPS
										+" FROM (SELECT * FROM "+Constants.TABLE_CATEGORY
												+" WHERE "+Constants.KEY_CATEGORY_HASHID
												+" IN (SELECT "+Constants.KEY_SUB_CATEGORY_CHILD
														+" FROM "+Constants.TABLE_SUB_CATEGORY
														+" WHERE "+Constants.KEY_SUB_CATEGORY_PARENT+"="+Constants.CATEGORY_HASHID_GAMES+")) C"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID
																	+", count("+Constants.KEY_APP_CATEGORY_APP_FULL_HASHID+") AS "+Constants.DISPLAY_CATEGORY_APPS
															+" FROM "+Constants.TABLE_APP_CATEGORY
															+" WHERE "+Constants.KEY_APP_CATEGORY_APP_FULL_HASHID
																	+" IN (SELECT "+Constants.KEY_APPLICATION_FULL_HASHID
																		+" FROM "+Constants.TABLE_APPLICATION
																		+" WHERE "+Constants.KEY_APPLICATION_REPO_HASHID
																			+" IN "+"(SELECT "+Constants.KEY_REPO_HASHID
																					+" FROM "+Constants.TABLE_REPOSITORY
																					+" WHERE "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+")) "
															+" GROUP BY "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID+") A;";

		String selectOthersApplications = "SELECT "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID
												+", count("+Constants.KEY_APP_CATEGORY_APP_FULL_HASHID+") AS "+Constants.DISPLAY_CATEGORY_APPS
										+" FROM "+Constants.TABLE_APP_CATEGORY
										+" WHERE "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID+"="+Constants.CATEGORY_HASHID_OTHERS
										+" AND "+Constants.KEY_APP_CATEGORY_APP_FULL_HASHID
										+" IN (SELECT "+Constants.KEY_APPLICATION_FULL_HASHID
											+" FROM "+Constants.TABLE_APPLICATION
											+" WHERE "+Constants.KEY_APPLICATION_REPO_HASHID
												+" IN "+"(SELECT "+Constants.KEY_REPO_HASHID
														+" FROM "+Constants.TABLE_REPOSITORY
														+" WHERE "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+")) "
										+" GROUP BY "+Constants.KEY_APP_CATEGORY_CATEGORY_HASHID+";";
		
		
		ViewDisplayCategory topCategory = new ViewDisplayCategory("TOP", Constants.TOP_CATEGORY, 0);
		
		db.beginTransaction();
		try{
			Cursor cursorApplicationsSubCategories = aptoideNonAtomicQuery(selectApplicationsSubCategories);
			Cursor cursorGamesSubCategories = aptoideNonAtomicQuery(selectGamesSubCategories);
			Cursor cursorOthersApplications = aptoideNonAtomicQuery(selectOthersApplications);
			
			db.setTransactionSuccessful();
			db.endTransaction();

			if(cursorOthersApplications.getCount() != Constants.EMPTY_INT){
				cursorOthersApplications.moveToFirst();
				topCategory.addSubCategory(new ViewDisplayCategory(Constants.CATEGORY_OTHERS, Constants.CATEGORY_HASHID_OTHERS
																, cursorOthersApplications.getInt(OTHERS_APPS)));
			}
			cursorOthersApplications.close();
			
			ViewDisplayCategory category;

			
			ViewDisplayCategory applications = new ViewDisplayCategory(Constants.CATEGORY_APPLICATIONS, Constants.CATEGORY_HASHID_APPLICATIONS, 0);
			cursorApplicationsSubCategories.moveToFirst();
			do{
				if(cursorApplicationsSubCategories.getInt(CATEGORY_APPS) != Constants.EMPTY_INT){
					category = new ViewDisplayCategory(cursorApplicationsSubCategories.getString(CATEGORY_NAME), cursorApplicationsSubCategories.getInt(CATEGORY_HASHID)
													, cursorApplicationsSubCategories.getInt(CATEGORY_APPS));
					applications.addSubCategory(category);
				}
			}while(cursorApplicationsSubCategories.moveToNext());
			cursorApplicationsSubCategories.close();
			topCategory.addSubCategory(applications);

			
			ViewDisplayCategory games = new ViewDisplayCategory(Constants.CATEGORY_GAMES, Constants.CATEGORY_HASHID_GAMES, 0);
			cursorGamesSubCategories.moveToFirst();
			do{
				if(cursorGamesSubCategories.getInt(CATEGORY_APPS) != Constants.EMPTY_INT){
					category = new ViewDisplayCategory(cursorGamesSubCategories.getString(CATEGORY_NAME), cursorGamesSubCategories.getInt(CATEGORY_HASHID)
													, cursorGamesSubCategories.getInt(CATEGORY_APPS));
					games.addSubCategory(category);
				}
			}while(cursorGamesSubCategories.moveToNext());
			cursorGamesSubCategories.close();
			topCategory.addSubCategory(games);
			
		}catch (Exception e) {
			if(db.inTransaction()){
				db.endTransaction();
			}
			// TODO: handle exception
			e.printStackTrace();
		}
		return topCategory;
	}
	
	/**
	 * getInstalledAppsDisplayInfo, retrieves a list of all installed apps
	 *  
	 * @return ViewDisplayListApps list of installed apps
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayListApps getInstalledAppsDisplayInfo(){
		
		final int APP_NAME = Constants.COLUMN_FIRST;
		final int APP_HASHID = Constants.COLUMN_SECOND;
		final int INSTALLED_VERSION_NAME = Constants.COLUMN_THIRD;
		final int INSTALLED_VERSION_CODE = Constants.COLUMN_FOURTH;
		final int UP_TO_DATE_VERSION_NAME = Constants.COLUMN_FIFTH;
		final int UP_TO_DATE_VERSION_CODE = Constants.COLUMN_SIXTH;
		final int DOWNGRADE_VERSION_NAME = Constants.COLUMN_SEVENTH;
		final int DOWNGRADE_VERSION_CODE = Constants.COLUMN_EIGTH;
		
		ViewDisplayListApps installedApps = null;
		ViewDisplayApplication app;							
		
		String selectInstalledApps = "SELECT I."+Constants.KEY_APP_INSTALLED_NAME+",I."+Constants.KEY_APP_INSTALLED_HASHID
											+",I."+Constants.KEY_APP_INSTALLED_VERSION_NAME+",I."+Constants.KEY_APP_INSTALLED_VERSION_CODE
											+",U."+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME+",U."+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
											+",D."+Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME+",D."+Constants.DISPLAY_APP_DOWNGRADE_VERSION_CODE
									+" FROM "+Constants.TABLE_APP_INSTALLED+" I"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_PACKAGE_NAME
																	+",MAX("+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
																	+","+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
															 +" FROM "+Constants.TABLE_APPLICATION
															 +" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME+") U"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_PACKAGE_NAME
																	+",MIN("+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_DOWNGRADE_VERSION_CODE
																	+","+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME
															 +" FROM "+Constants.TABLE_APPLICATION
															 +" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME+") D"
									+" ORDER BY I."+Constants.KEY_APP_INSTALLED_NAME+";";
		
		db.beginTransaction();
		try{
			Cursor appsCursor = aptoideNonAtomicQuery(selectInstalledApps);

			db.setTransactionSuccessful();
			db.endTransaction();

			installedApps = new ViewDisplayListApps();
			
			appsCursor.moveToFirst();
			do{
				app = new ViewDisplayApplication(appsCursor.getInt(APP_HASHID), appsCursor.getString(APP_NAME), appsCursor.getString(INSTALLED_VERSION_NAME)
												 ,(appsCursor.getInt(UP_TO_DATE_VERSION_CODE) <= 0?false:(appsCursor.getInt(UP_TO_DATE_VERSION_CODE) > appsCursor.getInt(INSTALLED_VERSION_CODE)))
												 , appsCursor.getString(UP_TO_DATE_VERSION_NAME)
												 ,(appsCursor.getInt(DOWNGRADE_VERSION_CODE) <= 0?false:(appsCursor.getInt(DOWNGRADE_VERSION_CODE) < appsCursor.getInt(INSTALLED_VERSION_CODE)))
												 , appsCursor.getString(DOWNGRADE_VERSION_NAME));
				installedApps.addApp(app);

			}while(appsCursor.moveToNext());
			appsCursor.close();
	
		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
			e.printStackTrace();
		}
		return installedApps;
	}
	
	
	/**
	 * getAvailableAppsDisplayInfo, retrieves a list of all available apps
	 * 
	 * @param int offset, number of row to start from
	 * @param int range, number of rows to list
	 * 
	 * @return ViewDisplayListApps list of available apps
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayListApps getAvailableAppsDisplayInfo(int offset, int range){
		
		final int APP_NAME = Constants.COLUMN_FIRST;
		final int APP_HASHID = Constants.COLUMN_SECOND;
//		final int PACKAGE_NAME = Constants.COLUMN_THIRD;
//		final int UP_TO_DATE_VERSION_CODE = Constants.COLUMN_FOURTH;
		final int UP_TO_DATE_VERSION_NAME = Constants.COLUMN_FIFTH;
		final int STARS = Constants.COLUMN_SIXTH;
		final int DOWNLOADS = Constants.COLUMN_SEVENTH;
		
		ViewDisplayListApps availableApps = null;
		ViewDisplayApplication app;							
		
		String selectAvailableApps = "SELECT A."+Constants.KEY_APPLICATION_NAME+", A."+Constants.KEY_APPLICATION_HASHID+", A."+Constants.KEY_APPLICATION_PACKAGE_NAME
											+", MAX(A."+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
											+", A."+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
											+", S."+Constants.KEY_STATS_STARS+", S."+Constants.KEY_STATS_DOWNLOADS
									+" FROM (SELECT *"
											+" FROM "+Constants.TABLE_APPLICATION
											+" WHERE "+Constants.KEY_APPLICATION_REPO_HASHID
												+" IN "+"(SELECT "+Constants.KEY_REPO_HASHID
														+" FROM "+Constants.TABLE_REPOSITORY
														+" WHERE "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+")) A"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_STATS_APP_FULL_HASHID+", "+Constants.KEY_STATS_STARS+", "+Constants.KEY_STATS_DOWNLOADS
															+" FROM "+Constants.TABLE_STATS_INFO+") S"
									+" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME
									+" ORDER BY "+Constants.KEY_APPLICATION_NAME
									+" LIMIT ?"
									+" OFFSET ?;";
		String[] selectAvailableAppsArgs = new String[] {Integer.toString(range), Integer.toString(offset)};
		
		db.beginTransaction();
		try{
			Cursor appsCursor = aptoideNonAtomicQuery(selectAvailableApps, selectAvailableAppsArgs);

			db.setTransactionSuccessful();
			db.endTransaction();

			availableApps = new ViewDisplayListApps();
			
			appsCursor.moveToFirst();
			
			if(appsCursor.getCount() == 0){
				appsCursor.close();
				return availableApps;
			}
			
			do{																			
				app = new ViewDisplayApplication(appsCursor.getInt(APP_HASHID), appsCursor.getString(APP_NAME), appsCursor.getFloat(STARS)
												, appsCursor.getInt(DOWNLOADS), appsCursor.getString(UP_TO_DATE_VERSION_NAME));
				availableApps.addApp(app);

			}while(appsCursor.moveToNext());
			appsCursor.close();
	
		}catch (Exception e) {
			if(db.inTransaction()){
				db.endTransaction();
			}
			// TODO: handle exception
			e.printStackTrace();
		}
		return availableApps;
	}
	
	
	/**
	 * getUpdatableAppsDisplayInfo, retrieves a list of all apps' updates
	 * 
	 * @param int offset, number of row to start from
	 * @param int range, number of rows to list
	 * 
	 * @return ViewDisplayListApps list of apps available updates
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayListApps getUpdatableAppsDisplayInfo(){
		final int APP_NAME = Constants.COLUMN_FIRST;
		final int APP_HASHID = Constants.COLUMN_SECOND;
		final int INSTALLED_VERSION_NAME = Constants.COLUMN_THIRD;
		final int INSTALLED_VERSION_CODE = Constants.COLUMN_FOURTH;
		final int UP_TO_DATE_VERSION_NAME = Constants.COLUMN_FIFTH;
		final int UP_TO_DATE_VERSION_CODE = Constants.COLUMN_SIXTH;
		final int UPDATE_STARS = Constants.COLUMN_SEVENTH;
		final int UPDATE_DOWNLOADS = Constants.COLUMN_EIGTH;
		
		ViewDisplayListApps updatableApps = null;
		ViewDisplayApplication app;	
		
		String selectUpdatableApps = "SELECT I."+Constants.KEY_APP_INSTALLED_NAME+",I."+Constants.KEY_APP_INSTALLED_HASHID
											+",I."+Constants.KEY_APP_INSTALLED_VERSION_NAME+",I."+Constants.KEY_APP_INSTALLED_VERSION_CODE
											+",U."+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME+",U."+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
											+",S."+Constants.KEY_STATS_STARS+",S."+Constants.KEY_STATS_DOWNLOADS
									+" FROM "+Constants.TABLE_APP_INSTALLED+" I"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_PACKAGE_NAME+", "+Constants.KEY_APPLICATION_FULL_HASHID
																	+",MAX("+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
																	+","+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
															+" FROM (SELECT *"
																	+" FROM "+Constants.TABLE_APPLICATION
																	+" WHERE "+Constants.KEY_APPLICATION_REPO_HASHID
																		+" IN "+"(SELECT "+Constants.KEY_REPO_HASHID
																				+" FROM "+Constants.TABLE_REPOSITORY
																				+" WHERE "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+"))"	
															+" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME+") U"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_STATS_APP_FULL_HASHID+", "+Constants.KEY_STATS_STARS+", "+Constants.KEY_STATS_DOWNLOADS
															+" FROM "+Constants.TABLE_STATS_INFO
															+" GROUP BY "+Constants.KEY_APPLICATION_FULL_HASHID+") S"
									+" ORDER BY I."+Constants.KEY_APP_INSTALLED_NAME+";";
		
		db.beginTransaction();
		try{
			Cursor appsCursor = aptoideNonAtomicQuery(selectUpdatableApps);

			db.setTransactionSuccessful();
			db.endTransaction();

			updatableApps = new ViewDisplayListApps();
			
			appsCursor.moveToFirst();
			do{
				if( (appsCursor.getInt(UP_TO_DATE_VERSION_CODE) <= 0?false:(appsCursor.getInt(UP_TO_DATE_VERSION_CODE) > appsCursor.getInt(INSTALLED_VERSION_CODE))) ){
					app = new ViewDisplayApplication(appsCursor.getInt(APP_HASHID), appsCursor.getString(APP_NAME), appsCursor.getString(INSTALLED_VERSION_NAME)
							 , appsCursor.getString(UP_TO_DATE_VERSION_NAME), appsCursor.getFloat(UPDATE_STARS), appsCursor.getInt(UPDATE_DOWNLOADS));
					updatableApps.addApp(app);
				}

			}while(appsCursor.moveToNext());
			appsCursor.close();
	
		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
		}
		return updatableApps;
	}
	
	
	/**
	 * getAppSearchResults, retrieves a list of available Apps matching the search string parameters
	 * 
	 * @param String searchString, search parameters
	 * 
	 * @return ViewDisplayListApps, list of available Apps matching the search string parameters
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */	
	public ViewDisplayListApps getAppSearchResultsDisplayInfo(String searchString){
		
		final int APP_NAME = Constants.COLUMN_FIRST;
		final int APP_HASHID = Constants.COLUMN_SECOND;
//		final int PACKAGE_NAME = Constants.COLUMN_THIRD;
//		final int UP_TO_DATE_VERSION_CODE = Constants.COLUMN_FOURTH;
		final int UP_TO_DATE_VERSION_NAME = Constants.COLUMN_FIFTH;
		final int STARS = Constants.COLUMN_SIXTH;
		final int DOWNLOADS = Constants.COLUMN_SEVENTH;
		
		ViewDisplayListApps availableApps = null;
		ViewDisplayApplication app;							
		
		String selectAvailableApps = "SELECT A."+Constants.KEY_APPLICATION_NAME+", A."+Constants.KEY_APPLICATION_HASHID+", A."+Constants.KEY_APPLICATION_PACKAGE_NAME
											+", MAX(A."+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
											+", A."+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
											+", S."+Constants.KEY_STATS_STARS+", S."+Constants.KEY_STATS_DOWNLOADS
									+" FROM (SELECT *"
											+" FROM "+Constants.TABLE_APPLICATION
											+" WHERE "+Constants.KEY_APPLICATION_NAME+" LIKE '%"+searchString+"%'"
											+" AND "+Constants.KEY_APPLICATION_REPO_HASHID
												+" IN "+"(SELECT "+Constants.KEY_REPO_HASHID
														+" FROM "+Constants.TABLE_REPOSITORY
														+" WHERE "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+")) A"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_STATS_APP_FULL_HASHID+", "+Constants.KEY_STATS_STARS+", "+Constants.KEY_STATS_DOWNLOADS
															+" FROM "+Constants.TABLE_STATS_INFO+") S"
									+" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME
									+" ORDER BY "+Constants.KEY_APPLICATION_NAME+";";
		
		db.beginTransaction();
		try{
			Cursor appsCursor = aptoideNonAtomicQuery(selectAvailableApps);

			db.setTransactionSuccessful();
			db.endTransaction();

			availableApps = new ViewDisplayListApps();
			
			appsCursor.moveToFirst();
			
			if(appsCursor.getCount() == 0){
				appsCursor.close();
				return availableApps;
			}
			
			do{																			
				app = new ViewDisplayApplication(appsCursor.getInt(APP_HASHID), appsCursor.getString(APP_NAME), appsCursor.getFloat(STARS)
												, appsCursor.getInt(DOWNLOADS), appsCursor.getString(UP_TO_DATE_VERSION_NAME));
				availableApps.addApp(app);

			}while(appsCursor.moveToNext());
			appsCursor.close();
	
		}catch (Exception e) {
			if(db.inTransaction()){
				db.endTransaction();
			}
			// TODO: handle exception
			e.printStackTrace();
		}
		return availableApps;
	}
	
	
	/**
	 * getIconsDownloadInfo, retrieves a list of icons Download Info
	 * 
	 * @param int offset, number of row to start from
	 * @param int range, number of rows to list
	 * 
	 * @return ViewDownloadInfo list of icons Download Info
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */		//TODO refactor data transport -> move ArrayList to a full data transport object with it's size properly initialized
	public ArrayList<ViewDownloadInfo> getIconsDownloadInfo(ViewRepository repo, int offset, int range){
		
		final int ICONS_PATH = Constants.COLUMN_FIRST;
		final int REMOTE_PATH_TAIL = Constants.COLUMN_FIRST;
		final int APP_HASHID = Constants.COLUMN_SECOND;
		final int APP_NAME = Constants.COLUMN_THIRD;
		
		
		ArrayList<ViewDownloadInfo> iconsInfo = null;
		ViewDownloadInfo iconInfo;							
		
		String selectRepoIconsPath = "SELECT "+Constants.KEY_REPO_ICONS_PATH
									+" FROM "+Constants.TABLE_REPOSITORY
									+" WHERE "+Constants.KEY_REPO_HASHID+"="+repo.getHashid()+";";
		
		String selectIconDownloadInfo = "SELECT I."+Constants.KEY_ICON_REMOTE_PATH_TAIL+",A."+Constants.KEY_APPLICATION_HASHID
											+",A."+Constants.KEY_APPLICATION_NAME
									+" FROM "+Constants.TABLE_ICON_INFO+" I"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_FULL_HASHID
																	+","+Constants.KEY_APPLICATION_REPO_HASHID
																	+","+Constants.KEY_APPLICATION_HASHID
																	+","+Constants.KEY_APPLICATION_NAME
																	+","+Constants.KEY_APPLICATION_TIMESTAMP
															 +" FROM "+Constants.TABLE_APPLICATION
															 +" GROUP BY "+Constants.KEY_APPLICATION_HASHID+") A"
										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_REPO_HASHID
															 +" FROM "+Constants.TABLE_REPOSITORY
															 +" GROUP BY "+Constants.KEY_REPO_HASHID+")"
									+" ORDER BY A."+Constants.KEY_APPLICATION_NAME
									+" LIMIT ?"
									+" OFFSET ?;";
		String[] selectIconDownloadInfoArgs = new String[] {Integer.toString(range), Integer.toString(offset)};
		
		db.beginTransaction();
		try{
			Cursor repoCursor = aptoideNonAtomicQuery(selectRepoIconsPath);
			Cursor iconsCursor = aptoideNonAtomicQuery(selectIconDownloadInfo, selectIconDownloadInfoArgs);

			db.setTransactionSuccessful();
			db.endTransaction();

			repoCursor.moveToFirst();
			String repoIconsPath = repoCursor.getString(ICONS_PATH);
			repoCursor.close();
			
			iconsInfo = new ArrayList<ViewDownloadInfo>(iconsCursor.getCount());
			
			iconsCursor.moveToFirst();
			do{
				iconInfo = new ViewDownloadInfo(repoIconsPath+iconsCursor.getString(REMOTE_PATH_TAIL), iconsCursor.getString(APP_NAME)
												, iconsCursor.getInt(APP_HASHID), EnumDownloadType.ICON);
				iconsInfo.add(iconInfo);

			}while(iconsCursor.moveToNext());
			iconsCursor.close();
	
		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
			e.printStackTrace();
		}
		return iconsInfo;
	}
	
	
	/**
	 * appInRepo, checks if the app/version referenced by appHashid is present on the repo referenced by the repoHashid
	 * 
	 * @param int repoHashid
	 * @param int appHashid
	 * 
	 * @return boolean is app in repo?
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public boolean appInRepo(int repoHashid, int appHashid){
		final int COUNT = Constants.COLUMN_FIRST;
		
		String selectAppInRepo = "SELECT count(distinct A."+Constants.KEY_APPLICATION_HASHID+")"
								+" FROM "
									+"(SELECT "+Constants.KEY_APPLICATION_HASHID+", "+Constants.KEY_APPLICATION_REPO_HASHID
									+" FROM "+Constants.TABLE_APPLICATION+") A"
								+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_REPO_HASHID+", "+Constants.KEY_REPO_IN_USE
															+" FROM "+Constants.TABLE_REPOSITORY
															+" GROUP BY "+Constants.KEY_REPO_HASHID+") R"
								+" WHERE "+Constants.KEY_REPO_HASHID+"="+repoHashid+" AND "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE
									+" AND "+Constants.KEY_APPLICATION_HASHID+"="+appHashid+";";
		
		Cursor appInRepoCursor = aptoideAtomicQuery(selectAppInRepo);
		appInRepoCursor.moveToFirst();
		
		boolean appInRepo = (appInRepoCursor.getInt(COUNT)==Constants.DB_TRUE?true:false);
		
		appInRepoCursor.close();
		Log.d("Aptoide-appInRepo", "appInrepo? "+appInRepo);
		
		return appInRepo;
	}
	
	
	/**
	 * getAppRepo, get a repo that contains the app/version represented by the appHashid
	 * 
	 * @param int appHashid
	 * 
	 * @return ViewRepository the repository
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewRepository getAppRepo(int appHashid){
		final int REPO_HASHID = Constants.COLUMN_FIRST;
		final int URI = Constants.COLUMN_SECOND;
//		final int IN_USE = Constants.COLUMN_THIRD;
		final int SIZE = Constants.COLUMN_FOURTH;
		final int BASE_PATH = Constants.COLUMN_FIFTH;
		final int ICONS_PATH = Constants.COLUMN_SIXTH;
		final int SCREENS_PATH = Constants.COLUMN_SEVENTH;
		final int DELTA = Constants.COLUMN_EIGTH;
		
//		final int LOGIN_REPO_HASHID = Constants.COLUMN_FIRST;
		final int USERNAME = Constants.COLUMN_SECOND;
		final int PASSWORD = Constants.COLUMN_THIRD;

		String selectRepo = "SELECT "+Constants.KEY_REPO_HASHID+", "+Constants.KEY_REPO_URI+", "+Constants.KEY_REPO_IN_USE+", "+Constants.KEY_REPO_SIZE
									+", "+Constants.KEY_REPO_BASE_PATH+", "+Constants.KEY_REPO_ICONS_PATH+", "+Constants.KEY_REPO_SCREENS_PATH
									+", "+Constants.KEY_REPO_DELTA
							+" FROM "+Constants.TABLE_REPOSITORY;
		if(appInRepo(Constants.APPS_REPO_HASHID, appHashid)){
				selectRepo+=" WHERE "+Constants.KEY_REPO_HASHID+"="+Constants.APPS_REPO_HASHID+";";
		}else{
				selectRepo+=" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_HASHID
												+", "+Constants.KEY_APPLICATION_REPO_HASHID
												+" FROM "+Constants.TABLE_APPLICATION
												+" GROUP BY "+Constants.KEY_APPLICATION_HASHID+") "
							+" WHERE "+Constants.KEY_APPLICATION_HASHID+"="+appHashid+" AND "+Constants.KEY_REPO_IN_USE+"="+Constants.DB_TRUE+";";
		}
		Cursor repoCursor = aptoideAtomicQuery(selectRepo);
		if(repoCursor.getCount()==Constants.EMPTY_INT){
			repoCursor.close();
			return null;		//TODO refactor null object
		}else{
			repoCursor.moveToFirst();
		}
		
		String selectLogin = "SELECT *"
							 +" FROM "+Constants.TABLE_LOGIN
							 +" WHERE "+Constants.KEY_LOGIN_REPO_HASHID+"="+repoCursor.getInt(REPO_HASHID)+";";
		Cursor loginCursor = aptoideAtomicQuery(selectLogin);
		
		ViewRepository repo = new ViewRepository(repoCursor.getString(URI), repoCursor.getInt(SIZE), repoCursor.getString(BASE_PATH)
												, repoCursor.getString(ICONS_PATH), repoCursor.getString(SCREENS_PATH), repoCursor.getString(DELTA));
		
		if( !(loginCursor.getCount() == Constants.EMPTY_INT) ){
			loginCursor.moveToFirst();
			ViewLogin login = new ViewLogin(loginCursor.getString(USERNAME),loginCursor.getString(PASSWORD));
			repo.setLogin(login);
		}
		loginCursor.close();
		Log.d("Aptoide-getAppRepo", "appRepo: "+repo);
		
		repoCursor.close();
		
		return repo;
	}
	
	/**
	 * getAppDisplayInfo, retrieves information about all the versions of the app referenced by the appHashid
	 * 
	 * @param int appHashid
	 * 
	 * @return ViewDisplayAppVersionsInfo app versions info
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayAppVersionsInfo getAppDisplayInfo(int appHashid){
		
		final int APP_FULL_HASHID = Constants.COLUMN_FIRST;
		final int VERSION_CODE = Constants.COLUMN_SECOND;
		final int VERSION_NAME = Constants.COLUMN_THIRD;
		final int APP_NAME = Constants.COLUMN_FOURTH;
		final int LIKES = Constants.COLUMN_FIFTH;
		final int DISLIKES = Constants.COLUMN_SIXTH;
		final int STARS = Constants.COLUMN_SEVENTH;
		final int DOWNLOADS = Constants.COLUMN_EIGTH;
		final int DESCRIPTION = Constants.COLUMN_NINTH;
		
		final int INSTALLED_VERSION_CODE = Constants.COLUMN_FIRST;
		final int INSTALLED_VERSION_NAME = Constants.COLUMN_SECOND;
		final int INSTALLED_NAME = Constants.COLUMN_THIRD;
		
		int installedVersionCode = Constants.EMPTY_INT;
		
		ViewDisplayAppVersionsInfo appVersions = new ViewDisplayAppVersionsInfo();
		ViewDisplayAppVersionInfo appVersion;
		
		String selectAppVersions = "SELECT A."+Constants.KEY_APPLICATION_FULL_HASHID+", A."+Constants.KEY_APPLICATION_VERSION_CODE
											+", A."+Constants.KEY_APPLICATION_VERSION_NAME+", A."+Constants.KEY_APPLICATION_NAME
											+", S."+Constants.KEY_STATS_LIKES+", S."+Constants.KEY_STATS_DISLIKES+", S."+Constants.KEY_STATS_STARS
											+", S."+Constants.KEY_STATS_DOWNLOADS+", E."+Constants.KEY_EXTRA_DESCRIPTION
									+" FROM (SELECT * " 
											+" FROM "+Constants.TABLE_APPLICATION
											+" WHERE "+Constants.KEY_APPLICATION_PACKAGE_NAME+"="
											+" (SELECT DISTINCT "+Constants.KEY_APPLICATION_PACKAGE_NAME
												+" FROM "+Constants.TABLE_APPLICATION
												+" WHERE "+Constants.KEY_APPLICATION_HASHID+"="+appHashid
											+" UNION SELECT DISTINCT "+Constants.KEY_APP_INSTALLED_PACKAGE_NAME
												+" FROM "+Constants.TABLE_APP_INSTALLED
												+" WHERE "+Constants.KEY_APP_INSTALLED_HASHID+"="+appHashid+")) A"
									+" NATURAL LEFT JOIN (SELECT * FROM "+Constants.TABLE_STATS_INFO+") S"
									+" NATURAL LEFT JOIN (SELECT * FROM "+Constants.TABLE_EXTRA_INFO+") E"
//									+" NATURAL LEFT JOIN (SELECT "
//															+Constants.KEY_SCREEN_APP_FULL_HASHID+", COUNT("+Constants.KEY_SCREEN_REMOTE_PATH_TAIL+")"
//															+" FROM "+Constants.TABLE_SCREEN_INFO+")) C"
									+" ORDER BY A."+Constants.KEY_APPLICATION_VERSION_CODE+" DESC;";
		
		String selectInstalledAppVersion = " SELECT "+Constants.KEY_APP_INSTALLED_VERSION_CODE+", "+Constants.KEY_APP_INSTALLED_VERSION_NAME
													+", "+Constants.KEY_APP_INSTALLED_NAME
											+" FROM "+Constants.TABLE_APP_INSTALLED
											+" WHERE "+Constants.KEY_APPLICATION_PACKAGE_NAME+"="
													+" (SELECT DISTINCT "+Constants.KEY_APPLICATION_PACKAGE_NAME
														+" FROM "+Constants.TABLE_APPLICATION
														+" WHERE "+Constants.KEY_APPLICATION_HASHID+"="+appHashid
												+" UNION "
													+"SELECT DISTINCT "+Constants.KEY_APP_INSTALLED_PACKAGE_NAME
														+" FROM "+Constants.TABLE_APP_INSTALLED
														+" WHERE "+Constants.KEY_APP_INSTALLED_HASHID+"="+appHashid+");";
		
		db.beginTransaction();
		try{
			Cursor appVersionsCursor = aptoideNonAtomicQuery(selectAppVersions);
			Cursor installedVersionCursor = aptoideNonAtomicQuery(selectInstalledAppVersion);
			
			db.setTransactionSuccessful();
			db.endTransaction();
						
			if(installedVersionCursor.getCount() != Constants.EMPTY_INT){
				installedVersionCursor.moveToFirst();
				installedVersionCode = installedVersionCursor.getInt(INSTALLED_VERSION_CODE);
			}
			
			if(appVersionsCursor.getCount() == Constants.EMPTY_INT){
				if(installedVersionCode == Constants.EMPTY_INT){
					//TODO throw exception (Unrecognized appHashid)
				}else{
					appVersion = new ViewDisplayAppVersionInfo(installedVersionCursor.getString(INSTALLED_NAME), installedVersionCursor.getString(INSTALLED_VERSION_NAME)
																, installedVersionCode, Constants.EMPTY_INT, true);
					appVersions.addAppVersionInfo(appVersion);
				}
				installedVersionCursor.close();
			}else{
				installedVersionCursor.close();
				appVersionsCursor.moveToFirst();
				
				do{
					appVersion = new ViewDisplayAppVersionInfo(appVersionsCursor.getString(APP_NAME), appVersionsCursor.getString(VERSION_NAME)
																, appVersionsCursor.getInt(VERSION_CODE), appVersionsCursor.getInt(APP_FULL_HASHID)
																, (appVersionsCursor.getInt(VERSION_CODE)==installedVersionCode?true:false));
					appVersion.setStats(new ViewDisplayAppVersionStats(appVersionsCursor.getInt(APP_FULL_HASHID), appVersionsCursor.getInt(LIKES)
																		, appVersionsCursor.getInt(DISLIKES), appVersionsCursor.getFloat(STARS)
																		, appVersionsCursor.getInt(DOWNLOADS)));
					if(appVersionsCursor.getString(DESCRIPTION)!=null){
						ViewDisplayAppVersionExtras extras = new ViewDisplayAppVersionExtras(appVersionsCursor.getInt(APP_FULL_HASHID), appVersionsCursor.getString(DESCRIPTION));
						appVersion.setExtras(extras);
					}
					appVersions.addAppVersionInfo(appVersion);
				}while(appVersionsCursor.moveToNext());
			}
			appVersionsCursor.close();
			
		}catch (Exception e) {
			if(db.inTransaction()){
				db.endTransaction();
			}
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return appVersions;
	}
	
	/**
	 * getAppDownload, retrieves the best download information for the app referenced by the appHashid
	 * 
	 * @param int appHashid
	 * 
	 * @return ViewDownload downloadInfo
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDownload getAppDownload(int appHashid){
		
		final int REMOTE_PATH_TAIL = Constants.COLUMN_FIRST;
		final int SIZE = Constants.COLUMN_SECOND;
		final int MD5HASH = Constants.COLUMN_THIRD;
		final int REMOTE_PATH_BASE = Constants.COLUMN_FOURTH;
		final int APP_NAME = Constants.COLUMN_FIFTH;
		final int REPO_HASHID = Constants.COLUMN_SIXTH;
		
		final int USERNAME = Constants.COLUMN_FIRST;
		final int PASSWORD = Constants.COLUMN_SECOND;
		
		ViewDownload appDownload = null;
		
		String selectAppDownloadInfo = "SELECT D."+Constants.KEY_DOWNLOAD_REMOTE_PATH_TAIL+", D."+Constants.KEY_DOWNLOAD_SIZE+", D."+Constants.KEY_DOWNLOAD_MD5HASH
											+", R."+Constants.KEY_REPO_BASE_PATH+", A."+Constants.KEY_APPLICATION_NAME+", R."+Constants.KEY_REPO_HASHID
									+" FROM (SELECT "+Constants.KEY_APPLICATION_FULL_HASHID+", "+Constants.KEY_APPLICATION_REPO_HASHID+", "+Constants.KEY_APPLICATION_NAME
											+" FROM "+Constants.TABLE_APPLICATION
											+" WHERE "+Constants.KEY_APPLICATION_HASHID+"="+appHashid;
		if(appInRepo(Constants.APPS_REPO_HASHID, appHashid)){
			selectAppDownloadInfo +=			" AND "+Constants.KEY_APPLICATION_REPO_HASHID+"="+Constants.APPS_REPO_HASHID+") A";
		}else{
			selectAppDownloadInfo +=			") A";
		}
			selectAppDownloadInfo += " NATURAL LEFT JOIN (SELECT * FROM "+Constants.TABLE_DOWNLOAD_INFO+") D"
									+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_REPO_BASE_PATH+", "+Constants.KEY_REPO_HASHID+") R;";
		
		db.beginTransaction();
		try{
			Cursor appDownloadInfoCursor = aptoideNonAtomicQuery(selectAppDownloadInfo);
						
			if(appDownloadInfoCursor.getCount() == Constants.EMPTY_INT){
					//TODO throw exception (Unrecognized appHashid)
			}
			appDownloadInfoCursor.moveToFirst();
			
			String selectLogin = "SELECT *"
				 +" FROM "+Constants.TABLE_LOGIN
				 +" WHERE "+Constants.KEY_LOGIN_REPO_HASHID+"="+appDownloadInfoCursor.getInt(REPO_HASHID)+";";
			Cursor loginCursor = aptoideNonAtomicQuery(selectLogin);
			
			db.setTransactionSuccessful();
			db.endTransaction();

			if(loginCursor.getCount() == Constants.EMPTY_INT){
				appDownload = serviceData.getManagerDownloads().prepareApkDownload(appHashid, appDownloadInfoCursor.getString(APP_NAME)
									, appDownloadInfoCursor.getString(REMOTE_PATH_BASE), appDownloadInfoCursor.getString(REMOTE_PATH_TAIL)
									, appDownloadInfoCursor.getInt(SIZE), appDownloadInfoCursor.getString(MD5HASH));
			}else{
				ViewLogin login = new ViewLogin(loginCursor.getString(USERNAME), loginCursor.getString(PASSWORD));
				
				appDownload = serviceData.getManagerDownloads().prepareApkDownload(appHashid, appDownloadInfoCursor.getString(APP_NAME)
						, appDownloadInfoCursor.getString(REMOTE_PATH_BASE), appDownloadInfoCursor.getString(REMOTE_PATH_TAIL), login
						, appDownloadInfoCursor.getInt(SIZE), appDownloadInfoCursor.getString(MD5HASH));
			}
			loginCursor.close();
			appDownloadInfoCursor.close();
			
		}catch (Exception e) {
			if(db.inTransaction()){
				db.endTransaction();
			}
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return appDownload;
	}
	
	
	/**
	 * getInstalledAppPackageName, retrieves the packageName for the installed app referenced by the appHashid
	 * 
	 * @param int appHashid
	 * 
	 * @return String packageName
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */	
	public String getInstalledAppPackageName(int appHashid){
		final int PACKAGE_NAME = Constants.COLUMN_FIRST;
		
		String packageName = null;
		
		String selectInstalledApp = "SELECT "+Constants.KEY_APP_INSTALLED_PACKAGE_NAME
									+" FROM "+Constants.TABLE_APP_INSTALLED
									+" WHERE "+Constants.KEY_APP_INSTALLED_HASHID+"="+appHashid+";";
		
		Cursor installedAppCursor = aptoideAtomicQuery(selectInstalledApp);
		
		if(installedAppCursor.getCount() == Constants.EMPTY_INT){
			//TODO raise exception app not installed
		}
		
		installedAppCursor.moveToFirst();
		packageName = installedAppCursor.getString(PACKAGE_NAME);
		installedAppCursor.close();
		
		return packageName;
	}
	
	//TODO rest of activity support classes (depends on activity Layout definitions, for performance reasons)
	
}