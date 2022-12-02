package fr.oceaneconsulting.dashboard.connector.redmine.model.enums;

public enum RedmineTreatmentStatus {
	NOUVEAU(2),
	EN_COURS(3),
	RESOLU(4),
	COMMENTAIRE(5),
	FERME(6),
	REJETE(7),
	COMPLETE(8),
	INITIE(9),
	VALIDE(10),
	LIVRE(11),
	DEMANDE_DABANDON(12),
	RECETTE_EFFECTUEE(13),
	A_CHIFFRER(14),
	DEVIS_A_EMETTRE(15),
	EN_ATTENTE_DE_COMMANDE(16),
	A_DEFINIR(17),
	A_REALISER(18),
	A_HOMOLOGUER(19),
	A_CORRIGER(20),
	PERSISTANT(21),
	UNKNOWN;

	private int id;

	RedmineTreatmentStatus(int id) {
		this.id = id;
	}

	RedmineTreatmentStatus() {
	}

	public static RedmineTreatmentStatus valueOfId(int id) {
	    for (RedmineTreatmentStatus e : values()) {
	        if (e.id == id) {
	            return e;
	        }
	    }
	    return RedmineTreatmentStatus.UNKNOWN;
	}
}
