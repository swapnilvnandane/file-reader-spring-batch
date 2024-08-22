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

@Service
public class DirectoryWatcherService implements Runnable {

    private final WatchService watchService;
    private final JobLauncher jobLauncher;
    private final Job job;
    private final Path inputDir;

    @Autowired
    public DirectoryWatcherService(@Value("${com.file.location}") String inputDirectory,
                                   JobLauncher jobLauncher, Job job) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.inputDir = Paths.get(inputDirectory);
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.inputDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @PostConstruct
    public void startWatching() {
        Thread thread = new Thread(this);
        thread.start();
    }

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
