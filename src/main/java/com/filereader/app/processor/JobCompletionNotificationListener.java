package com.filereader.app.processor;

import com.filereader.app.service.IArchiverService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * JobCompletionNotificationListener class is used to move the file to archive or error directory based on the job status.
 */
@RequiredArgsConstructor
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final IArchiverService archiverService;

    /**
     * The afterJob method is used to perform the action after the job execution.
     * @param jobExecution a {@link JobExecution} object.
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        if(StringUtils.isAllBlank(filePath)){
            filePath = jobExecution.getJobParameters().getString("bucketName");
        }
        String fileName = jobExecution.getJobParameters().getString("fileName");
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            this.archiverService.moveFile(filePath, fileName,true);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            this.archiverService.moveFile(filePath, fileName,false);
        }
    }
}
