package cm.aptoide.pt2;

import java.io.File;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable{
	
	public static enum State { PARSING, PARSED, QUEUED, FAILED };
	
	long id;
	String url = "";
	String delta = "";
	String timestamp = "";
	int n_apk = 0;
	State state = State.QUEUED;
	String iconsPath;
	String basePath;
	String apkPath;
	String webservicesPath;
	String username;
	String password;
	File xml;
	
	
	public Server(String url){
		this.url = url;
	}
	
	public Server(String url, String delta, long id){
		this.url = url;
		this.delta = delta;
		this.id=id;
	}

	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Server(Parcel in) {
		readFromParcel(in);
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order
		dest.writeLong(id);
		dest.writeString(url);
		dest.writeString(delta);
		dest.writeString(timestamp);
		dest.writeInt(n_apk);
		dest.writeInt(state.ordinal());
	}

	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel in) {

		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		id = in.readLong();
		url = in.readString();
		delta = in.readString();
		timestamp = in.readString();
		n_apk = in.readInt();
		state = State.values()[in.readInt()];
	}

   /**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *
    * This also means that you can use use the default
    * constructor to create the object and use another
    * method to hyrdate it as necessary.
    *
    * I just find it easier to use the constructor.
    * It makes sense for the way my brain thinks ;-)
    *
    */
   public static final Parcelable.Creator<Server> CREATOR =
   	new Parcelable.Creator<Server>() {
           public Server createFromParcel(Parcel in) {
               return new Server(in);
           }

           public Server[] newArray(int size) {
               return new Server[size];
           }
       };
	
}