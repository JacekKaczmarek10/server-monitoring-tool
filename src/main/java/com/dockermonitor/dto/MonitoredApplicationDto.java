package com.dockermonitor.dto;

import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

public record MonitoredApplicationDto(@Validated @NonNull String name, @Validated @NonNull String url) {
}
