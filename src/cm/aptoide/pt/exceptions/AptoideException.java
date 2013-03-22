/**
 * AptoideException,		part of Aptoide
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
package cm.aptoide.pt.exceptions;

/**
 * AptoideException, Aptoide's Exception
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class AptoideException extends RuntimeException{

	private static final long serialVersionUID = -4416000225399071171L;
	
	public AptoideException(){
		super();
	}
	
	public AptoideException(String message){
		super(message);
	}
	
	public AptoideException(Throwable rootCause){
		super(rootCause);
	}
	
	public AptoideException(String message, Throwable rootCause){
		super(message, rootCause);
	}

	public String toString(){
		return "Exception due to: " + getMessage();
	}
}
