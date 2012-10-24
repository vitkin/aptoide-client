package cm.aptoide.pt2.contentloaders;



import java.io.File;
import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
//        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),".aptoide/icons/");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(String hash){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
    	System.out.println("Getting file:" +hash);
        File f = new File(cacheDir, hash);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}