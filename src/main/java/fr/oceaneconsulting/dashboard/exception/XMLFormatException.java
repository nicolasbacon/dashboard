package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used during a XML formatting error.
 */
public class XMLFormatException extends FormatException{

	private static final long serialVersionUID = -9141383888899556716L;

	/**
	 * Create an exception when there is a XML formatting problem.
	 */
	public XMLFormatException() {
		super("An error occurred during a XML format process");
	}

	/**
	 * Create an exception when there is a XML formatting problem.
	 *
	 * @param message message of the exception
	 */
	public XMLFormatException(String message) {
		super(message);
	}

}
