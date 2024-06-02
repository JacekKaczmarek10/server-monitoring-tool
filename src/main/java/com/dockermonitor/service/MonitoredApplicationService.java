package com.dockermonitor.service;

import com.dockermonitor.dto.MonitoredAppDto;
import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.exception.ApplicationNotFoundException;
import com.dockermonitor.repository.MonitoredApplicationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MonitoredApplicationService {

    private final MonitoredApplicationRepository repository;

    public List<MonitoredApplication> findAll() {
        return repository.findAll();
    }

    public MonitoredApplication getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    public boolean getStatusById(Long id) {
        final var application = repository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
        return application.isActive();
    }

    public MonitoredApplication create(MonitoredAppDto applicationDto) {
        final var application = new MonitoredApplication();
        application.setName(applicationDto.name());
        application.setUrl(applicationDto.url());
        repository.save(application);
        return application;
    }

    public void update(Long id, MonitoredAppDto applicationDto) {
        final var application = repository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
        application.setName(applicationDto.name());
        application.setUrl(applicationDto.url());
        repository.save(application);
    }

    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new ApplicationNotFoundException(id);
        }
    }

}