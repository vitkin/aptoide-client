/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.webservices.taste;

import java.util.Locale;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
public enum EnumResponseTasteElement {
	
	RESPONSE, STATUS, LIKES, DISLIKES, ENTRY, USERNAME, USERIDHASH, TIMESTAMP, ERRORS, OK, FAIL;
	
	public static EnumResponseTasteElement valueOfToUpper(String name) {
		return EnumResponseTasteElement.valueOf(name.toUpperCase(Locale.US));
	}
	
}
