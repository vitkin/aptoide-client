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

package cm.aptoide.pt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Settings extends Activity implements OnCheckedChangeListener, OnClickListener{
	
	private boolean rating = false;
	private boolean iu = false;
	private boolean recent = false;
	private boolean abc = false;
	
	private int rating_id;
	private int iu_id;
	private int recent_id;
	private int abc_id;
	
	private Intent rtrn = new Intent();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Intent i = getIntent();
		
		RadioButton btn1 = (RadioButton) findViewById(R.id.shw_ct);
		btn1.setEnabled(false);
		
		RadioGroup grp1 = (RadioGroup) findViewById(R.id.groupbtn);
		grp1.setOnCheckedChangeListener(this);
		
		RadioButton b1 = (RadioButton) findViewById(R.id.org_iu);
		RadioButton b2 = (RadioButton) findViewById(R.id.org_rat);
		//b2.setEnabled(false);
		RadioButton b3 = (RadioButton) findViewById(R.id.org_rct);
		//b3.setEnabled(false);
		RadioButton b4 = (RadioButton) findViewById(R.id.org_abc);
		
		if(i.getStringExtra("order").equalsIgnoreCase("iu")){
			b1.setChecked(true);
			iu = true;
		}else if(i.getStringExtra("order").equalsIgnoreCase("abc")){
			b4.setChecked(true);
			abc = true;
		}else if(i.getStringExtra("order").equalsIgnoreCase("recent")){
			b3.setChecked(true);
			recent = true;
		}else if(i.getStringExtra("order").equalsIgnoreCase("rating")){
			b2.setChecked(true);
			rating = true;
		}
		
		rating_id = b2.getId();
		iu_id = b1.getId();
		recent_id = b3.getId();
		abc_id = b4.getId();
		
		Button btn_ok = (Button) findViewById(R.id.btn_save);
		btn_ok.setOnClickListener(this);
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		rating  = false;
		iu = false;
		recent = false;
		abc = false;
		
		if(checkedId == rating_id){
			rating = true;
		}else if(checkedId == iu_id){
			iu = true;
		}else if(checkedId == recent_id){
			recent = true;
		}else if(checkedId == abc_id){
			abc = true;
		}
	}
	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(rating == true){
			rtrn.putExtra("align", "rating");
		}else if(iu == true){
			rtrn.putExtra("align", "iu");
		}else if(recent == true){
			rtrn.putExtra("align", "recent");
		}else if (abc == true){
			rtrn.putExtra("align", "abc");
		}
		finish();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		rtrn.putExtra("settings", 0);
		this.setResult(RESULT_OK, rtrn);
		super.finish();
	}

}
