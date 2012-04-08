/**
 * EnumAgeRating,	auxiliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.preferences;

/**
 * EnumAgeRating, typeSafes age rating
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public enum EnumAgeRating {
	All,
	Pre_Teen,
	Teen,
	Mature,
	No_Filter,
	unrecognized;
	
	public static EnumAgeRating safeValueOf(String name){
		EnumAgeRating tag;
		if(name.equals("Pre-Teen")){
			tag = EnumAgeRating.Pre_Teen;
		}else if(name.equals("No Filter")){
			tag = EnumAgeRating.No_Filter;
		}else{
			try {
				tag = EnumAgeRating.valueOf(name);
			} catch (Exception e1) {
				tag = EnumAgeRating.unrecognized;
			}
		}
		return tag;
	}
	
	public static EnumAgeRating reverseOrdinal(int ordinal){
		return values()[ordinal];
	}
}
