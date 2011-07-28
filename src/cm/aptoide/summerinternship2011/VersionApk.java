/**
 * 
 */
package cm.aptoide.summerinternship2011;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author rafael
 * @since summerinternship2011
 *
 */
public class VersionApk {
	
	private Version version;
	private URL url;
	private final String md5;
	
	public VersionApk(String version, String uri, String md5) throws MalformedURLException {
		this.version = new Version(version);
		url = new URL(uri);
		this.md5 = md5;
	}
	
	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
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
	
}
