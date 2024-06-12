package com.dockermonitor.controller.rest;

import com.dockermonitor.dto.ExceptionMessageDto;
import com.dockermonitor.dto.MonitoredApplicationDto;
import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.exception.ApplicationNotFoundException;
import com.dockermonitor.service.ApplicationMonitor;
import com.dockermonitor.service.MonitoredApplicationService;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Validated
public class MonitoringApplicationController {

    private final MonitoredApplicationService monitoredApplicationService;
    private final ApplicationMonitor applicationMonitor;

    @GetMapping("")
    public ResponseEntity<List<MonitoredApplication>> getApplications() {
        return ResponseEntity.ok(monitoredApplicationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoredApplication> getApplication(@PathVariable Long id) {
        return ResponseEntity.ok(monitoredApplicationService.getById(id));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> getApplicationStatus(@PathVariable Long id) {
        return ResponseEntity.ok(monitoredApplicationService.getStatusById(id));
    }

    @PostMapping("/status")
    public ResponseEntity<Boolean> getApplicationStatusByUrl(@RequestParam @Validated @NonNull String url) {
        return ResponseEntity.ok(applicationMonitor.checkStatus(url));
    }

    @PostMapping("")
    public ResponseEntity<MonitoredApplication> createApplication(@RequestBody @Validated @NonNull MonitoredApplicationDto applicationDto) {
        return ResponseEntity.ok(monitoredApplicationService.create(applicationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApplication(@PathVariable Long id, @RequestBody MonitoredApplicationDto applicationDto) {
        monitoredApplicationService.update(id, applicationDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        monitoredApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({ApplicationNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionMessageDto> handleApplicationNotFound(ApplicationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionMessageDto(ex.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionMessageDto> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionMessageDto(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final var errorMessage = ex.getBindingResult().getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }

}