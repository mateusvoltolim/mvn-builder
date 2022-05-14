package mvnproject.properties;

/**
 * Enum to parameters configurations
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public enum MvnKeysProperties {
	
	/**
	 * Path where are located the directory base for all repositories checked out
	 */
	DIR_BASE_CHECKOUT("dirBaseCheckout"),
	
	/**
	 * Path where is located the maven binaries. If blank, it'll be considered the Environment Variables
	 */
	MAVEN_HOME("mavenHome"),
	M2_HOME("mavenHome"),
	
	/**
	 * Maven parameters
	 */
	GOALS("goals"),
	
	/**
	 * Path where is located the local repository (dependencies downloaded by maven)
	 */
	LOCAL_REPOSITORY("localRepository"),
	
	/**
	 * The modules from parent's which desire to build
	 */
	MODULES("modules"),
	
	/**
	 * If the build should throw any failure and stop execution
	 */
	THROW_FAILURE("throwFailure");
	
	private final String value;
	
	MvnKeysProperties(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
