package com.dockermonitor.controller.websocket;

import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ApplicationService applicationService;

    @MessageMapping("/updateStatus")
    @SendTo("/topic/applicationStatus")
    public MonitoredApplication updateStatus(MonitoredApplication application) {
        applicationService.updateApplicationStatus(application);
        return application;
    }

}