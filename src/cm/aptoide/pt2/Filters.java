/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt2;

public class Filters {

	public static enum Screens {
		notfound,small,normal,large,xlarge;
		
		static Screens lookup(String screen){
			try{
				return valueOf(screen);
			}catch (Exception e) {
				return notfound;
			}
			
			
		}
		
	}
	
	public static enum Ages {
		All,Mature;
		static Ages lookup(String age){
			try{
				return valueOf(age);
			}catch (Exception e) {
				return All;
			}
			
			
		}
	}
}
