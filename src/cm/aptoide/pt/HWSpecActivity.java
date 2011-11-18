package cm.aptoide.pt;

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
		
		specs = new HWSpecifications(this);
		
		setContentView(R.layout.hwspecs);
		
		
		sdkVer= (TextView) findViewById(R.id.sdkver);
		screenSize = (TextView) findViewById(R.id.screenSize);
		esglVer = (TextView) findViewById(R.id.esglVer);
		
		sdkVer.setText(new Integer(specs.getSdkVer()).toString());
		screenSize.setText(specs.getScreenSize());
		esglVer.setText(specs.getEsglVer());
		
		
	}

}
