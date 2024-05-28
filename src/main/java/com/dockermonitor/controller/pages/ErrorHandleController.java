package com.dockermonitor.controller.pages;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;

@Controller
public class ErrorHandleController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        final var errorStatus = getErrorStatus(request);
        final var errorType = getErrorType(errorStatus);
        final var timestamp = new Timestamp(System.currentTimeMillis());

        model.addAttribute("errorType", errorType);
        model.addAttribute("errorStatus", errorStatus);
        model.addAttribute("timestamp", timestamp);

        return "error";
    }

    int getErrorStatus(HttpServletRequest request) {
        final var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return status != null ? Integer.parseInt(status.toString()) : 0;
    }

    String getErrorType(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            case 503 -> "Service Unavailable";
            default -> "Unknown";
        };
    }

}