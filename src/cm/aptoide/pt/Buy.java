package cm.aptoide.pt;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cm.aptoide.pt.R;
import cm.aptoide.pt.webservices.login.Login;

import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPreapproval;
import com.paypal.android.MEP.PayPalResultDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class Buy extends Activity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static String resultTitle;
	public static String resultInfo;
	public static String resultExtra;
	static final int request = 1;

	String pakey;

	int server = PayPal.ENV_SANDBOX;
	String appID = "APP-80W284485P519543T";
	String repo;
	String apkid;
	String versionName;
	String userMail ;
	String token ;
	String url = ("http://webservices.aptoide.com/webservices/hasPurchaseAuthorization");
	String urlPay = ("http://webservices.aptoide.com/webservices/payApk/");
	String urlCheck = ("http://webservices.aptoide.com/webservices/checkPaidProduct/");
	String urlRedirect="http://www.sandbox.paypal.com/webscr?cmd=_ap-payment&paykey=";


	TextView tv;
	Bundle b;
	boolean canceled=false;
	int operation=1;
	// 1 - Get pre-approval key
	// 2 - Validation status
	// 3 - Pay
	private SharedPreferences sPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paypalbuy);
		token = Login.getToken(this);
		userMail = Login.getUserLogin(this);
		b = getIntent().getExtras();

		apkid = b.getString("apkid");
		versionName = b.getString("versionName");
		repo=b.getString("repo");
//		String params = token+"/"+userMail+"/check/json";
        String params = token+"/check/json";
		send(url, params);
		tv=(TextView) findViewById(R.id.tv);

	}



	public void send(final String url, final String params){

		Thread t = new Thread(){
			public void run() {

				String temp=null;

				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
				HttpResponse response=null;
				HttpGet request = new HttpGet();

				try {
					request.setURI(new URI(url+"/"+params));
					System.out.println(request.getURI());
				} catch (URISyntaxException e) {
					Log.e("preapproval", "URISyntaxException");
					e.printStackTrace();
				}

				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					Log.e("preapproval", "ClientProtocolException");
					e.printStackTrace();
				} catch (IOException e) {
					Log.e("preapproval", "IOException on response");
					e.printStackTrace();
				}

				if(response!=null){
					try {
						temp = EntityUtils.toString(response.getEntity());
					} catch (ParseException e) {
						Log.e("preapproval", "ParseException");
						e.printStackTrace();
					} catch (IOException e) {
						Log.e("preapproval", "IOException on parse");
						e.printStackTrace();
					}
					Log.i("preapproval", temp);
				}
				else{
					Log.e("preapproval", "the response is null");
				}

				Bundle data=new Bundle();
				data.putString("response", temp);
				Message msg = new Message();
				msg.setData(data);

				handler2.sendMessage(msg);
			}
		};
		t.start();
	}


	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Bundle data=msg.getData();
			String response=data.getString("response");

			Log.i("preapproval", "handler: "+response);

			JSONObject respJSON;

			try {
				respJSON = new JSONObject(response);

				if(respJSON.getString("status").equals("OK")){
					if(respJSON.has("listing")){
						if(respJSON.getJSONObject("listing").has("preapprovalKey")){
							pakey=respJSON.getJSONObject("listing").getString("preapprovalKey");
							System.out.println("PreApprovalKey" + pakey);
							tv.setText("Starting PayPal...");
							startPayPal();
						}
					}else switch(operation){

					case 1:
						Log.i("preapproval", "The buyer already have a key");
						sendPayReq();
						break;
					case 2:
						Log.i("preapproval", "Validation status sent");
						if (!canceled)
							sendPayReq();
						break;
					case 3:
						Log.i("preapproval", "Payment Complete!");
						tv.setText("Payment Completed!");
						setResult(RESULT_OK,null);
						finish();
					}
				}
				else if(respJSON.getString("status").equals("failure") || respJSON.getString("status").equals("FAIL")){
					tv.setText("Operation failed");
				}

			} catch (Exception e) {
				e.printStackTrace();
				tv.setText("Operation failed");
				Log.e("preapproval", "failed to create a JSON response object or get String");
			}

		}
	};


	public void startPayPal(){

		Thread libraryInitializationThread = new Thread() {
			public void run() {
				try{


				initLib();
				launch();
				if (PayPal.getInstance().isLibraryInitialized()) {
					Log.i("preapproval", "lib init success");

				}
				else {
					Log.e("preapproval", "lib init failed");
				}
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		libraryInitializationThread.start();
	}


	public void launch(){

		PayPalPreapproval preapproval = Preapproval();
		PayPal.getInstance().setPreapprovalKey(pakey);
		Intent preapproveIntent = PayPal.getInstance().preapprove(preapproval, this, new ResultDelegate());
		startActivityForResult(preapproveIntent, request);
	}


	public PayPalPreapproval Preapproval() {

		PayPalPreapproval preapproval = new PayPalPreapproval();
		preapproval.setCurrencyType("EUR");
		preapproval.setMemo("Pagamento");
		preapproval.setMerchantName(getString(R.string.app_name));

		return preapproval;
	}


	public void initLib(){
		try{
			PayPal pp = PayPal.getInstance();

			if(pp == null) {
				pp = PayPal.initWithAppID(this, appID, server);
				pp.setLanguage("en_US");
				pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
				pp.setDynamicAmountCalculationEnabled(false);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		operation=2;

		Log.i("preapproval", "requestCode: "+requestCode);
		Log.i("preapproval", "resultCode: "+resultCode);

		Log.i("preapproval", "resultTitle: "+resultTitle);
		Log.i("preapproval", "resultInfo: "+resultInfo);
		Log.i("preapproval", "resultExtra: "+resultExtra);
		if(resultCode!=RESULT_OK){
			tv.setText("Canceled.");
			canceled=true;
//			String params = token+"/"+userMail+"/validate"+"/"+pakey+"/false/json";
            String params = token+"/validate"+"/"+pakey+"/false/json";
            send(url, params);
		}else{
			tv.setText("Key validated!");
			Log.i("preapproval", "key validated");
//			String params = token+"/"+userMail+"/validate"+"/"+pakey+"/true/json";
            String params = token+"/validate"+"/"+pakey+"/true/json";

            send(url, params);
		}
	}

	public void sendPayReq() {
		Log.i("preapproval", "Payment started");
		operation=3;
		tv.setText("Waiting for server response...");
//		String params = token+"/"+userMail+"/" + repo +"/"+apkid+"/"+versionName+"/pay/json";
        String params = token+"/" + repo +"/"+apkid+"/"+versionName+"/pay/json";

        Log.i("preapproval", "url: "+urlPay+"/"+params);
		send(urlPay, params);
	}


}