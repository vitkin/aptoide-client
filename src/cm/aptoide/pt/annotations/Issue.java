package cm.aptoide.pt.annotations;

//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
//@Target(ElementType.TYPE)
//@Retention(RetentionPolicy.CLASS)
//@Retention(RetentionPolicy.RUNTIME)
public @interface Issue {
	String value();
	String author() default "Unknown";
	Priority priority() default Priority.NOT_DEFINED;
	Type type() default Type.NOT_DEFINED;
}