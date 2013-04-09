package cm.aptoide.pt.webservices;

import android.util.Log;
import cm.aptoide.pt.ApplicationAptoide;
import cm.aptoide.pt.Category;
import cm.aptoide.pt.Database;
import cm.aptoide.pt.util.NetworkUtils;
import cm.aptoide.pt.views.ViewApk;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cm.aptoide.pt.webservices.comments.Comment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 11-03-2013
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class WebserviceGetApkInfo {

    JSONObject response;

    private URL url;
    String arguments = "getApkInfo/<repo>/<apkid>/<apkversion>/options=(<options>)/<mode>";
    String defaultWebservice = "http://webservices.aptoide.com/webservices/";
    private ArrayList<Comment> comments;
    private boolean seeAll = false;
    private boolean screenshotChanged;


    public WebserviceGetApkInfo(String webservice,ViewApk apk, Category category, String token, String md5 ) throws IOException, JSONException {

        StringBuilder url = new StringBuilder();

        if(webservice==null){
            url.append(defaultWebservice);
        }else{
            url.append(webservice);
        }

        ArrayList<WebserviceOptions> options = new ArrayList<WebserviceOptions>();

        if(token!=null)options.add(new WebserviceOptions("token", token));
        options.add(new WebserviceOptions("cmtlimit", "6"));
        options.add(new WebserviceOptions("md5sum", md5));
        options.add(new WebserviceOptions("payinfo", "true"));

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(WebserviceOptions option: options){
            sb.append(option);
            sb.append(";");
        }
        sb.append(")");

        url.append("webservices/getApkInfo/").append(apk.getRepoName()).append("/")
                .append(apk.getApkid()).append("/")
                .append(URLEncoder.encode(apk.getVername(),"UTF-8")).append("/")
                .append("options=")
                .append(sb.toString()).append("/")
                .append("json");

        NetworkUtils utils = new NetworkUtils();
        String line;

        BufferedReader br = new BufferedReader(new InputStreamReader(utils.getInputStream(url.toString(), null, null, ApplicationAptoide.getContext())));
        sb = new StringBuilder();
        while ((line = br.readLine()) != null){
            sb.append(line).append('\n');
        }

        Log.e("REQUEST",url.toString());
        Log.e("RESPONSE",sb.toString());
        response = new JSONObject(sb.toString());


        try {
            comments = getComments();
            Database database = Database.getInstance();

            Log.d("WebserviceGetApkInfo", comments.size()+"");





            if(category.equals(Category.INFOXML)){
                JSONArray screenshots = getScreenshots();


                ArrayList<String> loadedScreenshots =  apk.getScreenshots();


                for(int i = 0; i != screenshots.length(); i++){
                    if(!loadedScreenshots.contains(screenshots.getString(i))){
                        apk.getScreenshots().clear();
                        for(int j = 0; j != screenshots.length(); j++){
                            apk.getScreenshots().add(screenshots.getString(j));
                        }
                        Database.getInstance().insertScreenshots(apk,category);
                        screenshotChanged = true;
                        break;
                    }
                }

                Log.d("WebserviceGetApkInfo", loadedScreenshots.toString());
                Log.d("WebserviceGetApkInfo", screenshots.toString());


            }
            database.deleteCommentsCache(apk.getId(), category);
            for(Comment comment: comments){
                database.insertComment(apk.getId(),comment,category);
            }
            database.insertLikes(getLikes(),category, apk.getId());
            database.insertMalwareInfo(getMalwareInfo(),category, apk.getId());

        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public boolean isSeeAll(){
        return seeAll;

    }

    public MalwareStatus getMalwareInfo() throws JSONException {

        JSONObject malwareResponse = response.getJSONObject("malware");

        return new MalwareStatus(malwareResponse.getString("status"), malwareResponse.getString("reason"));

    }

    public String getLatestVersionURL() throws JSONException {
        return response.getString("latest");

    }

    public boolean hasLatestVersion() {
        return response.has("latest");
    }


    public ArrayList<Comment> getComments() throws JSONException, ParseException {

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JSONArray array = response.getJSONArray("comments");

        if(comments!=null){
            return comments;
        }
        comments = new ArrayList<Comment>();
        for(int i = 0;i!=array.length();i++){

            Comment comment = new Comment();
            comment.id = array.getJSONObject(i).getInt("id");
            comment.subject = array.getJSONObject(i).getString("subject");
            comment.timeStamp =  dateFormater.parse(array.getJSONObject(i).getString("timestamp"));
            comment.username = array.getJSONObject(i).getString("username");
            comment.text = array.getJSONObject(i).getString("text");
            comments.add(comment);
            if(i>4){
                seeAll = true;
            }
        }

        return comments;
    }



    public JSONArray getScreenshots() throws JSONException {



        return response.getJSONArray("sshots");
    }

    public TasteModel getLikes() throws JSONException {
        TasteModel model = new TasteModel();
        JSONObject likevotes = response.getJSONObject("likevotes");

        model.likes = likevotes.getString("likes");
        model.dislikes =  likevotes.getString("dislikes");
        if(likevotes.has("uservote")){
            model.uservote = likevotes.getString("uservote");
        }

        return model;
    }

    public boolean isScreenshotChanged() {
        return screenshotChanged;
    }

    public void setScreenshotChanged(boolean screenshotChanged) {
        this.screenshotChanged = screenshotChanged;
    }

    public JSONObject getPayment() throws JSONException {
        return response.getJSONObject("payment");
    }

    private class WebserviceOptions {
        String key;
        String value;


        private WebserviceOptions(String key,String value) {
            this.value = value;
            this.key = key;
        }

        /**
         * Returns a string containing a concise, human-readable description of this
         * object. Subclasses are encouraged to override this method and provide an
         * implementation that takes into account the object's type and data. The
         * default implementation is equivalent to the following expression:
         * <pre>
         *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
         * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
         * {@code toString} method</a>
         * if you intend implementing your own {@code toString} method.
         *
         * @return a printable representation of this object.
         */
        @Override
        public String toString() {
            return key+"="+value;    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

}
