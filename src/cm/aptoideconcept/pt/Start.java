package cm.aptoideconcept.pt;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Start extends FragmentActivity {
	Context context;
	DBHandler db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=this;
        db = new DBHandler(context);
        db.open();
		setContentView(R.layout.start);
		
		//Check installed
		new Thread(new Runnable() {

			public void run() {
				PackageManager mPm = getPackageManager();
				List<PackageInfo> system_installed_list = mPm.getInstalledPackages(0);
				List<String> database_installed_list = db.getStartupInstalled();
				for (PackageInfo pkg : system_installed_list) {
					if (!database_installed_list.contains(pkg.packageName)) {
						try {
							Apk apk = new Apk();
							apk.apkid = pkg.packageName;
							apk.vercode = pkg.versionCode;
							apk.vername = pkg.versionName;
							apk.name = (String) pkg.applicationInfo.loadLabel(mPm);
							db.insertInstalled(apk);
						} catch (Exception e) {
							//TODO Error manager
							e.printStackTrace();
						}finally{
							
						}
					}
				}
				startActivityForResult(new Intent(Start.this,Aptoide.class),0);
				
			}
		}).start();
		
        

        
        
        
    }

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		finish();
		
	}
    
    
}