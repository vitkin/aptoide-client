/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5Handler {
	
	private static MessageDigest digest;
	
	
	public static String md5Calc(File f){
		int i;
		String md5hash = "";		
		byte[] buffer = new byte[1024];
		int read = 0;
		try {
			digest = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(f);
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			md5hash = bigInt.toString(16);
		}catch(Exception e) {
			e.printStackTrace();
			return md5hash;
		}
		
		if(md5hash.length() != 33){
			String tmp = "";
			for(i=1; i< (33-md5hash.length()); i++){
				tmp = tmp.concat("0");
			}
			md5hash = tmp.concat(md5hash);
		}
		
		return md5hash;
	}

}
