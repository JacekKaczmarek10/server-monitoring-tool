package com.dockermonitor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.dockermonitor.entity.MonitoredApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class ApplicationServiceTest {

    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class UpdateApplicationStatusTest {

        @Test
        void shouldUpdateApplicationStatus() {
            final var application = new MonitoredApplication();
            application.setName("TestApp");
            application.setUrl("https://example.com");
            application.setActive(true);

            applicationService.updateApplicationStatus(application);

            verify(messagingTemplate, times(1)).convertAndSend("/topic/applicationStatus", application);
        }

    }
}