package com.filereader.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * FileArchiverServiceImpl is used to move the file to archive or error directory based on the job status.
 */
@Profile("filesystem")
@Service
public class FileArchiverServiceImpl implements IArchiverService {

    @Value("${com.file.archive}")
    private String archiveDirectory;

    @Value("${com.file.error}")
    private String errorDirectory;

    /**
     * The moveFile method is used to move the file to the target directory.
     *
     * @param filePath a {@link String} object.
     * @param fileName a {@link String} object.
     * @param success  a {@link boolean} object.
     */
    @Override
    public void moveFile(String filePath, String fileName, boolean success) {
        // Move the file to archive directory if the job is successful, otherwise error directory.
        try {
            String targetDirectory = success ? archiveDirectory : errorDirectory;
            Files.move(Paths.get(filePath), Paths.get(targetDirectory + fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
