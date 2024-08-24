package com.filereader.app.processor;

import org.springframework.core.io.AbstractResource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * A Spring {@link AbstractResource} implementation for an S3 object.
 */
public class S3Resource extends AbstractResource {

    private final S3Client s3Client;
    private final String bucketName;
    private final String fileName;

    /**
     * Creates a new {@link S3Resource}.
     *
     * @param s3Client   the S3 client
     * @param bucketName the bucket name
     * @param fileName   the file name
     */
    public S3Resource(S3Client s3Client, String bucketName, String fileName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.fileName = fileName;
    }

    /**
     * Description that returns S3 resource details.
     *
     * @return details of the S3 resource
     */
    @Override
    public String getDescription() {
        return "S3 resource [bucket='" + bucketName + "', fileName='" + fileName + "']";
    }

    /**
     * Returns the input stream for the S3 object.
     *
     * @return the input stream
     * @throws IOException if the object cannot be read
     */
    @Override
    public InputStream getInputStream() throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}
