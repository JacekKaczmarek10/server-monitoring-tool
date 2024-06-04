package com.dockermonitor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dockermonitor.dto.MonitoredApplicationDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class ImportMonitoredApplicationServiceTest {

    @Mock
    private MonitoredApplicationService monitoredApplicationService;

    @InjectMocks
    private ImportMonitoredApplicationService importMonitoredApplicationService;

    @Nested
    class ImportApplicationsFromCSVTest {

        @Test
        void shouldImportApplicationsSuccessfully() throws IOException {
            final var csvData = "name,url\nFacebook,https://facebook.com\nGoogle,https://google.com";
            final var inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
            final var file = new MockMultipartFile("test.csv", inputStream);

            importMonitoredApplicationService.importApplicationsFromCSV(file);

            verify(monitoredApplicationService, times(2)).create(any(MonitoredApplicationDto.class));
        }

        @Test
        void shouldThrowIOException() throws IOException {
            final var file = mock(MultipartFile.class);
            when(file.getInputStream()).thenThrow(new IOException());

            assertThrows(IOException.class, () -> importMonitoredApplicationService.importApplicationsFromCSV(file));
        }
    }

    @Nested
    class ProcessCSVLineTest {

        @Test
        void shouldCreateDtoAndCallService() {
            final var line = "Facebook,https://facebook.com";
            final var expectedDto = new MonitoredApplicationDto("Facebook", "https://facebook.com");

            importMonitoredApplicationService.processCSVLine(line);

            verify(monitoredApplicationService).create(expectedDto);
        }

        @Test
        void shouldHandleInvalidCSVLine() {
            final var invalidLine = "Invalid Data";

            final var exception = assertThrows(IllegalArgumentException.class, () -> importMonitoredApplicationService.processCSVLine(invalidLine));
            assertEquals("Invalid CSV line format: Invalid Data", exception.getMessage());
        }
    }

    @Nested
    class IsValidCSVLineTest {

        @Test
        void shouldReturnTrueForValidCSVLine() {
            String[] validData = {"Facebook", "https://facebook.com"};

            boolean isValid = importMonitoredApplicationService.isValidCSVLine(validData);

            assertThat(isValid).isTrue();
        }

        @Test
        void shouldReturnFalseForInvalidCSVLine() {
            String[] invalidData = {"Facebook"};

            final var isValid = importMonitoredApplicationService.isValidCSVLine(invalidData);

            assertThat(isValid).isFalse();
        }
    }

    @Nested
    class CreateMonitoredApplicationDtoTest {

        @Test
        void shouldCreateDtoCorrectly() {
            String[] data = {"Facebook", "https://facebook.com"};
            final var expectedDto = new MonitoredApplicationDto("Facebook", "https://facebook.com");

            final var dto = importMonitoredApplicationService.createMonitoredApplicationDto(data);

            assertThat(dto).isEqualTo(expectedDto);
        }
    }
}
