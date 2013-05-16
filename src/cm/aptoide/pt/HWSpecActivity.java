/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;


public class HWSpecActivity extends Activity /*SherlockActivity */{

	
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

		AptoideThemePicker.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.hwspecs);
//		getSupportActionBar().setIcon(R.drawable.brand_padding);
//		getSupportActionBar().setTitle(getString(R.string.setting_hwspecstitle));
//		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		sdkVer= (TextView) findViewById(R.id.sdkver);
		screenSize = (TextView) findViewById(R.id.screenSize);
		esglVer = (TextView) findViewById(R.id.esglVer);
		
		sdkVer.setText(HWSpecifications.getSdkVer()+"");
		screenSize.setText(HWSpecifications.getScreenSize(this)+"");
		esglVer.setText(HWSpecifications.getEsglVer(this));
		
		
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
