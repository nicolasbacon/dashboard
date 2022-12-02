package fr.oceaneconsulting.dashboard.connector.mantis.model.enums;

public enum MantisRequestType {
	ANOMALIE,
	DEMANDES_QUESTIONS,
	EVOLUTION,
	UNKNOWN;

	public static MantisRequestType valueOfIgnoreSpaceAndCase(String value) {
		if (value != null)
			if(!value.isBlank()) {
			for (MantisRequestType e : values()) {
				if (value.replace(" ", "_").toUpperCase().equals(e.toString())) {
					return e;
				}
			}
		}
	    return MantisRequestType.UNKNOWN;
	}
}
