package cm.aptoide.pt.annotations;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
public @interface Issue {
	String value();
	String author() default "Unknown";
	Priority priority() default Priority.NOT_DEFINED;
	Type type() default Type.NOT_DEFINED;
}