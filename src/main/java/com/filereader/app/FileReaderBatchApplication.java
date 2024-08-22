package com.filereader.app;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEncryptableProperties
@EnableAsync
@EnableScheduling
public class FileReaderBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileReaderBatchApplication.class, args);
    }
}
