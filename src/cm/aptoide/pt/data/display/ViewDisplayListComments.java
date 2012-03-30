/**
 * ViewListComments,		part of Aptoide's data model
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

package cm.aptoide.pt.data.display;

import java.util.LinkedList;

import android.os.Parcel;
import android.os.Parcelable;

 /**
 * ViewDisplayListComments, models a list of Comments,
 * 			 				maintains insertion order
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayListComments extends LinkedList<ViewDisplayComment> implements Parcelable{ 

	private static final long serialVersionUID = -1243164744886675592L;

	
	public ViewDisplayListComments() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder listApps = new StringBuilder("Comments: ");
		for (int i=0; i<size(); i++) {
			listApps.append("\n comment:   "+get(i));
		}
		return listApps.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayListComments> CREATOR = new Parcelable.Creator<ViewDisplayListComments>() {
		public ViewDisplayListComments createFromParcel(Parcel in) {
			return new ViewDisplayListComments(in);
		}

		public ViewDisplayListComments[] newArray(int size) {
			return new ViewDisplayListComments[size];
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

	private ViewDisplayListComments(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		int size = this.size();
		
		out.writeInt(size);
		
		for(int i=0; i<size; i++){
			out.writeParcelable(this.get(i),flags);
		}
	}

	public void readFromParcel(Parcel in) {
		this.clear();
		
		int size = in.readInt();
		
		for(int i=0; i<size; i++){
			this.add((ViewDisplayComment) in.readParcelable(ViewDisplayComment.class.getClassLoader()));
		}
	}
	
}
