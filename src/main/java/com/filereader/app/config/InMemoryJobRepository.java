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

public class InMemoryJobRepository implements JobRepository {

    private final Map<Long, JobInstance> jobInstanceMap = new ConcurrentHashMap<>();
    private final Map<Long, JobExecution> jobExecutionMap = new ConcurrentHashMap<>();
    private final PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
    private final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

    @Override
    public boolean isJobInstanceExists(String jobName, JobParameters jobParameters) {
        return this.jobInstanceMap.values().stream()
                .anyMatch(jobInstance -> jobInstance.getJobName().equals(jobName));
    }

    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
        JobInstance jobInstance = new JobInstance(System.currentTimeMillis(), jobName);
        jobInstanceMap.put(jobInstance.getId(), jobInstance);
        return jobInstance;
    }

    @Override
    public JobExecution createJobExecution(String jobName, JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobInstance jobInstance = jobInstanceMap.values().stream()
                .filter(ji -> ji.getJobName().equals(jobName))
                .findFirst()
                .orElse(createJobInstance(jobName, jobParameters));
        JobExecution jobExecution = new JobExecution(jobInstance, System.currentTimeMillis(), jobParameters);
        jobExecutionMap.put(jobExecution.getId(), jobExecution);
        return jobExecution;
    }

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
