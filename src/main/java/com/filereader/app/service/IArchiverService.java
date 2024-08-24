package com.filereader.app.service;

/**
 * IArchiverService interface is used to move the file to archive or error directory based on the job status.
 */
public interface IArchiverService {

    /**
     * The moveFile method is used to move the file to the target directory.
     * @param filePathOrBucketName a {@link String} object.
     * @param fileName a {@link String} object.
     * @param success a {@link boolean} object.
     */
    void moveFile(String filePathOrBucketName, String fileName, boolean success);
}
