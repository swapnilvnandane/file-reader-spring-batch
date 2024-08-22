package com.filereader.app.service;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * DirectoryWatcherService class is used to watch the directory for new files and trigger the job.
 */
@Service
public class DirectoryWatcherService implements Runnable {

    // The WatchService class is used to watch the directory for new files.
    private final WatchService watchService;

    // The JobLauncher class is used to launch the job.
    private final JobLauncher jobLauncher;

    // The Job class is used to define the job.
    private final Job job;

    // The inputDir is used to store the input directory path.
    private final Path inputDir;

    /**
     * The DirectoryWatcherService constructor is used to initialize the DirectoryWatcherService object.
     * @param inputDirectory a {@link String} object.
     * @param jobLauncher a {@link JobLauncher} object.
     * @param job a {@link Job} object.
     * @throws IOException an {@link IOException} object.
     */
    @Autowired
    public DirectoryWatcherService(@Value("${com.file.location}") String inputDirectory,
                                   JobLauncher jobLauncher, Job job) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.inputDir = Paths.get(inputDirectory);
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.inputDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    /**
     * The startWatching method is used to start watching the directory for new files with help of thread.
     */
    @PostConstruct
    public void startWatching() {
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * The run method is used to watch the directory for new files and trigger the job.
     */
    @Override
    public void run() {
        WatchKey key;
        while (true) {
            try {
                key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path filePath = inputDir.resolve((Path) event.context());
                        System.out.println("New file detected: " + filePath);
                        triggerJob(filePath.toString(), filePath.getFileName().toString());
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Handle exceptions
                System.err.println("Error in directory watching: " + e.getMessage());
            }
        }
    }

    /**
     * The triggerJob method is used to trigger the job.
     * @param filePath a {@link String} object.
     * @param fileName a {@link String} object.
     */
    private void triggerJob(String filePath, String fileName) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileName", fileName)
                    .addString("filePath", filePath)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            // Exception handling
            System.err.println("Failed to start the job: " + e.getMessage());
        }
    }
}
