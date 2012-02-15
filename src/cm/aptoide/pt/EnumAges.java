package cm.aptoide.pt;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EnumAges {
	ALL("All"), PRETEEN("Pre-Teen"), TEEN("Teen"), MATURE("Mature");

	private static final Map<String, EnumAges> lookup = new HashMap<String, EnumAges>();

	static {
		for (EnumAges s : EnumSet.allOf(EnumAges.class))
			lookup.put(s.getCode(), s);
	}

	private String code;

	private EnumAges(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static EnumAges get(String code) {
		return lookup.get(code);
	}
	
	
	
}
