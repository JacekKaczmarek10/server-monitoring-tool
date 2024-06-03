package com.dockermonitor.service;

import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.repository.MonitoredApplicationRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ApplicationMonitorTest {

    @InjectMocks
    @Spy
    private ApplicationMonitor applicationMonitor;

    @Mock
    private MonitoredApplicationRepository repository;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private WebClient.Builder webClientBuilder;

    private MonitoredApplication app1;

    @BeforeEach
    public void setUp() {
        app1 = new MonitoredApplication();
        app1.setName("App1");
        app1.setUrl("https://app1.com");
        app1.setActive(true);

        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CheckApplicationStatusTest {

        @Test
        void shouldFindAll() {
            callService();

            verify(repository).findAll();
        }

        @Test
        void shouldCheckStatusForEachApplication() {
            when(repository.findAll()).thenReturn(List.of(app1));

            callService();

            verify(applicationMonitor).checkStatus(app1.getUrl());
        }

        @Test
        void shouldNotCheckStatusForEachApplication() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            callService();

            verify(applicationMonitor, never()).checkStatus(app1.getUrl());
        }

        @Test
        void shouldSave() {
            when(repository.findAll()).thenReturn(List.of(app1));

            callService();

            verify(repository).save(app1);
        }

        @Test
        void shouldNotSave() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            callService();

            verify(repository, never()).save(app1);
        }

        @Test
        void shouldUpdateApplicationStatus() {
            when(repository.findAll()).thenReturn(List.of(app1));
            when(applicationMonitor.checkStatus(app1.getUrl())).thenReturn(false);

            callService();

            verify(applicationService).updateApplicationStatus(app1);
        }

        @Test
        void shouldNotUpdateApplicationStatus() {
            when(repository.findAll()).thenReturn(List.of(app1));
            when(applicationMonitor.checkStatus(app1.getUrl())).thenReturn(true);

            callService();

            verify(applicationService, never()).updateApplicationStatus(app1);
        }

        private void callService() {
            applicationMonitor.checkApplicationStatus();
        }
    }

    @Nested
    class CheckStatusTest {

        @Test
        void shouldCheckStatusForValidUrl() {
            final var url = "https://example.com";
            when(webClientBuilder.baseUrl(url)).thenReturn(webClientBuilder);
            final var webClient = WebClient.builder().baseUrl(url).build();
            when(webClientBuilder.build()).thenReturn(webClient);

            final var result = applicationMonitor.checkStatus(url);

            assertThat(result).isTrue();
        }

        @Test
        void shouldCheckStatusForInvalidUrl() {
            final var invalidUrl = "invalid-url";

            final var result = applicationMonitor.checkStatus(invalidUrl);

            assertThat(result).isFalse();
        }
    }
}
