package mvnproject.properties;

import mvnproject.exceptions.InitConfigurationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static mvnproject.properties.MvnKeysProperties.DIR_BASE_CHECKOUT;
import static mvnproject.properties.MvnKeysProperties.GOALS;
import static mvnproject.properties.MvnKeysProperties.LOCAL_REPOSITORY;
import static mvnproject.properties.MvnKeysProperties.M2_HOME;
import static mvnproject.properties.MvnKeysProperties.MAVEN_HOME;
import static mvnproject.properties.MvnKeysProperties.MODULES;
import static mvnproject.properties.MvnKeysProperties.THROW_FAILURE;

/**
 * Initiates maven configuration
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public final class MvnConfigurationProperties {
	
	private static final Logger LOGGER = LogManager.getLogger(MvnConfigurationProperties.class);
	private static final String JSON_FILE_NAME = "/maven.json";
	
	private static MvnConfigurationProperties instance;
	
	private final Configuration configuration;
	
	private LocalRepository localRepository;
	
	public static synchronized MvnConfigurationProperties instance() {
		if (instance == null) {
			instance = new MvnConfigurationProperties();
		}
		return instance;
	}
	
	private MvnConfigurationProperties() {
		final Configurations configurations = new Configurations();
		try {
			FileBasedConfigurationBuilder<JSONConfiguration> builder = configurations.fileBasedBuilder(
					JSONConfiguration.class, IOUtils.resourceToURL(JSON_FILE_NAME));
			
			configuration = builder.getConfiguration();
			
		} catch (IOException | ConfigurationException e) {
			String message = format("Failure to initiate configurations: {0}", JSON_FILE_NAME);
			throw new InitConfigurationException(message, e);
		}
	}
	
	public String dirBaseCheckout() {
		return configuration.getString(DIR_BASE_CHECKOUT.getValue());
	}
	
	public File mavenHomeFile() {
		String mavenHome = configuration.getString(MAVEN_HOME.getValue());
		return new File(
				StringUtils.defaultIfBlank(
						mavenHome,
						StringUtils.defaultIfBlank(
								System.getenv(MAVEN_HOME.name()),
								System.getenv(M2_HOME.name())
						)
				)
		);
	}
	
	public List<String> goals() {
		return configuration.getList(String.class, GOALS.getValue());
	}
	
	public synchronized LocalRepository localRepository() {
		if (localRepository != null) {
			return localRepository;
		}
		
		localRepository = new LocalRepository();
		Iterator<String> keys = configuration.getKeys(LOCAL_REPOSITORY.getValue());
		List<Field> fields = Arrays.asList(LocalRepository.class.getDeclaredFields());
		Iterator<Field> iterator = fields.iterator();
		
		while (keys.hasNext()) {
			try {
				Field field = iterator.next();
				Object value = configuration.get(field.getType(), keys.next());
				field.set(localRepository, value);
				
			} catch (IllegalAccessException e) {
				LOGGER.error("Access to variable not allowed", e);
			}
		}
		return localRepository;
	}
	
	public Map<String, String> modules() {
		Map<String, String> map = new LinkedHashMap<>();
		for (String key : configuration.getList(String.class, MODULES.getValue())) {
			map.put(key, dirBaseCheckout() + key + "/pom.xml");
		}
		
		return map;
	}
	
	public boolean isThrowFailure() {
		return configuration.getBoolean(THROW_FAILURE.getValue());
	}
	
	public static class LocalRepository {
		
		Boolean enable;
		Integer maxAgeFiles;
		String path;
		
		public Boolean getEnable() {
			return enable;
		}
		
		public Integer getMaxAgeFiles() {
			return maxAgeFiles;
		}
		
		public String getPath() {
			return path;
		}
		
	}
	
}
