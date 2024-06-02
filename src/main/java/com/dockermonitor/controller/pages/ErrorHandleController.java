package com.dockermonitor.controller.pages;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@ControllerAdvice
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
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