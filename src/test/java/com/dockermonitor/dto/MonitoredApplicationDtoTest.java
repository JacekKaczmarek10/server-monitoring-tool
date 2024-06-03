package com.dockermonitor.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class MonitoredApplicationDtoTest {

    @Test
    public void testValidMonitoredApplicationDto() {
        final var dto = new MonitoredApplicationDto("AppName", "http://example.com");

        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo("AppName");
        assertThat(dto.url()).isEqualTo("http://example.com");
    }

    @Test
    public void testInvalidMonitoredApplicationDtoWithNullName() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            new MonitoredApplicationDto(null, "http://example.com");
        });

        assertThat(exception.getMessage()).contains("name is marked non-null but is null");
    }

    @Test
    public void testInvalidMonitoredApplicationDtoWithNullUrl() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            new MonitoredApplicationDto("AppName", null);
        });

        assertThat(exception.getMessage()).contains("url is marked non-null but is null");
    }

    @Test
    public void testInvalidMonitoredApplicationDtoWithNullValues() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            new MonitoredApplicationDto(null, null);
        });

        assertThat(exception.getMessage()).contains("is marked non-null but is null");
    }
}