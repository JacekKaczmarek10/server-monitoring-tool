package com.dockermonitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootTest
@EnableScheduling
class DockerMonitorApplicationTest {

    @Test
    void contextLoads() {
        final var applicationClass = DockerMonitorApplication.class;

        final var context = SpringApplication.run(applicationClass);

        assertThat(context).isNotNull();
    }

}