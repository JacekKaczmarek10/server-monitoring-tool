package com.dockermonitor.service;

import com.dockermonitor.dto.MonitoredApplicationDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImportMonitoredApplicationService {

    private final MonitoredApplicationService monitoredApplicationService;

    public void importApplicationsFromCSV(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                processCSVLine(line);
            }
        }
    }

    void processCSVLine(String line) {
        final var data = line.split(",");
        if (isValidCSVLine(data)) {
            MonitoredApplicationDto applicationDto = createMonitoredApplicationDto(data);
            monitoredApplicationService.create(applicationDto);
        } else {
            handleInvalidCSVLine(line);
        }
    }

    boolean isValidCSVLine(String[] data) {
        return data.length == 2;
    }

    MonitoredApplicationDto createMonitoredApplicationDto(String[] data) {
        final var name = data[0].trim();
        final var url = data[1].trim();
        return new MonitoredApplicationDto(name, url);
    }

    void handleInvalidCSVLine(String line) {
        throw new IllegalArgumentException("Invalid CSV line format: " + line);
    }
}
