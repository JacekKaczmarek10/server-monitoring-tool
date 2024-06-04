package com.dockermonitor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dockermonitor.dto.MonitoredApplicationDto;
import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.exception.ApplicationNotFoundException;
import com.dockermonitor.repository.MonitoredApplicationRepository;
import com.dockermonitor.validator.UrlValidator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MonitoredApplicationServiceTest {

    @InjectMocks
    private MonitoredApplicationService service;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private MonitoredApplicationRepository repository;

    @Nested
    class FindAllTest {

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void shouldCallRepository() {
            callService();

            verify(repository).findAll();
        }

        @Test
        void shouldReturnMonitoredApplications() {
            when(repository.findAll()).thenReturn(List.of(new MonitoredApplication()));

            final var monitoredApplicationList = callService();

            assertThat(monitoredApplicationList).isNotEmpty();
        }

        @Test
        void shouldReturnEmptyList() {
            final var monitoredApplicationList = callService();

            assertThat(monitoredApplicationList).isEmpty();
        }

        private List<MonitoredApplication> callService() {
            return service.findAll();
        }

    }

    @Nested
    class GetByIdTest {

        @Test
        void shouldFindMonitoredApplication() {
            when(repository.findById(1L)).thenReturn(Optional.of(new MonitoredApplication()));

            final var monitoredApplication = callService();

            assertThat(monitoredApplication).isNotNull();
        }

        @Test
        void shouldThrowException() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> callService()).isInstanceOf(ApplicationNotFoundException.class);
        }

        private MonitoredApplication callService() {
            return service.getById(1L);
        }

    }

    @Nested
    class GetStatusByIdTest {

        private final MonitoredApplication monitoredApplication = new MonitoredApplication();

        @Test
        void shouldReturnTrue() {
            monitoredApplication.setActive(true);
            when(repository.findById(1L)).thenReturn(Optional.of(monitoredApplication));

            final var status = callService();

            assertThat(status).isTrue();
        }

        @Test
        void shouldReturnFalse() {
            monitoredApplication.setActive(false);
            when(repository.findById(1L)).thenReturn(Optional.of(monitoredApplication));

            final var status = callService();

            assertThat(status).isFalse();
        }

        @Test
        void shouldThrowException() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> callService()).isInstanceOf(ApplicationNotFoundException.class);
        }

        private boolean callService() {
            return service.getStatusById(1L);
        }

    }

    @Nested
    class CreateTest {

        private final String name = "Facebook";
        private final String url = "https://www.facebook.com/";
        private final MonitoredApplicationDto monitoredApplicationDto = new MonitoredApplicationDto(name, url);

        @Test
        void shouldValidateUrl() {
            callService();

            verify(urlValidator).validateUrl(url);
        }

        @Test
        void shouldSave() {
            callService();

            verify(repository).save(any());
        }

        @Test
        void shouldSetName() {
            final var monitoredApplication = callService();

            assertThat(monitoredApplication.getName()).isEqualTo(name);
        }

        @Test
        void shouldSetUrl() {
            final var monitoredApplication = callService();

            assertThat(monitoredApplication.getUrl()).isEqualTo(url);
        }

        @Test
        void shouldReturnApplication() {
            final var monitoredApplication = callService();

            assertThat(monitoredApplication).isNotNull();
        }

        private MonitoredApplication callService() {
            return service.create(monitoredApplicationDto);
        }

    }

    @Nested
    class UpdateTest {

        private final long id = 1L;
        private final String name = "Facebook";
        private final String url = "https://www.facebook.com/";
        private final MonitoredApplicationDto monitoredApplicationDto = new MonitoredApplicationDto(name, url);
        private final MonitoredApplication monitoredApplication = new MonitoredApplication();

        @Test
        void shouldValidateUrl() {
            when(repository.findById(id)).thenReturn(Optional.of(monitoredApplication));

            callService();

            verify(urlValidator).validateUrl(url);
        }

        @Test
        void shouldFindById() {
            when(repository.findById(id)).thenReturn(Optional.of(monitoredApplication));

            callService();

            verify(repository).findById(id);
        }

        @Test
        void shouldThrowApplicationNotFoundException() {
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> callService()).isInstanceOf(ApplicationNotFoundException.class);
        }

        @Test
        void shouldUpdateName() {
            when(repository.findById(id)).thenReturn(Optional.of(monitoredApplication));

            callService();

            final var monitoredApplication = repository.findById(id);
            assertThat(monitoredApplication.get().getName()).isEqualTo(name);
        }

        @Test
        void shouldUpdateUrl() {
            when(repository.findById(id)).thenReturn(Optional.of(monitoredApplication));

            callService();

            final var monitoredApplication = repository.findById(id);
            assertThat(monitoredApplication.get().getUrl()).isEqualTo(url);
        }

        @Test
        void shouldSave() {
            when(repository.findById(id)).thenReturn(Optional.of(monitoredApplication));

            callService();

            verify(repository).save(any());
        }

        private void callService() {
            service.update(id, monitoredApplicationDto);
        }

    }

    @Nested
    class ValidateUrlTest {

    }

    @Nested
    class DeleteByIdTest {

        private final long id = 1L;

        @Test
        void shouldDeleteById() {
            callService();

            verify(repository).deleteById(id);
        }

        @Test
        void shouldThrowEmptyResultDataAccessException() {
            doThrow(ApplicationNotFoundException.class).when(repository).deleteById(id);

            assertThatThrownBy(() -> callService()).isInstanceOf(ApplicationNotFoundException.class);
        }

        private void callService() {
            service.deleteById(id);
        }
    }

}
