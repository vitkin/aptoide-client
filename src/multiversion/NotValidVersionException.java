package multiversion;

/**
 * @author rafael
 * 
 * Describes a version that isn't correct for a given pattern.
 */
@SuppressWarnings("serial")
public class NotValidVersionException extends RuntimeException{
	
	public NotValidVersionException(String versionIncorrect) {
		super("The version format \""+versionIncorrect+"\" is incorrect");
	}
	
}
