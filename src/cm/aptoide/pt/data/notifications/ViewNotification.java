/**
 * ViewNotification,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.notifications;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

 /**
 * ViewNotification, models a notification
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewNotification {

	private ManagerNotifications managerNotifications;
	
	private EnumNotificationTypes notificationType;
	/** Name and version if it's relevant */
	private String actionsTargetName;
	private int targetsHashid;
	/** Combined from target's unique id and the action ongoing */
	private int notificationHashid; 	
	private AtomicInteger currentProgress;
	private AtomicInteger notificationUpdateProgress;
	private int progressCompletionTarget;
	private boolean completed;
	
	private ViewNotification parentNotification;
	private ArrayList<ViewNotification> subNotifications;

	public ViewNotification(ManagerNotifications managerNotifications, EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid, int progressCompletionTarget) {
		this.managerNotifications = managerNotifications;
		
		this.notificationType = notificationType;
		this.actionsTargetName = actionsTargetName;
		this.targetsHashid = targetsHashid;
		this.notificationHashid = (targetsHashid+"|"+notificationType).hashCode();
		this.notificationUpdateProgress = new AtomicInteger(0);
		this.currentProgress = new AtomicInteger(0);
		this.progressCompletionTarget = progressCompletionTarget;
		this.completed = false;
		
		this.parentNotification = null;		//TODO refactor null to nullobject
		this.subNotifications = new ArrayList<ViewNotification>();
	}
	
	public ViewNotification(ManagerNotifications managerNotifications, EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid) {
		this(managerNotifications, notificationType, actionsTargetName, targetsHashid, 1);
	}
	
	
	public void setProgressCompletionTarget(int target){
		this.progressCompletionTarget = target;
		managerNotifications.setNotification(this);
	}

	public void progressSetCurrent(int currentProgress) {
//		if(currentProgress > this.currentProgress.get()){
//			//TODO raise exception
//		}
		if((this.progressCompletionTarget/20) > 0 && ((currentProgress-this.notificationUpdateProgress.get())/(this.progressCompletionTarget/20)) >= 1){
			this.notificationUpdateProgress.set(currentProgress);
			managerNotifications.setNotification(this);
		}
		this.currentProgress.set(currentProgress);
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		if(completed){
			this.currentProgress.set(this.progressCompletionTarget);
			dissociateFromParent();
			managerNotifications.dismissNotification(targetsHashid);
		}
		this.completed = completed;
	}
	
	private void setParentNotification(ViewNotification parent){
		this.parentNotification = parent;
	}
	
	public void dissociateFromParent(){
		if(this.parentNotification != null){
			this.parentNotification.removeSubNotification(this);
		}
	}

	public ArrayList<ViewNotification> getSubNotifications() {
		return subNotifications;
	}
	
	public void addSubNotification(ViewNotification subNotification){
		subNotification.setParentNotification(this);
		this.subNotifications.add(subNotification);
	}
	
	public ViewNotification removeSubNotification(int position){
		return this.subNotifications.remove(position);
	}
	
	public boolean removeSubNotification(ViewNotification subNotification){
		return this.subNotifications.remove(subNotification);
	}

	public void removeSubNotifications(){
		for (ViewNotification subNotification : subNotifications) {
			subNotification.dissociateFromParent();
		}
		return;
	}
	
	public void setSubNotifications(ArrayList<ViewNotification> subNotifications) {
		this.subNotifications = subNotifications;
	}

	public EnumNotificationTypes getNotificationType() {
		return notificationType;
	}

	public String getActionsTargetName() {
		return actionsTargetName;
	}

	public int getTargetsHashid() {
		return targetsHashid;
	}

	public int getNotificationHashid() {
		return notificationHashid;
	}

	public int getProgressCompletionTarget() {
		return progressCompletionTarget;
	}
	
	
	public int incrementProgress(int increment){
		if(subNotifications.isEmpty()){
			if(!isCompleted()){
//				Log.d("Aptoide-ViewNotification", "current progress: "+this.currentProgress.get()+" increment: "+increment+" notificationprogress: "+this.notificationUpdateProgress.get()+" progressUpdateTrigger: "+(this.progressCompletionTarget/20)+" progressCompletionTarget: "+this.progressCompletionTarget);
				if((this.progressCompletionTarget/20) > 0 && (((this.currentProgress.get()+increment)-this.notificationUpdateProgress.get())/(this.progressCompletionTarget/20) >= 1)){
//					Log.d("Aptoide-ViewNotification", "updating notification");
					this.currentProgress.addAndGet(increment);
					this.notificationUpdateProgress.set(this.currentProgress.get());
					managerNotifications.setNotification(this);
				}else{
//					Log.d("Aptoide-ViewNotification", "not updating notification");
					this.currentProgress.addAndGet(increment);
				}
				if(this.currentProgress.get() >= this.progressCompletionTarget){
					Log.d("Aptoide-ViewNotification", "Download target size: "+progressCompletionTarget+" current progress: "+currentProgress);
					setCompleted(true);	//TODO send Message to listening clients in Managing class if iscompleted after this invocation
				}
			}
			return this.currentProgress.get();
		}else{
			return -1;	//TODO raise exception
		}
	}
	
	public int getCurrentProgress(){
		if(!subNotifications.isEmpty()){
				this.progressCompletionTarget = 0;
				this.currentProgress.set(0);
			for (ViewNotification subNotification : this.subNotifications) {
				this.progressCompletionTarget += subNotification.progressCompletionTarget;
				this.currentProgress.addAndGet(subNotification.getCurrentProgress());
			}
		}
		if(this.currentProgress.get() >= this.progressCompletionTarget){
			setCompleted(true);	//TODO send Message to listening clients in Managing class if iscompleted after this invocation
		}
		return this.currentProgress.get();
	}
	
	
	public void clean(){
		this.managerNotifications = null;
		this.notificationType = null;
		this.actionsTargetName = null;
		this.targetsHashid = 0;
		this.notificationHashid = 0;
		this.notificationUpdateProgress = null;
		this.currentProgress = null;
		this.progressCompletionTarget = 0;
		this.completed = false;
		
		dissociateFromParent();
		removeSubNotifications();
		this.subNotifications = null;
	}
	
	public void reuse(ManagerNotifications managerNotifications, EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid, int progressCompletionTarget) {
		this.managerNotifications = managerNotifications;
		this.notificationType = notificationType;
		this.actionsTargetName = actionsTargetName;
		this.targetsHashid = targetsHashid;
		this.notificationHashid = (targetsHashid+notificationType.toString()).hashCode();
		this.currentProgress = new AtomicInteger(0);
		this.progressCompletionTarget = progressCompletionTarget;
		this.completed = false;
		
		this.subNotifications = new ArrayList<ViewNotification>();
	}
	
	public void reuse(ManagerNotifications managerNotifications, EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid){
		reuse(managerNotifications, notificationType, actionsTargetName, targetsHashid, 1);
	}
		
}
