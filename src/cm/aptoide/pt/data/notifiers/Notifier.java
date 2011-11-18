/**
 * Notifier,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.notifiers;

import java.util.ArrayList;

 /**
 * Notifier, models a notification
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Notifier {

	private EnumNotifierTypes notifierType;
	private String actionsTargetName;	//Name and version if it's relevant
	private String targetsUniqueId;		//Combined from target's id (pkg.vercode in case of an apk)
	private int uniqueId; 				//Combined from target's unique id and the action ongoing
	private int currentProgress;
	private int progressCompletion;
	private boolean completed;
	
	private ArrayList<Notifier> subNotifiers;

	public Notifier(EnumNotifierTypes notifierType, String actionsTargetName, String targetsUniqueId, int progressCompletion) {
		this.notifierType = notifierType;
		this.actionsTargetName = actionsTargetName;
		this.targetsUniqueId = targetsUniqueId;
		this.uniqueId = (targetsUniqueId+notifierType.toString()).hashCode();
		this.currentProgress = 0;
		this.progressCompletion = progressCompletion;
		this.completed = false;
		
		this.subNotifiers = new ArrayList<Notifier>();
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

	public ArrayList<Notifier> getSubNotifiers() {
		return subNotifiers;
	}
	
	public void addSubNotifier(Notifier subNotifier){
		this.subNotifiers.add(subNotifier);
	}
	
	public Notifier removeSubNotifier(int position){
		return this.subNotifiers.remove(position);
	}
	
	public boolean removeSubNotifier(Notifier subNotifier){
		return this.subNotifiers.remove(subNotifier);
	}

	public void setSubNotifiers(ArrayList<Notifier> subNotifiers) {
		this.subNotifiers = subNotifiers;
	}

	public EnumNotifierTypes getNotifierType() {
		return notifierType;
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
		if(subNotifiers.isEmpty()){
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
		if(!subNotifiers.isEmpty()){
				this.progressCompletion = 0;
				this.currentProgress = 0;
			for (Notifier subNotifier : this.subNotifiers) {
				this.progressCompletion += subNotifier.progressCompletion;
				this.currentProgress += subNotifier.currentProgress;
			}
		}
		if(this.currentProgress >= this.progressCompletion){
			setCompleted(true);	//TODO send Message to listening clients in Managing class if iscompleted after this invocation
		}
		return this.currentProgress;
	}
	
	
	public void clean(){
		this.notifierType = null;
		this.actionsTargetName = null;
		this.targetsUniqueId = null;
		this.uniqueId = 0;
		this.currentProgress = 0;
		this.progressCompletion = 0;
		this.completed = false;
		
		this.subNotifiers = null;
	}
	
	public void reuse(EnumNotifierTypes notifierType, String actionsTargetName, String targetsUniqueId, int progressCompletion) {
		this.notifierType = notifierType;
		this.actionsTargetName = actionsTargetName;
		this.targetsUniqueId = targetsUniqueId;
		this.uniqueId = (targetsUniqueId+notifierType.toString()).hashCode();
		this.currentProgress = 0;
		this.progressCompletion = progressCompletion;
		this.completed = false;
		
		this.subNotifiers = new ArrayList<Notifier>();
	}
		
}
