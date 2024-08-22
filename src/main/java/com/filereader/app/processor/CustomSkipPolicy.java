package com.filereader.app.processor;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

/**
 * CustomSkipPolicy class is used to skip the exception and continue the job execution.
 */
public class CustomSkipPolicy implements SkipPolicy {

    /**
     * The shouldSkip method is used to skip the exception and continue the job execution.
     * @param t a {@link Throwable} object.
     * @param skipCount a {@link long} value.
     * @return a {@link boolean} value.
     * @throws SkipLimitExceededException a {@link SkipLimitExceededException} object.
     */
    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
        return false;
    }
}
