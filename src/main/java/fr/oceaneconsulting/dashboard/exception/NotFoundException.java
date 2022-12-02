package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used when a resource has not been found.
 */
public abstract class NotFoundException extends Exception{

	private static final long serialVersionUID = 8812366883140659340L;

	/**
	 * Create an exception when retrieving a resource.
	 */
	public NotFoundException() {
		super("No resource(s) was found");
	}

	/**
     * Create an exception when retrieving a ressource.
     *
     * @param message message of the exception
     */
	public NotFoundException(String message) {
		super(message);
	}
}
