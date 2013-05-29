package cm.aptoide.pt.views;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.util.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 21-05-2013
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class ViewCacheObb extends ViewCache implements Parcelable {

    private String packageName;
    private ViewCache parentCache;
    private String name;

    public ViewCacheObb(int id, String name, String md5, ViewCache parentCache, String packageName) {
        super(id, md5);
        this.name=name;
        this.parentCache = parentCache;
        this.packageName = packageName;

    }


    public String getLocalPath() {
        return Constants.PATH_SDCARD + "/Android/obb/" + packageName + "/" +name;
    }

    public ViewCache getParentCache() {
        return parentCache;
    }

    public static final Parcelable.Creator<ViewCacheObb> CREATOR = new Parcelable.Creator<ViewCacheObb>() {
        public ViewCacheObb createFromParcel(Parcel in) {

            return new ViewCacheObb(in);
        }

        public ViewCacheObb[] newArray(int size) {
            return new ViewCacheObb[size];
        }
    };

    /**
     * we're annoyingly forced to create this even if we clearly don't need it,
     *  so we just use the default return 0
     *
     *  @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    protected ViewCacheObb(Parcel in){
        super(in);
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out,flags);
        out.writeString(name);
        out.writeParcelable(parentCache,flags);
        out.writeString(packageName);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        this.name = in.readString();
        this.parentCache = in.readParcelable(ViewCache.class.getClassLoader());
        this.packageName = in.readString();

    }
}
