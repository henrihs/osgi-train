package no.ntnu.item.its.osgi.map.model;

import java.util.regex.Pattern;

public class RailLegId {
	
//	public final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String PATTERN = "\\d{1,8}[e|t|d]{1}.\\d{1,8}[e|t|d]{1}";
	private static final String DELIMITER = ".";
	private final String stringId;
	
	public RailLegId(String id){
		if (validate(id))
			stringId = id;
		else 
			throw new IllegalArgumentException("Illegal format: ".concat(id));
	}

	public RailLegId(PointConnector connector1, PointConnector connector2){
		this(buildOrderedIdString(connector1, connector2));
	}

	public String value() {
		return stringId;
	}
	
	@Override
	public boolean equals(Object other){
		if (other.getClass() != this.getClass()){
			return false;
		}
		
		String[] o = other.toString().split(Pattern.quote(DELIMITER));
		String[] t = this.toString().split(Pattern.quote(DELIMITER));
		if (t[0].equals(o[0]) && t[1].equals(o[1])
			|| t[0].equals(o[1]) && t[1].equals(o[0])) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return stringId;
	}
	
	private static String buildOrderedIdString(PointConnector connector1, PointConnector connector2) {
		String idString1 = connector1.id().toString().
							concat(connector1.getType().shorthand());
		String idString2 = connector2.id().toString().
						concat(connector2.getType().shorthand());
		String idString;
		if (connector1.id().compareTo(
				connector2.id()) < 0) {
			idString = idString1.concat(DELIMITER).concat(idString2);
		}
		else if (connector1.id().compareTo(
				connector2.id()) > 0) {
			idString = idString2.concat(DELIMITER).concat(idString1);
		}
		else {
			throw new IllegalArgumentException("ID can not consist of two equal ID's: \"".
												concat(idString1).
												concat("\" and \"".
												concat(idString2).
												concat("\"")));
		}
		
		return idString;
	}
	
	private boolean validate(String stringId) {
		if (stringId.matches(PATTERN)) {			
			return true;
		}
		else {
			return false;
		}
	}
}
