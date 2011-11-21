/**
 * AptoideLog,		part of Aptoide
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
package cm.aptoide.pt.debug;

import android.util.Log;
import cm.aptoide.pt.data.Constants;

/**
 * AptoideLog, handles global Log messaging
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class AptoideLog {
	
	static EnumLogLevels logLevelFilter = Constants.LOG_LEVEL_FILTER;

	public static void logWithParts(InterfaceAptoideLog logFrom, EnumLogLevels logLevel, String[] messageParts){
		if(logLevelFilter.ordinal() <= logLevel.ordinal()){
			StringBuilder message = new StringBuilder();
			for (String messagePart : messageParts) {
				message.append(messagePart);
				message.append(" ");
			}
			switch (logLevel) {
			case DEBUG:
				d(logFrom, message.toString());
				break;
			case VERBOSE:
				v(logFrom, message.toString());
				break;
			case INFO:
				i(logFrom, message.toString());
				break;
			case WARNING:
				w(logFrom, message.toString());
				break;
			case ERROR:
				e(logFrom, message.toString());
				break;

			default:
				break;
			}
		}
	}
	
	
	
	public static void d(InterfaceAptoideLog logFrom, String[] messageParts){
		logWithParts(logFrom, EnumLogLevels.DEBUG, messageParts);
	}
	
	public static void d(InterfaceAptoideLog logFrom, String message){
		if(logLevelFilter.equals(EnumLogLevels.DEBUG)){
			Log.d(logFrom.getTag(), message);
		}
	}
	
	
	public static void v(InterfaceAptoideLog logFrom, String[] messageParts){
		logWithParts(logFrom, EnumLogLevels.VERBOSE, messageParts);
	}
	
	public static void v(InterfaceAptoideLog logFrom, String message){
		if(logLevelFilter.ordinal() <= EnumLogLevels.VERBOSE.ordinal()){
			Log.w(logFrom.getTag(), message);
		}
	}
	
	
	public static void i(InterfaceAptoideLog logFrom, String[] messageParts){
		logWithParts(logFrom, EnumLogLevels.INFO, messageParts);
	}
	
	public static void i(InterfaceAptoideLog logFrom, String message){
		if(logLevelFilter.ordinal() <= EnumLogLevels.INFO.ordinal()){
			Log.w(logFrom.getTag(), message);
		}
	}
	
	
	public static void w(InterfaceAptoideLog logFrom, String[] messageParts){
		logWithParts(logFrom, EnumLogLevels.WARNING, messageParts);
	}
	
	public static void w(InterfaceAptoideLog logFrom, String message){
		if(logLevelFilter.ordinal() <= EnumLogLevels.WARNING.ordinal()){
			Log.w(logFrom.getTag(), message);
		}
	}
	
	
	public static void e(InterfaceAptoideLog logFrom, String[] messageParts){
		logWithParts(logFrom, EnumLogLevels.ERROR, messageParts);
	}
	
	public static void e(InterfaceAptoideLog logFrom, String message){
		if(logLevelFilter.ordinal() <= EnumLogLevels.ERROR.ordinal()){
			Log.w(logFrom.getTag(), message);
		}
	}
	

}
