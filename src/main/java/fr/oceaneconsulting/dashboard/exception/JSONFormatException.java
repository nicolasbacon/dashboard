package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used during a JSON formatting error.
 */
public class JSONFormatException extends FormatException{

	private static final long serialVersionUID = -4276394160408992025L;

	/**
     * Create an exception when there is a JSON formatting problem.
     */
	public JSONFormatException() {
		super("An error occurred during a JSON format process");
	}

	/**
     * Create an exception when there is a JSON formatting problem.
     *
     * @param message message of the exception
     */
	public JSONFormatException(String message) {
		super(message);
	}

}
