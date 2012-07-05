package cm.aptoide.pt.utils;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
public class Security {

	private Security(){}
	
	/**
	 * Useful to convert the digest to the hash
	 * 
	 * @param b
	 * @return
	 */
	public static String byteArrayToHexString(byte[] b) {
		  StringBuilder result = new StringBuilder("");
		  for (int i=0; i < b.length; i++) {
		    result.append(Integer.toString( (b[i] & 0xff) + 0x100 , 16).substring( 1 ));
		  }
		  return result.toString();
	}

}
