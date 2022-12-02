package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used during a formatting error.
 */
public abstract class FormatException extends Exception{

	private static final long serialVersionUID = 1354128916459399202L;

	/**
	 * Create an exception when there is a formatting problem.
	 */
	public FormatException() {
		super("An error occurred during a format process");
	}

	/**
	 * Create an exception when there is a formatting problem.
	 *
	 * @param message message of the exception
	 */
	public FormatException(String message) {
		super(message);
	}



}
