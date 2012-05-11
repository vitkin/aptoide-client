package cm.aptoideconcept.pt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceExample extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("Starting Service");
		new Thread(new Runnable() {
			public void run() {
				try {

					Thread.sleep(1000);
					DBHandler dbhandler = new DBHandler(getApplicationContext());
					dbhandler.open();
					dbhandler.delete("aptoide");
					dbhandler.close();
					Thread.sleep(1000);
					dbhandler.open();
					dbhandler.delete("aptoide");
					dbhandler.close();
					
					Thread.sleep(1000);
					dbhandler.open();
					dbhandler.delete("aptoide");
					dbhandler.close();
					Thread.sleep(1000);
					dbhandler.open();
					dbhandler.delete("aptoide");
					dbhandler.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		super.onStart(intent, startId);
	}

}
