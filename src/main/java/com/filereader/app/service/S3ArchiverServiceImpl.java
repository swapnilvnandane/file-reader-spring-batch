package com.filereader.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3ArchiverServiceImpl is used to move the file to S3 bucket based on the job status.
 */
@RequiredArgsConstructor
@Profile("aws")
@Service
public class S3ArchiverServiceImpl implements IArchiverService {

    /**
     * The S3Client is used to interact with the S3 bucket.
     **/
    private final S3Client s3Client;

    /**
     * The moveFile method is used to move the file to the S3 bucket.
     *
     * @param bucketName a {@link String} object.
     * @param fileName a {@link String} object.
     * @param success  a {@link boolean} object.
     */
    @Override
    public void moveFile(String bucketName, String fileName, boolean success) {
        try {
            String fileNameWithoutPath = fileName.substring(fileName.lastIndexOf("/") + 1);
            String targetKey = success ? "archive/" + fileNameWithoutPath : "error/" + fileNameWithoutPath;
            s3Client.copyObject(copyObjectRequest -> copyObjectRequest
                    .sourceBucket(bucketName)
                    .sourceKey(fileName)
                    .destinationBucket(bucketName)
                    .destinationKey(targetKey));
            s3Client.deleteObject(deleteObjectRequest -> deleteObjectRequest
                    .bucket(bucketName)
                    .key(fileName));
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
