package com.filereader.app.config;

import com.filereader.app.entity.MyTest;
import com.filereader.app.processor.CustomLineMapper;
import com.filereader.app.processor.CustomSkipPolicy;
import com.filereader.app.processor.CustomStepListener;
import com.filereader.app.processor.DataTransformer;
import com.filereader.app.processor.JobCompletionNotificationListener;
import com.filereader.app.processor.ValidatingItemProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final CustomLineMapper lineMapper;
    private final DataTransformer writer;
    private final ValidatingItemProcessor processor;

    @Bean
    @Primary
    public JobRepository jobRepository() {
        return new InMemoryJobRepository();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public Job job(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step) {
        return new JobBuilder("fileProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("fileProcessingStep", jobRepository)
                .<MyTest, MyTest>chunk(10, transactionManager)
                .reader(fileItemReader(null))
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipPolicy(new CustomSkipPolicy())
                .listener(new CustomStepListener())
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<MyTest> fileItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<MyTest>()
                .name("fileItemReader")
                .resource(new FileSystemResource(filePath))
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public JobCompletionNotificationListener listener() {
        return new JobCompletionNotificationListener();
    }

    @Bean
    public JobLauncher simpleJobLauncher(JobRepository jobRepository) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }
}
