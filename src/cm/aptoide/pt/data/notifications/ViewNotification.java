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

 /**
 * ViewNotification, models a notification
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewNotification {

	private EnumNotificationTypes notificationType;
	private String actionsTargetName;	//Name and version if it's relevant
	private String targetsUniqueId;		//Combined from target's id (pkg.vercode in case of an apk)
	private int uniqueId; 				//Combined from target's unique id and the action ongoing
	private int currentProgress;
	private int progressCompletion;
	private boolean completed;
	
	private ArrayList<ViewNotification> subNotifications;

	public ViewNotification(EnumNotificationTypes notificationType, String actionsTargetName, String targetsUniqueId, int progressCompletion) {
		this.notificationType = notificationType;
		this.actionsTargetName = actionsTargetName;
		this.targetsUniqueId = targetsUniqueId;
		this.uniqueId = (targetsUniqueId+notificationType.toString()).hashCode();
		this.currentProgress = 0;
		this.progressCompletion = progressCompletion;
		this.completed = false;
		
		this.subNotifications = new ArrayList<ViewNotification>();
	}

	public void setCurrentProgress(int currentProgress) {
		this.currentProgress = currentProgress;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
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

	public String getTargetsUniqueId() {
		return targetsUniqueId;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public int getProgressCompletion() {
		return progressCompletion;
	}
	
	
	public int incrementProgress(int increment){
		if(subNotifications.isEmpty()){
			this.currentProgress += increment;
			if(this.currentProgress >= this.progressCompletion){
				setCompleted(true);	//TODO send Message to listening clients in Managing class if iscompleted after this invocation
			}
			return this.currentProgress;
		}else{
			return -1;	//TODO raise exception
		}
	}

	public int getCurrentProgress(){
		if(!subNotifications.isEmpty()){
				this.progressCompletion = 0;
				this.currentProgress = 0;
			for (ViewNotification subNotification : this.subNotifications) {
				this.progressCompletion += subNotification.progressCompletion;
				this.currentProgress += subNotification.currentProgress;
			}
		}
		if(this.currentProgress >= this.progressCompletion){
			setCompleted(true);	//TODO send Message to listening clients in Managing class if iscompleted after this invocation
		}
		return this.currentProgress;
	}
	
	
	public void clean(){
		this.notificationType = null;
		this.actionsTargetName = null;
		this.targetsUniqueId = null;
		this.uniqueId = 0;
		this.currentProgress = 0;
		this.progressCompletion = 0;
		this.completed = false;
		
		this.subNotifications = null;
	}
	
	public void reuse(EnumNotificationTypes notificationType, String actionsTargetName, String targetsUniqueId, int progressCompletion) {
		this.notificationType = notificationType;
		this.actionsTargetName = actionsTargetName;
		this.targetsUniqueId = targetsUniqueId;
		this.uniqueId = (targetsUniqueId+notificationType.toString()).hashCode();
		this.currentProgress = 0;
		this.progressCompletion = progressCompletion;
		this.completed = false;
		
		this.subNotifications = new ArrayList<ViewNotification>();
	}
		
}
