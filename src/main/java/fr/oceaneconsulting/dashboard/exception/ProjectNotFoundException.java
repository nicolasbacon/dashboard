package fr.oceaneconsulting.dashboard.exception;

/**
 * Exception class used when a project has not been found.
 */
public class ProjectNotFoundException extends NotFoundException{

	private static final long serialVersionUID = -1547710324675397304L;

	/**
	 * Create an exception when retrieving a project.
	 */
	public ProjectNotFoundException() {
		super("No Project(s) was found");
	}

	/**
     * Create an exception when retrieving a project.
     *
     * @param message message of the exception
     */
	public ProjectNotFoundException(String message) {
		super(message);
	}
}
