package com.dockermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DockerMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerMonitorApplication.class, args);
    }

}
