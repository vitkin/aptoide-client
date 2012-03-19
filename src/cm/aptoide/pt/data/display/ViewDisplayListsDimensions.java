/**
 * ViewDisplayListsDimensions,	 auxiliary class to Aptoide's ServiceData
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
package cm.aptoide.pt.data.display;

import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.data.util.Constants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ViewDisplayListsDimensions, models the aptoide client's display lists dimensions
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayListsDimensions implements Parcelable{
	private int pageSize;
	private int cacheSize;
	private int increaseTrigger;
	private int decreaseTrigger;
	private int fastReset;
//	private int triggerMargin;
//	private int trigger;
	
	
	public ViewDisplayListsDimensions(ViewScreenDimensions screenDimensions) {
		pageSize = ((screenDimensions.getHeight()>screenDimensions.getWidth()?screenDimensions.getHeight():screenDimensions.getWidth())
					/Math.round(Constants.DISPLAY_SIZE_COMPARATOR*screenDimensions.getDensity()))
					*Constants.DISPLAY_LISTS_PAGE_SIZE_MULTIPLIER;
		
//		increaseTrigger = pageSize*Constants.DISPLAY_LISTS_PAGE_INCREASE_OFFSET_TRIGGER_PROPORTION_LEVEL/Constants.DISPLAY_LISTS_PAGE_SIZE_MULTIPLIER;
//		
//		decreaseTrigger = pageSize*Constants.DISPLAY_LISTS_PAGE_DECREASE_OFFSET_TRIGGER_PROPORTION_LEVEL/Constants.DISPLAY_LISTS_PAGE_SIZE_MULTIPLIER;
//		
//		triggerMargin = decreaseTrigger - increaseTrigger;
		
		cacheSize = pageSize*Constants.DISPLAY_LISTS_CACHE_SIZE_PAGES_MULTIPLIER;
		
//		trigger = cacheSize*2;
		
		increaseTrigger = pageSize*Constants.DISPLAY_LISTS_PAGE_INCREASE_TRIGGER_MULTIPLIER;
		
		decreaseTrigger = pageSize*Constants.DISPLAY_LISTS_PAGE_DECREASE_TRIGGER_MULTIPLIER;
		
		fastReset = pageSize*Constants.DISPLAY_LISTS_FAST_RESET_INCREASE_TRIGGER_MULTIPLIER;
	}
	
	
	public int getIncreaseTrigger() {
		return increaseTrigger;
	}

	public int getDecreaseTrigger() {
		return decreaseTrigger;
	}
//	
//	public int getTriggerMargin() {
//		return triggerMargin;
//	}
//	
//	public int getTrigger() {
//		return trigger;
//	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCacheSize() {
		return cacheSize;
	}
	
	public int getFastReset(){
//		return increaseTrigger*Constants.DISPLAY_LISTS_FAST_RESET_INCREASE_TRIGGER_MULTIPLIER;
		return fastReset;
	}



	@Override
	public String toString() {
		return "pageSize: "+pageSize+" cacheSize: "+cacheSize+" increaseTrigger: "+increaseTrigger+" decreaseTrigger: "+decreaseTrigger+" fastReset: "+fastReset;//+" triggerMargin: "+triggerMargin;
//		return "pageSize: "+pageSize+" cacheSize: "+cacheSize+" trigger: "+trigger;
	}

	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayListsDimensions> CREATOR = new Parcelable.Creator<ViewDisplayListsDimensions>() {
			        public ViewDisplayListsDimensions createFromParcel(Parcel in) {
			            return new ViewDisplayListsDimensions(in);
			        }

			        public ViewDisplayListsDimensions[] newArray(int size) {
			            return new ViewDisplayListsDimensions[size];
			        }
			    };
	
	/** 
	 * we're annoyingly forced to create this even if we clearly don't need it,
	 *  so we just use the default return 0
	 *  
	 *  @return 0
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	private ViewDisplayListsDimensions(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(pageSize);
		out.writeInt(cacheSize);
		out.writeInt(increaseTrigger);
		out.writeInt(decreaseTrigger);
		out.writeInt(fastReset);
//		out.writeInt(trigger);
//		out.writeInt(triggerMargin);
	}
	
	public void readFromParcel(Parcel in) {
		pageSize = in.readInt();
		cacheSize = in.readInt();
		increaseTrigger = in.readInt();
		decreaseTrigger = in.readInt();
		fastReset = in.readInt();
//		trigger = in.readInt();
//		triggerMargin = in.readInt();
	}
	
}
