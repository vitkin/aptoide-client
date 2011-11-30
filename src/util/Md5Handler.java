/*
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
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
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Handler {
	
	private MessageDigest digest;
	
	public Md5Handler (){
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {e.printStackTrace();	}
	}
	
	public String md5Calc(File f){
		int i;
		String md5hash = null;		
		byte[] buffer = new byte[1024];
		int read = 0;
		
		try {
			InputStream is = new FileInputStream(f);
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			md5hash = bigInt.toString(16);
		}catch(Exception e) {}
		
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
