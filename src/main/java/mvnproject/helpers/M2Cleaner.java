package mvnproject.helpers;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Helper to filter and clean the garbage in local repository path
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public class M2Cleaner {

    private M2Cleaner() {
    }

    private static final Logger LOGGER = LogManager.getLogger(M2Cleaner.class);

    public static void clean(String pathRepository, Integer maxAgeFiles) {
        try (Stream<Path> paths = Files.list(Paths.get(pathRepository))) {
            paths.forEach(path -> tryDeleteFiles(maxAgeFiles, path));
        } catch (IOException e) {
            LOGGER.error("Failure to list directories", e);
        }
    }

    private static void tryDeleteFiles(Integer maxAgeFiles, Path path) {
        try (Stream<Path> subFiles = Files.list(path)) {
            deleteFiles(getFilteredFiles(subFiles, maxAgeFiles));
        } catch (IOException | XmlPullParserException e) {
            LOGGER.error("Failure to list files", e);
        }
    }

    private static void deleteFiles(Stream<File> filteredFiles) {
        filteredFiles.forEach(file -> {
            LOGGER.info("Removing folders: {}", file.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                LOGGER.error("Failure to delete folders", e);
            }
        });
    }

    private static Stream<File> getFilteredFiles(Stream<Path> subFiles, Integer maxAgeFiles)
            throws IOException, XmlPullParserException {
        return subFiles
                .map(Path::toFile)
                .filter(File::isDirectory)
                .filter(file -> ChronoUnit.DAYS.between(getLastModifiedDate(file), LocalDate.now()) >= maxAgeFiles);
    }

    private static LocalDate getLastModifiedDate(File file) {
        return Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
