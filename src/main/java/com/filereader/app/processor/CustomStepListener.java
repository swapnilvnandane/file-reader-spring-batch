package com.filereader.app.processor;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class CustomStepListener extends StepExecutionListenerSupport {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getFailureExceptions().isEmpty()) {
            return ExitStatus.COMPLETED;
        } else {
            // Handle step exceptions
            return ExitStatus.FAILED;
        }
    }
}
