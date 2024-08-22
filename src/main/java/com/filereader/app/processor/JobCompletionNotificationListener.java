package com.filereader.app.processor;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Value("${com.file.archive}")
    private String archiveDirectory;

    @Value("${com.file.error}")
    private String errorDirectory;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            moveFile(jobExecution, archiveDirectory);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            moveFile(jobExecution, errorDirectory);
        }
    }

    private void moveFile(JobExecution jobExecution, String targetDirectory) {
        try {
            String fiePath = jobExecution.getJobParameters().getString("filePath");
            String fileName = jobExecution.getJobParameters().getString("fileName");
            Files.move(Paths.get(fiePath), Paths.get(targetDirectory+fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
