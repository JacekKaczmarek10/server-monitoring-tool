package com.dockermonitor.controller.websocket;

import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WebSocketControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private WebSocketController webSocketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class UpdateStatusTest {

        @Test
        public void shouldUpdateStatus() {
            final var application = new MonitoredApplication();
            application.setId(1L);
            application.setName("Test Application");
            application.setActive(true);
            doNothing().when(applicationService).updateApplicationStatus(application);

            final var result = webSocketController.updateStatus(application);

            verify(applicationService, times(1)).updateApplicationStatus(application);
            assertThat(application).isEqualTo(result);
        }

    }

}