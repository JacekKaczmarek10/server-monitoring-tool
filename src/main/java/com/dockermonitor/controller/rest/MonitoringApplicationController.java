package com.dockermonitor.controller.rest;

import com.dockermonitor.dto.MonitoredAppDto;
import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.repository.MonitoredApplicationRepository;
import com.dockermonitor.service.MonitoredApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class MonitoringApplicationController {

    private final MonitoredApplicationService monitoredApplicationService;
    private final MonitoredApplicationRepository repository;

    @GetMapping("")
    public ResponseEntity<List<MonitoredApplication>> getApplications() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoredApplication> getApplication(@PathVariable Long id) {
        return ResponseEntity.ok(monitoredApplicationService.getById(id));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> getApplicationStatus(@PathVariable Long id) {
        return ResponseEntity.ok(monitoredApplicationService.getStatusById(id));
    }

    @PostMapping("")
    public ResponseEntity<MonitoredApplication> createApplication(@RequestBody MonitoredAppDto applicationDto) {
        return ResponseEntity.ok(monitoredApplicationService.create(applicationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApplication(@PathVariable Long id, @RequestBody MonitoredAppDto applicationDto) {
        monitoredApplicationService.update(id, applicationDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        monitoredApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}