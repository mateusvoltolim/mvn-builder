package mvnproject;

import mvnproject.service.MvnBuildService;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.utils.cli.CommandLineException;

/**
 * Main App
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public class MainMvnBuildHelper {
	
	private static final MvnBuildService MAVEN_BUILD_SERVICE = new MvnBuildService();
	
	public static void main(String[] args) throws MavenInvocationException, CommandLineException {
		MAVEN_BUILD_SERVICE.build();
	}
	
}
