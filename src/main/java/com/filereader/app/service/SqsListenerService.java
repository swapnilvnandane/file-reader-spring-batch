package com.filereader.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@RequiredArgsConstructor
@Profile("aws")
@Service
public class SqsListenerService implements Runnable {

    @Value("${com.aws.sqs.queuename:}")
    private String queueName;

    /** The jobLauncher is used to launch the job. **/
    private final SqsClient sqsClient;

    // The JobLauncher class is used to launch the job.
    private final JobLauncher jobLauncher;

    // The Job class is used to define the job.
    private final Job job;

    /** ObjectMapper is used map string json to Object. **/
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void listen() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        GetQueueUrlRequest req = GetQueueUrlRequest.builder().queueName(queueName).build();
        GetQueueUrlResponse res = sqsClient.getQueueUrl(req);
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(res.queueUrl())
                .maxNumberOfMessages(1)
                .waitTimeSeconds(20)
                .build();
        while (true) {
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            for (Message message : messages) {
                processMessage(message);
                sqsClient.deleteMessage(builder -> builder.queueUrl(res.queueUrl()).receiptHandle(message.receiptHandle()));
            }
        }
    }

    private void processMessage(Message message) {
        try {
            // Parse S3 event notification message
            JsonNode jsonNode = objectMapper.readTree(message.body());
            String bucketName = jsonNode.get("Records").get(0).get("s3").get("bucket").get("name").asText();
            String fileName = jsonNode.get("Records").get(0).get("s3").get("object").get("key").asText();

            // Trigger batch job with S3 file path as a parameter
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileName", fileName)
                    .addString("bucketName", bucketName)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
