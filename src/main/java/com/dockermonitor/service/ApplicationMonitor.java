package com.dockermonitor.service;

import com.dockermonitor.repository.MonitoredApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ApplicationMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMonitor.class);

    private final MonitoredApplicationRepository repository;
    private final ApplicationService applicationService;
    private final WebClient webClient;

    @Autowired
    public ApplicationMonitor(MonitoredApplicationRepository repository, ApplicationService applicationService) {
        this.repository = repository;
        this.applicationService = applicationService;
        this.webClient = WebClient.create();
    }

    @Scheduled(cron = "0/30 * * * * *")
    void checkApplicationStatus() {
        final var applications = repository.findAll();
        applications.forEach(app -> {
            final var currentStatus = app.isActive();
            final var isActive = checkStatus(app.getUrl());
            app.setActive(isActive);
            repository.save(app);

            if (currentStatus != isActive) {
                applicationService.updateApplicationStatus(app);
            }
        });
    }

    public boolean checkStatus(final String url) {
        return webClient.get().uri(url).exchangeToMono(this::handleResponse).onErrorResume(ex -> {
            logger.error("Error occurred while checking URL: {}", url, ex);
            return Mono.just(false);
        }).blockOptional().orElse(false);
    }

    Mono<Boolean> handleResponse(ClientResponse response) {
        logResponseStatus(response);
        if (response.statusCode().is3xxRedirection()) {
            final var redirectUrl = response.headers().asHttpHeaders().getLocation().toString();
            logger.info("Redirecting to: {}", redirectUrl);
            return webClient.get().uri(redirectUrl).exchangeToMono(this::handleResponse);
        } else {
            return Mono.just(response.statusCode().is2xxSuccessful());
        }
    }

    void logResponseStatus(ClientResponse response) {
        logger.info("Response status code: {}", response.statusCode().value());
    }


}