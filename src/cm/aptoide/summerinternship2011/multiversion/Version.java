/**
 * 
 */
package cm.aptoide.summerinternship2011.multiversion;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * A description of a app version that provides to work easily with them.
 * 
 */
public class Version implements Comparable<Version>{
	
	/**
	 * Formated version
	 */
	private String version; 
	/**
	 * A field to easily compare versions
	 */
	private String rawVersion;
	/**
	 * A pattern for version format confirming
	 */
	private Pattern versionPattern;
	
	/**
	 * 
	 * @param version
	 */ 
	public Version(final String version) {
		versionPattern = Pattern.compile("^((\\d)+\\.)+(\\d)+$");
		this.version = version;
		rawVersion = processRawVersion(this.version);
	}
	
	public String getVersionString() {
		return version;
	}
	
	public String getRawVersion() {
		return rawVersion;
	}
	
	public String processRawVersion(final String version){
		confirmPattern(version);
		return version.replace(".", "");	
	}
	
	public void confirmPattern(String version){
		if(!versionPattern.matcher(version).matches())
			throw new NotValidVersionException(version);
	}
	
	public int compareTo(Version version) {
		StringBuilder[] rawVersions = ensureSameCharacterLength(version);
		System.out.println(rawVersions[0].toString() +" "+ rawVersions[1].toString());
		return new BigInteger(rawVersions[0].toString()).compareTo(new BigInteger(rawVersions[1].toString()));
	}
	
	/**
	 * Ensure that the rawVersion attribute of this class is the same length as the givenVersionRawVersion, by adding zeros at the end of the smaller string to make their length match.
	 * @param givenVersion
	 * @return A string array with two elements, the first is rawVersion of this class the second the rawVersion of the givenClass
	 */
	public StringBuilder[] ensureSameCharacterLength(final Version givenVersion){
		final StringBuilder[] rawVersions = {new StringBuilder(rawVersion), new StringBuilder(givenVersion.getRawVersion())};
		int rawVerionLengthDifference = rawVersion.length()-givenVersion.getRawVersion().length();
		if(rawVerionLengthDifference<0){
			rawVersions[0].append(getStringFielded(-rawVerionLengthDifference,'0')); 
		}else if(rawVerionLengthDifference>0){
			rawVersions[1].append(getStringFielded(rawVerionLengthDifference,'0')); 
		}
		return rawVersions;
	}
	
	@Override
	public String toString() { return getVersionString(); }
	
//	/**
//	 * @param size Size of the desired String
//	 * @param filedWith Character to field the string with
//	 * @return A String fielded with the filedWith and with a length size
//	 */
//	public String getStringFielded(int size,final char filedWith){
//		LinkedList<Character> stringLinkedList = new LinkedList<Character>();
//		while(size-->0)
//			stringLinkedList.add(filedWith);
//		return Arrays.toString(stringLinkedList.toArray(new Character[stringLinkedList.size()])).replaceAll("(\\[|\\])", "");
//	}
	
	/**
	 * @param size Size of the desired String
	 * @param filedWith Character to field the string with
	 * @return A String fielded with the filedWith and with a length size
	 */
	public String getStringFielded(int size,char fillWith){
		char[] buffer = new char[size];
		Arrays.fill(buffer, fillWith);
		return Arrays.toString(buffer).replaceAll("\\[|\\]|\\s|,", "");
	}
	
	@Override
	public boolean equals(Object version) {
		if(!(version instanceof Version))
			throw new IllegalArgumentException("The given argument is not a instance of Version class");
		return rawVersion.equals((Version) version);
	}
	
	@SuppressWarnings("serial")
	public class NotValidVersionException extends RuntimeException{
		
		public NotValidVersionException(String versionIncorrect) {
			super("The version format \""+versionIncorrect+"\" is incorrect");
		}
		
	}
	
}
