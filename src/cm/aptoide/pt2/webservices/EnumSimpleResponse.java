/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt2.webservices;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
public enum EnumSimpleResponse {
	
	RESPONSE, STATUS, ERRORS, ENTRY;
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static EnumSimpleResponse valueOfToUpper(String name) {
		EnumSimpleResponse[] array = EnumSimpleResponse.values();
		for(EnumSimpleResponse element: array){
			if(element.toString().equals(name))
				return element;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
