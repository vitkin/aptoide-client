package cm.aptoide.pt2.webservices.taste;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.util.Algorithms;
import cm.aptoide.pt2.webservices.EnumResponseStatus;
import cm.aptoide.pt2.webservices.login.Login;

public class Likes {

	private static final String listApkLikes = "webservices/listApkLikes/%1$s/%2$s/%3$s/xml";
	private static final String addApkLike = "webservices/addApkLike";
	private static final String DEFAULT_PATH = "http://webservices.aptoide.com/";
	public String WEB_SERVICE_LIKES_LIST;
	public String WEB_SERVICE_LIKES_POST;
	private ViewGroup view;
	private AsyncTask<String, Void, EnumResponseStatus> task;
	String userHashId;
	private boolean isLoggedin = false;
	Context context;
	private String version;
	private String apkid;
	private String repo;
	private ViewGroup viewButtons;
	
	public Likes(Activity activity, String webservicespath) {
		context = activity;
			if(webservicespath==null){
				webservicespath = DEFAULT_PATH;
			}
			WEB_SERVICE_LIKES_LIST = webservicespath + listApkLikes;
			WEB_SERVICE_LIKES_POST = webservicespath + addApkLike;
		isLoggedin =Login.isLoggedIn(activity);
		if(isLoggedin){
			try {
				userHashId=Algorithms.computeSHA1sum(Login.getUserLogin(activity));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void getLikes(String repo, String apkid, String version,
			ViewGroup view, ViewGroup viewButtons) {
		this.repo = repo;
		this.apkid = apkid;
		this.version = version;
		this.view = view;
		this.viewButtons = viewButtons;
		if (task != null) {
			System.out.println("canceling");
			task.cancel(true);
		}
		task = new LikesGetter().execute(repo, apkid, version);
	}
	
	public void postLike(String repo, String apkid, String version, EnumUserTaste taste) {
		this.taste = taste;
		task = new LikesPoster().execute(Login.getToken(context),repo, apkid, version,taste.toString());
	}
	
	int likes = 0;
	int dislikes = 0;
	EnumUserTaste taste;
	boolean userTasted =false;
	boolean userTasted2 =false;
	public class LikesGetter extends AsyncTask<String, Void, EnumResponseStatus> {

		
		EnumUserTaste usertaste = EnumUserTaste.TASTELESS;
		EnumResponseTasteElement tasteIndicator;
		EnumResponseStatus result;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			((TextView) view.findViewById(R.id.likes)).setText(context.getString(R.string.loading_likes));
			((TextView) view.findViewById(R.id.dislikes)).setText("");
		}

		@Override
		protected EnumResponseStatus doInBackground(String... params) {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(
						String.format(WEB_SERVICE_LIKES_LIST, new Object[] {
								params[0], params[1], params[2] }))
						.openConnection();
				System.out.println(connection.getURL());
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				BufferedInputStream bis = new BufferedInputStream(
						connection.getInputStream(), 8 * 1024);
				SAXParserFactory spfact = SAXParserFactory.newInstance();
				SAXParser parser = spfact.newSAXParser();
				parser.parse(bis, new DefaultHandler() {
					StringBuilder sb = new StringBuilder();

					@Override
					public void startElement(String uri, String localName,
							String qName, Attributes attributes)
							throws SAXException {
						super.startElement(uri, localName, qName, attributes);
						switch (EnumResponseTasteElement
								.valueOfToUpper(localName)) {
								case LIKES:
									tasteIndicator = EnumResponseTasteElement.LIKES;
									break;
								case DISLIKES:
									tasteIndicator = EnumResponseTasteElement.DISLIKES;
									break;
								default:
							break;
						}

						sb.setLength(0);

					}

					@Override
					public void characters(char[] ch, int start, int length)
							throws SAXException {
						super.characters(ch, start, length);
						sb.append(ch, start, length);
					}

					@Override
					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						super.endElement(uri, localName, qName);

						if (EnumResponseTasteElement.valueOfToUpper(localName) == EnumResponseTasteElement.ENTRY) {
							if (isLoggedin&&sb.toString().equals(userHashId)) {
								userTasted = true;
								userTasted2 = true;
							}
							switch (tasteIndicator) {
							case LIKES:
								likes++;
								if (userTasted) {
									usertaste = EnumUserTaste.LIKE;
									userTasted=false;
								}
								break;
							case DISLIKES:
								if (userTasted) {
									usertaste = EnumUserTaste.DONTLIKE;
									userTasted=false;
								}
								dislikes++;
								break;
							default:
								break;
							}
						}

					}
				});
				bis.close();
			    result = EnumResponseStatus.OK;
			} catch (Exception e) {
				e.printStackTrace();
				result = EnumResponseStatus.FAIL;
			}
			System.out.println("RESULT: " +result);
			return result;
		}

		@Override
		protected void onPostExecute(EnumResponseStatus result) {
			super.onPostExecute(result);
			switch (result) {
			case OK:
				((TextView) view.findViewById(R.id.likes)).setText(likes+"");
				((TextView) view.findViewById(R.id.dislikes)).setText(dislikes+"");
				switch (usertaste) {
				case LIKE:
					((Button) viewButtons.findViewById(R.id.likesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_btn_over , 0, 0, 0);
					((Button) viewButtons.findViewById(R.id.dislikesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_btn, 0, 0, 0);
					break;
				case DONTLIKE:
					((Button) viewButtons.findViewById(R.id.likesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_btn, 0, 0, 0);
					((Button) viewButtons.findViewById(R.id.dislikesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_btn_over, 0, 0, 0);
					break;
				default:
					break;
				}
				break;
			case FAIL:
				((TextView) view.findViewById(R.id.likes)).setText(context.getString(R.string.tastenotavailable));
				((TextView) view.findViewById(R.id.dislikes)).setText("");
				break;
			default:
				break;
			}
			
		}

	}
	
	public class LikesPoster extends AsyncTask<String, Void, EnumResponseStatus>{
		ProgressDialog pd = new ProgressDialog(context);
		@Override
		protected void onPreExecute() {
			pd.setMessage(context.getString(R.string.postingtaste));
			pd.show();
			super.onPreExecute();
		}
		
		@Override
		protected EnumResponseStatus doInBackground(String... params) {
			String data = null;
			StringBuilder sb=null;
			EnumResponseStatus response = EnumResponseStatus.FAIL;
			try{
				HttpURLConnection connection = (HttpURLConnection) new URL(WEB_SERVICE_LIKES_POST).openConnection();
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
				data += "&" + URLEncoder.encode("repo", "UTF-8") + "=" + URLEncoder.encode(params[1],"UTF-8");
				data += "&" + URLEncoder.encode("apkid", "UTF-8") + "=" + URLEncoder.encode(params[2],"UTF-8");
				data += "&" + URLEncoder.encode("apkversion", "UTF-8") + "=" + URLEncoder.encode(params[3],"UTF-8");
				data += "&" + URLEncoder.encode("like", "UTF-8") + "=" + URLEncoder.encode(params[4],"UTF-8");
				data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8");

				OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
				wr.write(data);
				wr.flush();
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                JSONObject object = new JSONObject(sb.toString());
			    response = EnumResponseStatus.valueOf(object.getString("status").toUpperCase());
			}catch(Exception e){
				e.printStackTrace();
				response = EnumResponseStatus.FAIL;
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(EnumResponseStatus result) {
			super.onPostExecute(result);
			switch (result) {
			case OK:
				Toast.makeText(context, context.getString(R.string.opinionsuccess), Toast.LENGTH_LONG).show();
				switch (taste) {
				case LIKE:
					((Button) viewButtons.findViewById(R.id.likesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_btn_over , 0, 0, 0);
					((Button) viewButtons.findViewById(R.id.dislikesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_btn, 0, 0, 0);
					break;
				case DONTLIKE:
					((Button) viewButtons.findViewById(R.id.likesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_btn, 0, 0, 0);
					((Button) viewButtons.findViewById(R.id.dislikesImage)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.dislike_btn_over, 0, 0, 0);
					break;
				default:
					break;
				}
				break;
			case FAIL:
				Toast.makeText(context, "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			
			pd.dismiss();
		}
	}

}
