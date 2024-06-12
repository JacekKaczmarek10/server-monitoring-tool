package com.dockermonitor.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

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

            final var result = callService(url);

            assertThat(result).isTrue();
        }

        @Test
        void shouldCheckStatusForInvalidUrl() {
            final var invalidUrl = "invalid-url";

            final var result = callService(invalidUrl);

            assertThat(result).isFalse();
        }

        private boolean callService(final String url) {
            return applicationMonitor.checkStatus(url);
        }

    }

    @Nested
    class HandleResponseTests {

        @Test
        void shouldCallLogResponseStatus() {
            final var clientResponse = mock(ClientResponse.class);
            when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);

            applicationMonitor.handleResponse(clientResponse);

            verify(applicationMonitor).logResponseStatus(clientResponse);
        }

        @Test
        void shouldHandleNon2xxResponse() {
            final var clientResponse = mock(ClientResponse.class);
            when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

            final var result = applicationMonitor.handleResponse(clientResponse);

            StepVerifier.create(result).expectNext(false).verifyComplete();
        }

        @Test
        void shouldReturn2xxSuccessful() {
            final var clientResponse = mock(ClientResponse.class);
            when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);

            final var result = applicationMonitor.handleResponse(clientResponse);

            StepVerifier.create(result).expectNext(true).verifyComplete();
        }

    }

    @Nested
    class LogResponseStatusTests {

        private ListAppender<ILoggingEvent> listAppender;

        @BeforeEach
        void setUp() {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            listAppender = new ListAppender<>();
            listAppender.start();
            context.getLogger(ApplicationMonitor.class).addAppender(listAppender);
        }

        @Test
        void shouldLogResponseStatus() {
            final var response = mock(ClientResponse.class);
            when(response.statusCode()).thenReturn(HttpStatusCode.valueOf(200));

            applicationMonitor.logResponseStatus(response);

            final var logsList = listAppender.list;
            assertThat(logsList.size()).isEqualTo(1);
            assertThat(logsList.getFirst().getMessage()).isEqualTo("Response status code: {}");
            assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.INFO);
        }
    }

}
