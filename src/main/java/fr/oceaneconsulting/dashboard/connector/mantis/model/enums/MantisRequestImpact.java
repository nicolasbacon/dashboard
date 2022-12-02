package fr.oceaneconsulting.dashboard.connector.mantis.model.enums;

public enum MantisRequestImpact {
//	FONCTIONNALITE(10),
//	SIMPLE(20),
//	TEXTE(30),
//	COSMETIQUE,
	MINEUR(50),
	MAJEUR(60),
	CRITIQUE(70),
	BLOQUANT(80),
//	NORMAL(30),
	UNKNOWN;

	private int id;

	MantisRequestImpact(int id) {
		this.id = id;
	}

	MantisRequestImpact() {
	}

	public static MantisRequestImpact valueOfId(int id) {
	    for (MantisRequestImpact e : values()) {
	        if (e.id == id) {
	            return e;
	        }
	    }
	    return MantisRequestImpact.UNKNOWN;
	}

}
