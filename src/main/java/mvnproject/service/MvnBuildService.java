package mvnproject.service;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

import mvnproject.helpers.M2Cleaner;
import mvnproject.properties.MvnConfigurationProperties;
import mvnproject.properties.MvnConfigurationProperties.LocalRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.utils.cli.CommandLineException;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Executes the maven build
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public final class MvnBuildService {
	
	private static final Logger LOGGER = LogManager.getLogger(MvnBuildService.class);
	private static final MvnConfigurationProperties PROPERTIES = MvnConfigurationProperties.instance();
	private final Map<String, String> report;
	
	public MvnBuildService() {
		this.report = new LinkedHashMap<>();
	}
	/**
	 * Executes the build of projects set on file configuration
	 *
	 * @throws MavenInvocationException if the {@link Invoker} has some problem to execute {@link InvocationRequest}
	 */
	public void build() throws MavenInvocationException, CommandLineException {
		cleanRepository();
		executeMavenRequest();
		
		LOGGER.info("");
		report.forEach((k, v) -> LOGGER.info(String.format("%s --> %s", StringUtils.rightPad(k, 30), v)));
	}
	
	/**
	 * Executes maven request for each project
	 *
	 * @throws MavenInvocationException if the {@link Invoker} has some problem to execute {@link InvocationRequest}
	 */
	private void executeMavenRequest() throws MavenInvocationException, CommandLineException {
		for (Entry<String, String> map : PROPERTIES.modules().entrySet()) {
			LocalTime start = LocalTime.now();
			try {
				InvocationRequest request = new DefaultInvocationRequest();
				request.setPomFile(new File(map.getValue()));
				request.setGoals(PROPERTIES.goals());
				
				Invoker invoker = new DefaultInvoker();
				invoker.setMavenHome(PROPERTIES.mavenHomeFile());
				InvocationResult result = invoker.execute(request);
				CommandLineException ex = result.getExecutionException();
				if (ex != null) {
					throw ex;
				}
				
				if (result.getExitCode() != 0) {
					updateReport(map, start, "FAILURE - Execution time: ");
					return;
				}
				
				updateReport(map, start, "OK - Execution time: ");
			} catch (MavenInvocationException | CommandLineException e) {
				updateReport(map, start, "FAILURE - Execution time: ");
				if (PROPERTIES.isThrowFailure()) {
					throw e;
				}
			}
		}
	}
	
	/**
	 * Calls {@link M2Cleaner} to clean local repository
	 */
	private void cleanRepository() {
		LocalRepository localRepository = PROPERTIES.localRepository();
		if (BooleanUtils.toBoolean(localRepository.getEnable())) {
			M2Cleaner.clean(localRepository.getPath(), localRepository.getMaxAgeFiles());
		}
	}
	
	private void updateReport(Entry<String, String> map, LocalTime start, String message) {
		LocalTime end = LocalTime.now();
		String duration = formatDuration(Duration.between(start, end).toMillis(), "HH:mm:ss");
		report.put(map.getKey(), message + duration);
	}
	
}
