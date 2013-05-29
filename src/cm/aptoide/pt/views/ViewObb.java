package cm.aptoide.pt.views;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 21-05-2013
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ViewObb {


    private ViewCacheObb patchCache;


    private ViewCacheObb mainCache;

    public ViewObb(String mainUrl, ViewCacheObb mainCache, ViewCacheObb patchCache){
        this(mainUrl,null, mainCache, patchCache);
    }

    public ViewObb(String mainUrl, String patchUrl, ViewCacheObb mainCache, ViewCacheObb patchCache){
        this.mainUrl = mainUrl;
        this.patchUrl = patchUrl;
        this.mainCache = mainCache;
        this.patchCache = patchCache;
    }

    private String mainUrl;
    private String patchUrl;

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getPatchUrl() {
        return patchUrl;
    }

    public void setPatchUrl(String patchUrl) {
        this.patchUrl = patchUrl;
    }

    public ViewCacheObb getPatchCache() {
        return patchCache;
    }


    public ViewCacheObb getMainCache() {
        return mainCache;
    }
}
