/**
 * 
 */
package cm.aptoide.summerinternship2011.multiversion;

import java.net.MalformedURLException;
import java.net.URL;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author rafael
 * @since summerinternship2011
 *
 */
public class VersionApk extends Version implements Parcelable{
	
	private URL url;
	private final String md5;
	
	public VersionApk(String version, String uri, String md5) throws MalformedURLException {
		super(version);
		url = new URL(uri);
		this.md5 = md5;
	}
	
	/**
	 * @return the version
	 */
	public Version getVersion() {
		return (Version) this;
	}

	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}
	
	/**
	 * 
	 * @param in
	 */
	public VersionApk(Parcel in) {
		super(in.readString());
		url = (URL) in.readSerializable();
		this.md5 = in.readString();
	}
	
	/**
	 * 
	 */
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 */
	public void writeToParcel(Parcel dest, int flags) {
		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order
		dest.writeSerializable(url);
		dest.writeString(md5);
	}
	
	/**
	 * 
	 */
	public static final Parcelable.Creator<VersionApk> CREATOR = new Parcelable.Creator<VersionApk>() {
		public VersionApk createFromParcel(Parcel in) {
		    return new VersionApk(in);
		}	
	
		public VersionApk[] newArray(int size) {
		    return new VersionApk[size];
		}
	};

}
