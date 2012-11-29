/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;
/**
 * DialogIpBlacklisted, part of Aptoide
 * Copyright (C) 2012 Duarte Silveira
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


import cm.aptoide.pt.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


/**
 * DialogIpBlacklisted, handles informing user of current IP address being blacklisted
 * 
 * @author dsilveira
 *
 */
public class DialogIpBlacklisted extends Dialog{
	
	public DialogIpBlacklisted(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width=WindowManager.LayoutParams.FILL_PARENT;
		params.height=WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);
		
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setTitle(R.string.ip_blacklisted);

		setContentView(R.layout.dialog_ip_blacklisted);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
		
		
		TextView ipBlacklistMessage = (TextView) findViewById(R.id.ip_blacklisted_message);
		ipBlacklistMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendEmail();
			}
		  });
		
		((Button)this.findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v) {
 				sendEmail();
			}
			
		});
		
		((Button)this.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v) {
 				dismiss();
			}
			
		});
		
	}
	
	private void sendEmail(){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{getContext().getString(R.string.ip_blacklisted_email_contact)});		  
		email.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.ip_blacklisted));
//		email.putExtra(Intent.EXTRA_TEXT, "message");
		email.setType(getContext().getString(R.string.email_encoding));
		getContext().startActivity(Intent.createChooser(email, getContext().getString(R.string.choose_email_client)));
	}
}
