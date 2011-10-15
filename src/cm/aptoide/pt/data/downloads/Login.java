/*
 * Login,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.downloads;


 /**
 * Login, models a download's authentication
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Login {

	private String username;
	private String password;
	

	public Login(String username, String password) {
		this.username = username;
		this.password = password;
	}


	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}
	
	
	public void clean(){
		this.username = null;
		this.password = null;
	}

	public void reuse(String username, String password){
		this.username = username;
		this.password = password;
	}
	
}
