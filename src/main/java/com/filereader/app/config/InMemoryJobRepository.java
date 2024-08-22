package com.filereader.app.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * InMemoryJobRepository class is used to store the job execution details in memory.
 */
public class InMemoryJobRepository implements JobRepository {

    /** The jobInstanceMap is used to store the job instance details. **/
    private final Map<Long, JobInstance> jobInstanceMap = new ConcurrentHashMap<>();

    /** The jobExecutionMap is used to store the job execution details. **/
    private final Map<Long, JobExecution> jobExecutionMap = new ConcurrentHashMap<>();

    /** The transactionManager is used to manage the transaction. **/
    private final PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();

    /** The transactionTemplate is used to execute the transaction. **/
    private final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

    /**
     * The isJobInstanceExists method is used to check whether the job instance exists or not.
     * @param jobName a {@link String} object.
     * @param jobParameters a {@link JobParameters} object.
     * @return a {@link boolean} value.
     */
    @Override
    public boolean isJobInstanceExists(String jobName, JobParameters jobParameters) {
        return this.jobInstanceMap.values().stream()
                .anyMatch(jobInstance -> jobInstance.getJobName().equals(jobName));
    }

    /**
     * The createJobInstance method is used to create the job instance.
     * @param jobName a {@link String} object.
     * @param jobParameters a {@link JobParameters} object.
     * @return a {@link JobInstance} object.
     */
    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
        JobInstance jobInstance = new JobInstance(System.currentTimeMillis(), jobName);
        jobInstanceMap.put(jobInstance.getId(), jobInstance);
        return jobInstance;
    }

    /**
     * The createJobExecution method is used to create the job execution.
     * @param jobName a {@link String} object.
     * @param jobParameters a {@link JobParameters} object.
     * @return a {@link JobExecution} object.
     * @throws JobExecutionAlreadyRunningException if the job execution is already running.
     * @throws JobRestartException if the job execution is restarted.
     * @throws JobInstanceAlreadyCompleteException if the job instance is already completed.
     */
    @Override
    public JobExecution createJobExecution(String jobName, JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobInstance jobInstance = jobInstanceMap.values().stream()
                .filter(ji -> ji.getJobName().equals(jobName))
                .findFirst()
                .orElse(createJobInstance(jobName, jobParameters));
        JobExecution jobExecution = new JobExecution(jobInstance, System.currentTimeMillis(), jobParameters);
        jobExecutionMap.put(jobExecution.getId(), jobExecution);
        return jobExecution;
    }

    /**
     * The update method is used to update the job execution.
     * @param jobExecution a {@link JobExecution} object.
     */
    @Override
    public void update(JobExecution jobExecution) {
        jobExecutionMap.putIfAbsent(jobExecution.getId(), jobExecution);
    }

    @Override
    public void add(StepExecution stepExecution) {
    }

    @Override
    public void addAll(Collection<StepExecution> stepExecutions) {
    }

    @Override
    public void update(StepExecution stepExecution) {
    }

    @Override
    public void updateExecutionContext(StepExecution stepExecution) {
    }

    @Override
    public void updateExecutionContext(JobExecution jobExecution) {
    }

    @Override
    public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
        return null;
    }

    @Override
    public long getStepExecutionCount(JobInstance jobInstance, String stepName) {
        return 0;
    }

    /**
     * The getLastJobExecution method is used to get the last job execution.
     * @param jobName a {@link String} object.
     * @param jobParameters a {@link JobParameters} object.
     * @return a {@link JobExecution} object.
     */
    @Override
    public JobExecution getLastJobExecution(String jobName, JobParameters jobParameters) {
        JobInstance jobInstance = jobInstanceMap.values().stream()
                .filter(instance -> instance.getJobName().equals(jobName))
                .findFirst()
                .orElse(null);
        return jobExecutionMap.values().stream()
                .filter(execution -> execution.getJobInstance().equals(jobInstance))
                .reduce((first, second) -> second)
                .orElse(null);
    }
}
