package com.filereader.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filereader.app.entity.MyTest;
import com.filereader.app.processor.CustomLineMapper;
import com.filereader.app.processor.CustomSkipPolicy;
import com.filereader.app.processor.CustomStepListener;
import com.filereader.app.processor.DataTransformer;
import com.filereader.app.processor.JobCompletionNotificationListener;
import com.filereader.app.processor.S3Resource;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.services.s3.S3Client;

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

    private final ApplicationContext context;

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
     * The fileItemReader method is used to create the FlatFileItemReader object.
     * @param filePath - The input file path.
     * @return {@link FlatFileItemReader} object.
     */
    @Profile("filesystem")
    @StepScope
    @Bean
    public FlatFileItemReader<MyTest> fileItemReaderFile(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<MyTest>()
                .name("fileItemReader")
                .resource(new FileSystemResource(filePath))
                .lineMapper(lineMapper)
                .build();
    }

    /**
     * The fileItemReader method is used to create the FlatFileItemReader object.
     * @param s3Client - The {@link S3Client} object.
     * @param bucketName - The bucket name.
     * @param fileName - The input file path.
     * @return {@link FlatFileItemReader} object.
     */
    @Profile("aws")
    @StepScope
    @Bean
    public FlatFileItemReader<MyTest> fileItemReaderS3(S3Client s3Client, @Value("#{jobParameters['bucketName']}") String bucketName,
                                                       @Value("#{jobParameters['fileName']}") String fileName) {
        return new FlatFileItemReaderBuilder<MyTest>()
                .name("fileItemReaderS3")
                .resource(new S3Resource(s3Client, bucketName, fileName))
                .lineMapper(lineMapper)
                .build();
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
        FlatFileItemReader<MyTest> fileItemReader = context.getBean(FlatFileItemReader.class);
        return new StepBuilder("fileProcessingStep", jobRepository)
                .<MyTest, MyTest>chunk(10, transactionManager)
                .reader(fileItemReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipPolicy(new CustomSkipPolicy())
                .listener(new CustomStepListener())
                .build();
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

    /**
     * The objectMapper method is used to create the ObjectMapper object.
     * @return {@link ObjectMapper} object.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
