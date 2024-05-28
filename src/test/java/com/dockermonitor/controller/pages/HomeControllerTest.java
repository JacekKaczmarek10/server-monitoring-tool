package com.dockermonitor.controller.pages;

import com.dockermonitor.repository.MonitoredApplicationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private MonitoredApplicationRepository repository;

    @InjectMocks
    private HomeController homeController;

    @Nested
    class IndexTest {

        @Test
        void shouldReturnIndexPage() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            final var viewName = homeController.index(mock(Model.class));

            assertThat(viewName).isEqualTo("index");
        }

        @Test
        void index_shouldAddAppsToModel() {
            final var model = mock(Model.class);

            homeController.index(model);

            verify(model).addAttribute("apps", Collections.emptyList());
        }

    }


}