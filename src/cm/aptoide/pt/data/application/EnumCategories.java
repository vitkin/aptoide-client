/*
 * EnumCategories		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.application;


/**
 * EnumCategories, typesafes application Categories in Aptoide  
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public enum EnumCategories {
	Comics,
	Communication,
	Entertainment,
	Finance,
	Health,
	Lifestyle,
	Multimedia,
	News_And_Weather,
	Productivity,
	Reference,
	Shopping,
	Social,
	Sports,
	Themes,
	Tools,
	Travel,
	Demo,
	Software_Libraries,
	Arcade_And_Action,
	Brain_And_Puzzle,
	Cards_And_Casino,
	Casual;
	
	public static EnumCategories reverseOrdinal(int ordinal){
		return values()[ordinal];
	}
}
