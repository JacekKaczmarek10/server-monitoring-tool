package com.dockermonitor.controller.rest;

import com.dockermonitor.service.ApplicationMonitor;
import com.dockermonitor.service.ImportMonitoredApplicationService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Validated
public class ImportMonitoringApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ImportMonitoringApplicationController.class);

    private final ImportMonitoredApplicationService importApplicationsFromCSV;

    @PostMapping("/import")
    public ResponseEntity<String> importApplications(@RequestParam("file") MultipartFile file) {
        try {
            importApplicationsFromCSV.importApplicationsFromCSV(file);
            return ResponseEntity.ok("CSV file imported successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import CSV file: " + e.getMessage());
        } catch (DataIntegrityViolationException ex) {
            logger.error("Exception occurred", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DataIntegrityViolationException occurred");
        }
    }

}
