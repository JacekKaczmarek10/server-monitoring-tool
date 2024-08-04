package com.dockermonitor.controller.pages;

import com.dockermonitor.repository.MonitoredApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
class HomeController {

    private final MonitoredApplicationRepository repository;

    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("apps", repository.findAll());
        return "index";
    }

}
