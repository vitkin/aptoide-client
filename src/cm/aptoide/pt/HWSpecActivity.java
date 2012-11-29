/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import cm.aptoide.pt.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class HWSpecActivity extends Activity {

	
	private HWSpecifications specs ;
	private TextView sdkVer;
	private TextView screenSize;
	private TextView esglVer;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hwspecs);
		
		
		sdkVer= (TextView) findViewById(R.id.sdkver);
		screenSize = (TextView) findViewById(R.id.screenSize);
		esglVer = (TextView) findViewById(R.id.esglVer);
		
		sdkVer.setText(HWSpecifications.getSdkVer()+"");
		screenSize.setText(HWSpecifications.getScreenSize(this)+"");
		esglVer.setText(HWSpecifications.getEsglVer(this));
		
		
	}

}
