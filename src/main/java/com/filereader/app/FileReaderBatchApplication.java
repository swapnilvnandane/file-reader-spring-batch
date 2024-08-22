package com.filereader.app;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * FileReaderBatchApplication class is used to start the Spring Boot application.
 */
@SpringBootApplication
@EnableEncryptableProperties
@EnableAsync
@EnableScheduling
public class FileReaderBatchApplication {

    /**
     * The main method is used to start the Spring Boot application.
     * @param args a String[] object.
     */
    public static void main(String[] args) {
        SpringApplication.run(FileReaderBatchApplication.class, args);
    }
}
