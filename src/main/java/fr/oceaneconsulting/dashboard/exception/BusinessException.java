package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used during a business error.
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 5003127900946459176L;

	/**
     * Create an exception when there is a business problem.
     */
    public BusinessException() {
        super("An error occurred during a business process");
    }

    /**
     * Create an exception when there is a business problem.
     *
     * @param message message of the exception
     */
    public BusinessException(final String message) {
        super(message);
    }

}
