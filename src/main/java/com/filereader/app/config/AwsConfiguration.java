package com.filereader.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * AwsConfiguration class is used to configure the AWS services, if profile active.
 */
@Profile("aws")
@Configuration
public class AwsConfiguration {

    @Value("${com.aws.accessKey.id}")
    private String accessKeyId;

    @Value("${com.aws.accessKey.secret}")
    private String secretAccessKey;

    @Value("${com.aws.region}")
    private String region;

    /**
     * The amazon S3 service method is used to create the S3Client object.
     * @return {@link S3Client} object.
     */
    @Bean
    public S3Client amazonS3() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                            )
                        )
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
                .build();
    }
}
