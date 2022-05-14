package mvnproject.exceptions;

/**
 * Exception to handle any errors while initiate configurations
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public class InitConfigurationException extends RuntimeException {

	public InitConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
