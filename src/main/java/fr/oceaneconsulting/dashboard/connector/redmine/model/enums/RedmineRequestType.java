package fr.oceaneconsulting.dashboard.connector.redmine.model.enums;

public enum RedmineRequestType {
	ANOMALIE(2),
	EVOLUTION(3),
	FONCTIONNEL(4),
	FORMATION(5),
	QUALIFICATION(6),
	AVANT_VENTE(7),
	CONGES(8),
	EPIC(11),
	USER_STORY(12),
	DEV_TU(13),
	TACHE_ANNEXE(14),
	UNKNOWN;

	private int id;

	RedmineRequestType(int id) {
		this.id = id;
	}

	RedmineRequestType() {
	}

	public static RedmineRequestType valueOfId(int id) {
	    for (RedmineRequestType e : values()) {
	        if (e.id == id) {
	            return e;
	        }
	    }
	    return RedmineRequestType.UNKNOWN;
	}
}
