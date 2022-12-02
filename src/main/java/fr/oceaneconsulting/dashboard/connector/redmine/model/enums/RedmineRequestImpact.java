package fr.oceaneconsulting.dashboard.connector.redmine.model.enums;

public enum RedmineRequestImpact {
	BASSE(32),
	NORMAL(1),
	HAUTE(6),
	CRITIQUE(7),
	UNKNOWN;

	private int id;

	RedmineRequestImpact(int id) {
		this.id = id;
	}

	RedmineRequestImpact() {
	}

	public static RedmineRequestImpact valueOfId(int id) {
		for (RedmineRequestImpact e : values()) {
			if (e.id == id) {
				return e;
			}
		}
		return RedmineRequestImpact.UNKNOWN;
	}

}
