package com.dockermonitor.service;

import com.dockermonitor.repository.MonitoredApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ApplicationMonitor {

    private final MonitoredApplicationRepository repository;
    private final ApplicationService applicationService;
    private final WebClient webClient;

    @Autowired
    public ApplicationMonitor(MonitoredApplicationRepository repository,
                              ApplicationService applicationService) {
        this.repository = repository;
        this.applicationService = applicationService;
        this.webClient = WebClient.create();
    }

    @Scheduled(cron = "0/30 * * * * *")
    void checkApplicationStatus() {
        final var applications = repository.findAll();
        applications.stream().forEach(app -> {
            final var currentStatus = app.getActive();
            final var isActive = checkStatus(app.getUrl());
            app.setActive(isActive);
            repository.save(app);

            if (currentStatus != isActive) {
                applicationService.updateApplicationStatus(app);
            }
        });
    }

    boolean checkStatus(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> Mono.just(response.getStatusCode().is2xxSuccessful()))
                .onErrorResume(ex -> Mono.just(false))
                .blockOptional()
                .orElse(false);
    }

}