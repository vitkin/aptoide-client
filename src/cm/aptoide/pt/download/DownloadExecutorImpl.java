package cm.aptoide.pt.download;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoader;
import cm.aptoide.pt.ApplicationAptoide;
import cm.aptoide.pt.R;
import cm.aptoide.pt.views.ViewApk;

import java.io.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 08-07-2013
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class DownloadExecutorImpl implements DownloadExecutor {
    NotificationManager managerNotification;
    Context context = ApplicationAptoide.getContext();
    NotificationCompat.Builder mBuilder;
    @Override
    public void execute(final String path, final ViewApk apk) {

        if(canRunRootCommands()){

            try {


                managerNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                final Process p = Runtime.getRuntime().exec("su");

                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                // Execute commands that require root access
                os.writeBytes("pm install -r " + path + "\n");
                os.flush();
                mBuilder = new NotificationCompat.Builder(context);

                Intent onClick = new Intent();

                // The PendingIntent to launch our activity if the user selects this notification
                PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
                mBuilder.setContentTitle(ApplicationAptoide.MARKETNAME)
                        .setContentText(context.getString(R.string.installing, apk.getName()));

                Bitmap bm = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiscCache().get(apk.getApkid() + "|" + apk.getVercode()).getAbsolutePath());


                mBuilder.setLargeIcon(bm);
                mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
                mBuilder.setContentIntent(onClickAction);
                mBuilder.setAutoCancel(true);


                managerNotification.notify(apk.getAppHashId(), mBuilder.build());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                            int read;
                            char[] buffer = new char[4096];
                            StringBuilder output = new StringBuilder();
                            while ((read = reader.read(buffer)) > 0) {
                                output.append(buffer, 0, read);
                            }
                            reader.close();
                            p.waitFor();

                            String failure = output.toString();

                            if (p.exitValue() != 255 && !failure.toLowerCase(Locale.ENGLISH).contains("failure")) {
                                // Sucess :-)

                                mBuilder = new NotificationCompat.Builder(context);

                                Intent onClick = new Intent(context.getPackageManager().getLaunchIntentForPackage(apk.getApkid()));

                                // The PendingIntent to launch our activity if the user selects this notification
                                PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
                                mBuilder.setContentTitle(ApplicationAptoide.MARKETNAME)
                                        .setContentText(context.getString(R.string.finished_install, apk.getName()));

                                Bitmap bm = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiscCache().get(apk.getApkid() + "|" + apk.getVercode()).getAbsolutePath());


                                mBuilder.setLargeIcon(bm);
                                mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                                mBuilder.setContentIntent(onClickAction);
                                mBuilder.setAutoCancel(true);
                                managerNotification.notify(apk.getAppHashId(), mBuilder.build());
                            }else{

                                managerNotification.cancel(apk.getAppHashId());
                                Intent install = new Intent(Intent.ACTION_VIEW);
                                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                install.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
                                Log.d("Aptoide", "Installing app: " + path);
                                context.startActivity(install);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

                os.writeBytes("exit\n");
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
            Log.d("Aptoide", "Installing app: "+path);
            ApplicationAptoide.getContext().startActivity(install);

        }


    }

    public static boolean canRunRootCommands()
    {
        boolean retval;
        Process suProcess;

        try
        {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            // Getting the id of the current user to check if this is root
            os.writeBytes("id\n");
            os.flush();

            String currUid = osRes.readLine();
            boolean exitSu;
            if (null == currUid)
            {
                retval = false;
                exitSu = false;
                Log.d("ROOT", "Can't get root access or denied by user");
            }
            else if (currUid.contains("uid=0"))
            {
                retval = true;
                exitSu = true;
                Log.d("ROOT", "Root access granted");
            }
            else
            {
                retval = false;
                exitSu = true;
                Log.d("ROOT", "Root access rejected: " + currUid);
            }

            if (exitSu)
            {
                os.writeBytes("exit\n");
                os.flush();
            }
        }
        catch (Exception e)
        {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted

            retval = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
    }
}
