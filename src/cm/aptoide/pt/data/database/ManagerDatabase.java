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
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.views.ViewAppComment;
import cm.aptoide.pt.data.views.ViewApplication;
import cm.aptoide.pt.data.views.ViewCategory;
import cm.aptoide.pt.data.views.ViewDisplayApplication;
import cm.aptoide.pt.data.views.ViewDisplayListApps;
import cm.aptoide.pt.data.views.ViewDisplayListRepos;
import cm.aptoide.pt.data.views.ViewDisplayRepository;
import cm.aptoide.pt.data.views.ViewDownloadInfo;
import cm.aptoide.pt.data.views.ViewExtraInfo;
import cm.aptoide.pt.data.views.ViewIconInfo;
import cm.aptoide.pt.data.views.ViewListIds;
import cm.aptoide.pt.data.views.ViewLogin;
import cm.aptoide.pt.data.views.ViewRepository;
import cm.aptoide.pt.data.views.ViewStatsInfo;

/**
 * ManagerDatabase, manages aptoide's sqlite data persistence
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerDatabase {
	
	private static SQLiteDatabase db = null;
	
	/**
	 * ManagerDatabase Constructor, opens Aptoide's database or creates it if it doens't exist yet 
	 *
	 * @param Context context, Aptoide's activity context
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ManagerDatabase(Context context) {
		if(db == null){
			db = context.openOrCreateDatabase(Constants.DATABASE, 0, null);
			db.beginTransaction();
			
			db.execSQL(Constants.CREATE_TABLE_REPOSITORY);
			db.execSQL(Constants.CREATE_TABLE_LOGIN);
			db.execSQL(Constants.CREATE_TABLE_APPLICATION);
			db.execSQL(Constants.CREATE_TABLE_CATEGORY);
			db.execSQL(Constants.CREATE_TABLE_SUB_CATEGORY);
			db.execSQL(Constants.CREATE_TABLE_APP_CATEGORY);
			db.execSQL(Constants.CREATE_TABLE_APP_INSTALLED);
			db.execSQL(Constants.CREATE_TABLE_ICON_INFO);
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
		}else if(!db.isOpen()){
			db = context.openOrCreateDatabase(Constants.DATABASE, 0, null);
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
				if(insertRepository.insert(repository.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
				if(repository.requiresLogin()){
					loginsValues.add(repository.getLogin().getValues());
				}
			}
			insertRepository.close();
			
			InsertHelper insertLogin = new InsertHelper(db, Constants.TABLE_LOGIN);
			for (ContentValues loginValues : loginsValues) {
				if(insertLogin.insert(loginValues) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertLogin.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: send to errorHandler the exception
		}finally{
			db.endTransaction();
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
			if(db.insert(Constants.TABLE_REPOSITORY, null, repository.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			if(repository.requiresLogin()){
				if(db.insert(Constants.TABLE_LOGIN, null, repository.getLogin().getValues()) == Constants.DB_ERROR){
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
	 * removeRepositories, handles multiple repositories removal
	 * 
	 * @param ViewListIds repoHashids
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public void removeRepositories(ViewListIds repoHashids){
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
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
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
		db.beginTransaction();
		try{
			InsertHelper insertApplication = new InsertHelper(db, Constants.TABLE_APPLICATION);
			ArrayList<ContentValues> appCategoriesRelations = new ArrayList<ContentValues>(applications.size());
			ContentValues appCategoryRelation;
			
			for (ViewApplication application : applications) {
				if(insertApplication.insert(application.getValues()) == Constants.DB_ERROR){
					//TODO throw exception;
				}
				appCategoryRelation = new ContentValues(Constants.NUMBER_OF_COLUMNS_APP_CATEGORY); //TODO check if this implicit object recycling works 
				appCategoryRelation.put(Constants.KEY_APP_CATEGORY_APP_FULL_HASHID, application.getFullHashid());
				appCategoryRelation.put(Constants.KEY_APP_CATEGORY_CATEGORY_HASHID, application.getCategoryHashid());
				appCategoriesRelations.add(appCategoryRelation);
				
			}
			insertApplication.close();
			
			InsertHelper insertAppCategoryRelation = new InsertHelper(db, Constants.TABLE_APP_CATEGORY);
			for (ContentValues appCategoryRelationValues : appCategoriesRelations) {
				if(insertAppCategoryRelation.insert(appCategoryRelationValues) == Constants.DB_ERROR){
					//TODO throw exception;
				}
			}
			insertAppCategoryRelation.close();
			
			db.setTransactionSuccessful();
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
		}finally{
			db.endTransaction();
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
		ContentValues appCategoryRelation;

		db.beginTransaction();
		try{
			if(db.insert(Constants.TABLE_APPLICATION ,null, application.getValues()) == Constants.DB_ERROR){
				//TODO throw exception;
			}
			appCategoryRelation = new ContentValues(Constants.NUMBER_OF_COLUMNS_APP_CATEGORY); //TODO check if this implicit object recycling works 
			appCategoryRelation.put(Constants.KEY_APP_CATEGORY_APP_FULL_HASHID, application.getFullHashid());
			appCategoryRelation.put(Constants.KEY_APP_CATEGORY_CATEGORY_HASHID, application.getCategoryHashid());

			if(db.insert(Constants.TABLE_APP_CATEGORY ,null, appCategoryRelation) == Constants.DB_ERROR){
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
	 * insertIconsInfo, handles single application's icon info insertion
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
	 * insertDownloadsInfo, handles multiple application's download info insertion
	 * 
	 * @param ArrayList<DownloadInfo> downloadsInfo
	 */
	public void insertDownloadsInfo(ArrayList<ViewDownloadInfo> downloadsInfo){
		db.beginTransaction();
		try{
			InsertHelper insertDownloadInfo = new InsertHelper(db, Constants.TABLE_DOWNLOAD_INFO);
			
			for (ViewDownloadInfo downloadInfo : downloadsInfo) {
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
	 * @param ViewDownloadInfo downloadInfo
	 */
	public void insertDownloadInfo(ViewDownloadInfo downloadInfo){
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
	 * @param ArrayList<ExtraInfo> extraInfos
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
		}catch (Exception e) {
			// TODO: *send to errorHandler the exception, possibly rollback first or find out what went wrong and deal with it and then call errorHandler*
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
	public void removeInstalledApplication(String appHashid){
		db.beginTransaction();
		try{
			if(db.delete(Constants.TABLE_APP_INSTALLED, Constants.KEY_APP_INSTALLED_HASHID+"="+appHashid, null) == Constants.DB_NO_CHANGES_MADE){
				//TODO throw exception;
			}
			
			db.setTransactionSuccessful();
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
	@SuppressWarnings("unused")
	private Cursor aptoideAtomicQuery(String sqlQuery){
		return aptoideAtomicQuery(sqlQuery, null);
	}
	
	
	
	/**
	 * getReposDisplayInfo, retrieves a list of all known repositories
	 * 					   with display relevant information (uri, inUse, Login if required)
	 * 
	 * @return ViewDisplayListRepos list of repos with it's logins
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
									  +Constants.KEY_REPO_IN_USE+","+Constants.KEY_REPO_SIZE
							+" FROM "+Constants.TABLE_REPOSITORY+";";
		ViewDisplayRepository repo;
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
			/**  repoHashid, arrayIndex */
			HashMap<Integer,Integer> indexMap = new HashMap<Integer, Integer>(reposCursor.getCount());

			reposCursor.moveToFirst();
			do{
				repo = new ViewDisplayRepository(reposCursor.getInt(REPO_HASHID), reposCursor.getString(URI), (reposCursor.getInt(IN_USE)==Constants.DB_TRUE?true:false), reposCursor.getInt(SIZE) );				
				listRepos.addRepo(repo);
				indexMap.put(reposCursor.getInt(REPO_HASHID), reposCursor.getPosition());
			}while(reposCursor.moveToNext());
			reposCursor.close();



			loginsCursor.moveToFirst();
			do{
				login = new ViewLogin(loginsCursor.getString(USERNAME),loginsCursor.getString(PASSWORD));
				listRepos.getRepo(indexMap.get(reposCursor.getInt(LOGIN_REPO_HASHID))).put(Constants.DISPLAY_REPO_LOGIN, login);
			}while(loginsCursor.moveToNext());
			loginsCursor.close();

		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
		}
		return listRepos;		
	}
	
	/**
	 * getInstalledApps, retrieves a list of all installed apps
	 * 
	 * @param int offset, number of row to start from
	 * @param int range, number of rows to list
	 * 
	 * @return ViewDisplayListApps list of installed apps
	 * 
	 * @author dsilveira
	 * @since 3.0
	 * 
	 */
	public ViewDisplayListApps getInstalledAppsDisplayInfo(int offset, int range){
		
		final int APP_NAME = Constants.COLUMN_FIRST;
		final int APP_HASHID = Constants.COLUMN_SECOND;
		final int INSTALLED_VERSION_NAME = Constants.COLUMN_THIRD;
		final int INSTALLED_VERSION_CODE = Constants.COLUMN_FOURTH;
		final int UP_TO_DATE_VERSION_NAME = Constants.COLUMN_FIFTH;
		final int UP_TO_DATE_VERSION_CODE = Constants.COLUMN_SIXTH;
		final int DOWNGRADE_VERSION_NAME = Constants.COLUMN_SEVENTH;
		final int DOWNGRADE_VERSION_CODE = Constants.COLUMN_EIGTH;
		
		ViewDisplayListApps installedApps = null;
		ViewDisplayApplication app;							//TODO check if this superQuery works as it's supposed to
		
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
									+" ORDER BY I."+Constants.KEY_APP_INSTALLED_NAME
									+" LIMIT ?"
									+" OFFSET ?;";
		String[] selectInstalledAppsArgs = new String[] {Integer.toString(range), Integer.toString(offset)};
		
		db.beginTransaction();
		try{
			Cursor appsCursor = aptoideNonAtomicQuery(selectInstalledApps, selectInstalledAppsArgs);

			db.setTransactionSuccessful();
			db.endTransaction();

			installedApps = new ViewDisplayListApps(appsCursor.getCount());
			
			appsCursor.moveToFirst();
			do{
				app = new ViewDisplayApplication(appsCursor.getInt(APP_HASHID), appsCursor.getString(APP_NAME), appsCursor.getString(INSTALLED_VERSION_NAME)
												 ,(appsCursor.getInt(UP_TO_DATE_VERSION_CODE) > appsCursor.getInt(INSTALLED_VERSION_CODE))
												 , appsCursor.getString(UP_TO_DATE_VERSION_NAME)
												 ,(appsCursor.getInt(DOWNGRADE_VERSION_CODE) < appsCursor.getInt(INSTALLED_VERSION_CODE))
												 , appsCursor.getString(DOWNGRADE_VERSION_NAME));
				installedApps.addApp(app);

			}while(appsCursor.moveToNext());
			appsCursor.close();
	
		}catch (Exception e) {
			db.endTransaction();
			// TODO: handle exception
		}
		return installedApps;
	}
	
	/**
	 * getAppsUpdates, retrieves a list of all apps' updates
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
	public ViewDisplayListApps getAppsUpdates(int offset, int range){	//TODO refactor from installedApps to appsUpdates
		return null; //TODO remove
//		final int APP_NAME = Constants.COLUMN_FIRST;
//		final int APP_HASHID = Constants.COLUMN_SECOND;
//		final int INSTALLED_VERSION_NAME = Constants.COLUMN_THIRD;
//		final int INSTALLED_VERSION_CODE = Constants.COLUMN_FOURTH;
//		final int UP_TO_DATE_VERSION_NAME = Constants.COLUMN_FIFTH;
//		final int UP_TO_DATE_VERSION_CODE = Constants.COLUMN_SIXTH;
//		final int DOWNGRADE_VERSION_NAME = Constants.COLUMN_SEVENTH;
//		final int DOWNGRADE_VERSION_CODE = Constants.COLUMN_EIGTH;
//		
//		ViewDisplayListApps installedApps = null;
//		ViewDisplayApplication app;							//TODO check if this superQuery works as it's supposed to
		
//		String selectInstalledApps = "SELECT I."+Constants.KEY_APP_INSTALLED_NAME+",I."+Constants.KEY_APP_INSTALLED_HASHID
//											+",I."+Constants.KEY_APP_INSTALLED_VERSION_NAME+",I."+Constants.KEY_APP_INSTALLED_VERSION_CODE
//											+",U."+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME+",U."+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
//											+",D."+Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME+",D."+Constants.DISPLAY_APP_DOWNGRADE_VERSION_CODE
//									+" FROM "+Constants.TABLE_APP_INSTALLED+" I"
//										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_PACKAGE_NAME
//																	+",MAX("+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_CODE
//																	+","+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
//															 +" FROM "+Constants.TABLE_APPLICATION
//															 +" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME+") U"
//										+" NATURAL LEFT JOIN (SELECT "+Constants.KEY_APPLICATION_PACKAGE_NAME
//																	+",MIN("+Constants.KEY_APPLICATION_VERSION_CODE+") AS "+Constants.DISPLAY_APP_DOWNGRADE_VERSION_CODE
//																	+","+Constants.KEY_APPLICATION_VERSION_NAME+" AS "+Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME
//															 +" FROM "+Constants.TABLE_APPLICATION
//															 +" GROUP BY "+Constants.KEY_APPLICATION_PACKAGE_NAME+") D"
//									+" ORDER BY I."+Constants.KEY_APP_INSTALLED_NAME
//									+" LIMIT ?"
//									+" OFFSET ?;";
//		String[] selectInstalledAppsArgs = new String[] {Integer.toString(range), Integer.toString(offset)};
//		
//		db.beginTransaction();
//		try{
//			Cursor appsCursor = aptoideNonAtomicQuery(selectInstalledApps, selectInstalledAppsArgs);
//
//			db.setTransactionSuccessful();
//			db.endTransaction();
//
//			installedApps = new ViewDisplayListApps(appsCursor.getCount());
//			
//			appsCursor.moveToFirst();
//			do{
//				app = new ViewDisplayApplication(appsCursor.getInt(APP_HASHID), appsCursor.getString(APP_NAME), appsCursor.getString(INSTALLED_VERSION_NAME)
//												 ,(appsCursor.getInt(UP_TO_DATE_VERSION_CODE) > appsCursor.getInt(INSTALLED_VERSION_CODE))
//												 , appsCursor.getString(UP_TO_DATE_VERSION_NAME)
//												 ,(appsCursor.getInt(DOWNGRADE_VERSION_CODE) < appsCursor.getInt(INSTALLED_VERSION_CODE))
//												 , appsCursor.getString(DOWNGRADE_VERSION_NAME));
//				installedApps.addApp(app);
//
//			}while(appsCursor.moveToNext());
//			appsCursor.close();
//	
//		}catch (Exception e) {
//			db.endTransaction();
//			// TODO: handle exception
//		}
//		return installedApps;
	}
	
	//TODO rest of activity support classes (depends on activity Layout definitions, for performance reasons)
	
}