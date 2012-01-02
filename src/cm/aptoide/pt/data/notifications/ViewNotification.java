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

 /**
 * ViewNotification, models a notification
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewNotification {

	private EnumNotificationTypes notificationType;
	/** Name and version if it's relevant */
	private String actionsTargetName;
	private int targetsHashid;
	/** Combined from target's unique id and the action ongoing */
	private int notificationHashid; 	
	private AtomicInteger currentProgress;
	private int progressCompletionTarget;
	private boolean completed;
	
	private ArrayList<ViewNotification> subNotifications;

	public ViewNotification(EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid, int progressCompletionTarget) {
		this.notificationType = notificationType;
		this.actionsTargetName = actionsTargetName;
		this.targetsHashid = targetsHashid;
		this.notificationHashid = (targetsHashid+"|"+notificationType).hashCode();
		this.currentProgress = new AtomicInteger(0);
		this.progressCompletionTarget = progressCompletionTarget;
		this.completed = false;
		
		this.subNotifications = new ArrayList<ViewNotification>();
	}
	
	public ViewNotification(EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid) {
		this(notificationType, actionsTargetName, targetsHashid, 1);
	}
	
	
	public void setProgressCompletionTarget(int target){
		this.progressCompletionTarget = target;
	}

	public void progressSetCurrent(int currentProgress) {
//		if(currentProgress > this.currentProgress.get()){
//			//TODO raise exception
//		}
		this.currentProgress.set(currentProgress);
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		if(completed){
			this.currentProgress.set(this.progressCompletionTarget);
		}
		this.completed = completed;
	}

	public ArrayList<ViewNotification> getSubNotifications() {
		return subNotifications;
	}
	
	public void addSubNotification(ViewNotification subNotification){
		this.subNotifications.add(subNotification);
	}
	
	public ViewNotification removeSubNotification(int position){
		return this.subNotifications.remove(position);
	}
	
	public boolean removeSubNotification(ViewNotification subNotification){
		return this.subNotifications.remove(subNotification);
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
				this.currentProgress.addAndGet(increment);
				if(this.currentProgress.get() >= this.progressCompletionTarget){
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
		this.notificationType = null;
		this.actionsTargetName = null;
		this.targetsHashid = 0;
		this.notificationHashid = 0;
		this.currentProgress = null;
		this.progressCompletionTarget = 0;
		this.completed = false;
		
		this.subNotifications = null;
	}
	
	public void reuse(EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid, int progressCompletionTarget) {
		this.notificationType = notificationType;
		this.actionsTargetName = actionsTargetName;
		this.targetsHashid = targetsHashid;
		this.notificationHashid = (targetsHashid+notificationType.toString()).hashCode();
		this.currentProgress = new AtomicInteger(0);
		this.progressCompletionTarget = progressCompletionTarget;
		this.completed = false;
		
		this.subNotifications = new ArrayList<ViewNotification>();
	}
	
	public void reuse(EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid){
		reuse(notificationType, actionsTargetName, targetsHashid, 1);
	}
		
}
