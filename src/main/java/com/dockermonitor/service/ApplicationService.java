package com.dockermonitor.service;

import com.dockermonitor.entity.MonitoredApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void updateApplicationStatus(MonitoredApplication application) {
        messagingTemplate.convertAndSend("/topic/applicationStatus", application);
    }

}