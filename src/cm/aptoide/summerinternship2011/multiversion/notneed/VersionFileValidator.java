///**
// * 
// */
//package cm.aptoide.summerinternship2011.multiversion.xml;
//
//import java.io.File;
//import java.io.IOException;
//
//import javax.xml.transform.Source;
//import javax.xml.transform.sax.SAXSource;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.validation.Schema;
//import javax.xml.validation.SchemaFactory;
//import javax.xml.validation.Validator;
//
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import cm.aptoide.summerinternship2011.ResourceSource;
//import cm.aptoide.summerinternship2011.multiversion.xml.Utils;
///**
// * @author rafael
// * @since summerinternship2011
// * 
// */
//public class VersionFileValidator {
//	
//	private String sourcePath;
//	private String versionFileSchema;
//	private ResourceSource source;
//	
//	/**
//	 * 
//	 * @param sourcePath
//	 * @param source
//	 */
//	public VersionFileValidator(String sourcePath, ResourceSource source, String versionFileSchema) {
//		this.sourcePath = sourcePath;
//		this.versionFileSchema = versionFileSchema;
//		this.source = source;
//	}
//	
//	/**
//	 * 
//	 * @throws IOException
//	 * @throws SAXException
//	 */
//	public void validFile() throws IOException, SAXException {
//		// Look for a xml standard
//        SchemaFactory factory = 
//        SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//        // Compile schema
//    	File schemaLocation = new File(versionFileSchema);
//        Schema schema = factory.newSchema(schemaLocation);
//        // Get a schema validator
//        Validator validator = schema.newValidator();
//        // Do the document parser
//        Source source = null;
//        switch(this.source){
//	        case WEB: 
//	        	source = new SAXSource(new InputSource(Utils.getXMLInputStream(sourcePath)));
//	        	break;
//	        case FILE: 
//	        	source = new StreamSource(sourcePath); 
//	        	break;
//	        default: throw new IllegalArgumentException("Resource source is invalid.");
//        }
//        // Validate document
//        validator.validate(source);
//        
//	}
//
//	/**
//	 * @return the sourcePath
//	 */
//	public String getSourcePath() {
//		return sourcePath;
//	}
//
//	/**
//	 * @return the versionFileSchema
//	 */
//	public String getVersionFileSchema() {
//		return versionFileSchema;
//	}
//
//	/**
//	 * @return the source
//	 */
//	public ResourceSource getSource() {
//		return source;
//	}
//	
//}
