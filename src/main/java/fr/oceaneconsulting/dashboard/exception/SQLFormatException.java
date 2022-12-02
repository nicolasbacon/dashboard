package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used during a SQL formatting error.
 */
public class SQLFormatException extends FormatException{

	private static final long serialVersionUID = 5275284550173034336L;

	/**
	 * Create an exception when there is a SQL formatting problem.
	 */
	public SQLFormatException() {
		super("An error occurred during a SQL format process");
	}

	/**
	 * Create an exception when there is a SQL formatting problem.
	 *
	 * @param message message of the exception
	 */
	public SQLFormatException(String message) {
		super(message);
	}

}