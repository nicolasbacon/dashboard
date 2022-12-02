package fr.oceaneconsulting.dashboard.connector.mantis.model.enums;

public enum MantisTreatmentStatus {
	NOUVEAU(10),
	REOUVERT(12),
	RETOUR_CLIENT_EFFECTUE(15),
	ANALYSE_EN_COURS(17),
	EN_ATTENTE_RETOUR_CLIENT(20),
	A_TRAITER(30),
	RESOLUTION_EN_COURS(50),
	TRAITE(80),
	A_RECETTER_USINE(81),
	VALIDE_USINE(82),
	A_DISPOSITION(83),
	FERME(90),
	UNKNOWN;

	private int id;

	MantisTreatmentStatus(int id) {
		this.id = id;
	}

	MantisTreatmentStatus() {
	}

	public static MantisTreatmentStatus valueOfId(int id) {
	    for (MantisTreatmentStatus e : values()) {
	        if (e.id == id) {
	            return e;
	        }
	    }
	    return MantisTreatmentStatus.UNKNOWN;
	}
}
