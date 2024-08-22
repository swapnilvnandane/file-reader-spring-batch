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
/**
 * BatchConfig class is used to configure the Spring Batch job and step.
 */
@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    /** The CustomLineMapper class is used to map the line from the input file to the MyTest object. **/
    private final CustomLineMapper lineMapper;

    /** The DataTransformer class is used to write the data to database table. **/
    private final DataTransformer writer;

    /** The ValidatingItemProcessor class is used to validate the data. **/
    private final ValidatingItemProcessor processor;

    /**
     * The jobRepository method is used to create the InMemoryJobRepository object.
     * @return {@link JobRepository} of {@link InMemoryJobRepository} object.
     */
    @Bean
    @Primary
    public JobRepository jobRepository() {
        return new InMemoryJobRepository();
    }

    /**
     * The transactionManager method is used to create the ResourcelessTransactionManager object.
     * @return {@link ResourcelessTransactionManager} object.
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    /**
     * The job method is used to create the Job object.
     * @param jobRepository - The {@link JobRepository} object.
     * @param listener - The {@link JobCompletionNotificationListener} object.
     * @param step - The {@link Step} object.
     * @return {@link Job} object.
     */
    @Bean
    public Job job(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step) {
        return new JobBuilder("fileProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }

    /**
     * The step method is used to create the Step object.
     * @param jobRepository - The {@link JobRepository} object.
     * @param transactionManager - The {@link PlatformTransactionManager} object.
     * @return {@link Step} object.
     */
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

    /**
     * The fileItemReader method is used to create the FlatFileItemReader object.
     * @param filePath - The input file path.
     * @return {@link FlatFileItemReader} object.
     */
    @StepScope
    @Bean
    public FlatFileItemReader<MyTest> fileItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<MyTest>()
                .name("fileItemReader")
                .resource(new FileSystemResource(filePath))
                .lineMapper(lineMapper)
                .build();
    }

    /**
     * The listener method is used to create the JobCompletionNotificationListener object.
     * @return {@link JobCompletionNotificationListener} object.
     */
    @Bean
    public JobCompletionNotificationListener listener() {
        return new JobCompletionNotificationListener();
    }

    /**
     * The simpleJobLauncher method is used to create the TaskExecutorJobLauncher object.
     * @param jobRepository - The {@link JobRepository} object.
     * @return {@link JobLauncher} of {@link TaskExecutorJobLauncher} object.
     */
    @Bean
    public JobLauncher simpleJobLauncher(JobRepository jobRepository) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }
}
