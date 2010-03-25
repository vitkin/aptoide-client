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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class Aptoide extends Activity { 
    
	private static final int OUT = 0;
    private static final long START = 2000;
    private static final String TMP_SRV_FILE = "/sdcard/.aptoide/server";
    
    // Used for Aptoide version update
	private DbHandler db = null;

    
    
    private Handler startHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case OUT:
				Intent i = new Intent(Aptoide.this, RemoteInTab.class);
				Intent get = getIntent();
				if(get.getData() != null){
					Uri uri = get.getData();
					downloadServ(uri.toString());
					i.putExtra("uri", TMP_SRV_FILE);
				}
				startActivityForResult(i,0);
				break;
			}
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		} 
    }; 
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = new DbHandler(this);
		db.UpdateTables();
		db.UpdateTables2();
        db = null;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start);
        Message msg = new Message();
        startHandler.sendMessageDelayed(msg, START);
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		this.finish();
	}
    
	private void downloadServ(String srv){
		try{
			BufferedInputStream getit = new BufferedInputStream(new URL(srv).openStream());

			File file_teste = new File(TMP_SRV_FILE);
			if(file_teste.exists())
				file_teste.delete();
			
			FileOutputStream saveit = new FileOutputStream(TMP_SRV_FILE);
			BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
			byte data[] = new byte[1024];
			
			int readed = getit.read(data,0,1024);
			while(readed != -1) {
				bout.write(data,0,readed);
				readed = getit.read(data,0,1024);
			}
			bout.close();
			getit.close();
			saveit.close();
		} catch(Exception e){
			AlertDialog p = new AlertDialog.Builder(this).create();
			p.setTitle("Erro");
			p.setMessage("Não foi possivel conectar ao servidor remoto.");
			p.setButton("Ok", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			          return;
			        } });
			p.show();
		}
	}
    
}
