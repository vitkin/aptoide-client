/**
 * 
 */
package cm.aptoide.pt.multiversion;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author rafael
 * 
 * A description of a application version that enables to work easily with them.
 * 
 */
public class VersionApk implements Comparable<VersionApk>, Parcelable{
	
	
	
	
	
	/**
	 * Formated version
	 */
	private String version; 
	/**
	 * A field to easily compare versions, this is the same string with out the dots
	 */
	private String rawVersion;
	/**
	 * 
	 */
	private String apkId;
	
	/**
	 * 
	 */
	private int size;
	
	/**
	 * A pattern for version format confirming
	 */
	private static final Pattern versionPattern = Pattern.compile("^(((\\d)+\\.)+(\\d)+)|((\\d)+)$");
	
	
	
	
	
	/**
	 * 
	 * @param version
	 * @param apkId
	 * @param versionLabel
	 */
	public VersionApk(String version, String apkId, int size ) {
		
		if(version==null)
			throw new IllegalArgumentException("The version can not be null");
		
		if(apkId==null)
			throw new IllegalArgumentException("The apkId can not be null");
		
		this.size = size;
		this.version = version;
		this.rawVersion = VersionApk.processRawVersion(this.version);
		this.apkId = apkId;
		
	}
	
	/**
	 * 
	 * @return Formated version
	 */
	public String getVersion() { return version; }
	
	/**
	 * 
	 * @return A field to easily compare versions, this is the same string as getVersionString() in with out the dots
	 */
	public String getRawVersion() { return rawVersion; }
	
	/**
	 * 
	 * @return
	 */
	public String getApkId() { return apkId; }
	
	/**
	 * 
	 * @return
	 */
	public int getSize() { return size; }
	
	/**
	 * 
	 * @param version Version to confirm with the pattern ^(((\\d)+\\.)+(\\d)+)|((\\d)+)$
	 * @throws NotValidVersionException
	 * @throws IllegalArgumentException
	 */
	public static void confirmPattern(String version){
		
		if(version==null)
			throw new IllegalArgumentException("The version can not be null");
		
		if(!versionPattern.matcher(version).matches())
			throw new NotValidVersionException(version);
	}
	
	/**
	 * 
	 * @param version
	 * @return A field to easily compare versions, this is the same string with out the dots
	 */
	public static String processRawVersion(final String version){
		
		if(version==null)
			throw new IllegalArgumentException("The version can not be null");
		
		confirmPattern(version);
		return version.replace(".", "");	
	}
	
	/**
	 * @param size Size of the desired String
	 * @param fillWith Character to field the string with
	 * @return A String fielded with the filedWith and with a length size
	 */
	private String getStringFielded(int size,char fillWith){
		char[] buffer = new char[size];
		Arrays.fill(buffer, fillWith);
		return Arrays.toString(buffer).replaceAll("\\[|\\]|\\s|,", "");
	}
	
	/**
	 * Ensure that the rawVersion attribute of this class is the same length as the givenVersionRawVersion, 
	 * by adding zeros at the end of the smaller string to make their length match.
	 * @param version
	 * @return A string array with two elements, the first is rawVersion of this class the second the rawVersion of the givenClass
	 */
	private StringBuilder[] ensureSameCharacterLength(final VersionApk version){
		
		if(version==null)
			throw new IllegalArgumentException("The givenVersion can not be null");
		
		final StringBuilder[] rawVersions = {
				new StringBuilder(rawVersion), 
				new StringBuilder(
						version.getRawVersion())
		};
		int rawVerionLengthDifference = rawVersion.length()-version.getRawVersion().length();
		if(rawVerionLengthDifference<0){
			rawVersions[0].append(getStringFielded(-rawVerionLengthDifference,'0')); 
		}else if(rawVerionLengthDifference>0){
			rawVersions[1].append(getStringFielded(rawVerionLengthDifference,'0')); 
		}
		return rawVersions;
	}
	
	@Override
	public String toString() { return "apkId: "+apkId+" version: "+version+" size:"+size; }
	
	@Override
	public boolean equals(Object version) {
		
		if(version==null)
			throw new IllegalArgumentException("The given object can not be null.");
		
		if(!(version instanceof VersionApk))
			throw new IllegalArgumentException("The given argument is not a instance of Version class.");
		
		VersionApk versionApk = (VersionApk)version;
		
		StringBuilder[] rawVersions = ensureSameCharacterLength(versionApk);
		
		return new BigInteger(rawVersions[0].toString()).equals(new BigInteger(rawVersions[1].toString())) && this.apkId.equals(((VersionApk)version).getApkId());
		
	}
	
	public int compareTo(VersionApk version) {
		
		if(version==null)
			throw new IllegalArgumentException("The version can not be null.");
		
		StringBuilder[] rawVersions = ensureSameCharacterLength(version);
		
		return new BigInteger(rawVersions[0].toString()).compareTo(new BigInteger(rawVersions[1].toString()));
	}
	
	public static String getStringFromVersionApkList(ArrayList<VersionApk> versionsApk){
		StringBuilder strBuilder = new StringBuilder("");
		Iterator<VersionApk> iteratorVersions = versionsApk.iterator();
		while(iteratorVersions.hasNext()){
			strBuilder.append(iteratorVersions.next().getVersion());
			if(iteratorVersions.hasNext()){
				strBuilder.append(", ");
			}
		}
		return strBuilder.toString();
	}
	
	public static HashMap<String,ArrayList<VersionApk>> getGreaterAndSmallerThan(VersionApk givenVersion, ArrayList<VersionApk> versions){
		
		HashMap<String,ArrayList<VersionApk>> ret = new HashMap<String,ArrayList<VersionApk>>();
		ret.put("smaller", new ArrayList<VersionApk>());
		ret.put("greater", new ArrayList<VersionApk>());
		
		for(VersionApk version:versions){
			int comp = givenVersion.compareTo(version);
			
			if(comp>0) ret.get("smaller").add(version);
			else if(comp<0) ret.get("smaller").add(version);
			
		}
		
		return ret;
		
	}
	
	/*
	 * Necessary to parcellation...
	 */

	/**
	 * 
	 * @param in
	 */
	public VersionApk(Parcel in) { 
		this(in.readString(),in.readString(), in.readInt());
	}
	
	/**
	 * It may happen that your class will have child classes, so each of child in this case can 
	 * return in describeContent() different values, 
	 * so you would know which particular object type to create from Parcel.
	 * 
	 * In this case one doesn't need to implement all 
	 * Parcelable methods in child classes - except describeContent(), this one.
	 */
	public int describeContents() { return 0; }

	/**
	 * 
	 */
	public void writeToParcel(Parcel dest, int flags) { 
		dest.writeString(this.getVersion()); 
		dest.writeString(this.getApkId());
		dest.writeInt(this.size);
	}
	
	/**
	 * 
	 */
	public static final Parcelable.Creator<VersionApk> CREATOR = new Parcelable.Creator<VersionApk>() {
		public VersionApk createFromParcel(Parcel in) { return new VersionApk(in); }	
		public VersionApk[] newArray(int size) { return new VersionApk[size]; }
	};
	
}
