package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used when a request has not been found.
 */
public class RequestNotFoundException extends NotFoundException{

	private static final long serialVersionUID = -6701773374930198293L;

	/**
     * Create an exception when retrieving a Request
     */
	public RequestNotFoundException() {
		super("No Request(s) was found");
	}

	/**
     * Create an exception when retrieving a Request
     *
     * @param message message of the exception
     */
	public RequestNotFoundException(String message) {
		super(message);
	}
}
