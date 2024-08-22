package com.filereader.app.processor;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

/**
 * CustomStepListener class is used to handle the step exceptions.
 */
public class CustomStepListener extends StepExecutionListenerSupport {

    /**
     * The afterStep method is used to handle the step exceptions.
     * @param stepExecution a {@link StepExecution} object.
     * @return a {@link ExitStatus} object.
     */
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
