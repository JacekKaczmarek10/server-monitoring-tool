package com.dockermonitor.controller.pages;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.ui.Model;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@WebMvcTest(ErrorHandleController.class)
class ErrorHandleControllerTest {

    @Autowired
    private ErrorHandleController errorHandleController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @Nested
    class HandleErrorTest {

        @Test
        void shouldReturnErrorView() {
            when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);

            final var viewName = errorHandleController.handleError(request, model);

            verify(model).addAttribute("errorType", "Not Found");
            verify(model).addAttribute("errorStatus", 404);
            verify(model).addAttribute(eq("timestamp"), any(Timestamp.class));
            assertThat(viewName).isEqualTo("error");
        }

        @Test
        void shouldReturnStatusCode() {
            when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(400);

            final var statusCode = errorHandleController.getErrorStatus(request);

            assertThat(statusCode).isEqualTo(400);
        }

    }

    @Nested
    class GetErrorStatusTest {

        @Test
        void shouldReturnStatusCodeWhenNotNull() {
            when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);

            final var statusCode = errorHandleController.getErrorStatus(request);

            assertThat(statusCode).isEqualTo(404);
        }

        @Test
        void shouldReturnZeroWhenStatusCodeIsNull() {
            when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

            final var statusCode = errorHandleController.getErrorStatus(request);

            assertThat(statusCode).isEqualTo(0);
        }

    }

    @Nested
    class GetErrorTypeMessageTest {

        @Test
        void shouldReturnCorrectErrorMessageFor400() {
            assertThat(errorHandleController.getErrorType(400)).isEqualTo("Bad Request");
        }

        @Test
        void shouldReturnCorrectErrorMessageFor401() {
            assertThat(errorHandleController.getErrorType(401)).isEqualTo("Unauthorized");
        }

        @Test
        void shouldReturnCorrectErrorMessageFor403() {
            assertThat(errorHandleController.getErrorType(403)).isEqualTo("Forbidden");
        }

        @Test
        void shouldReturnCorrectErrorMessageFor404() {
            assertThat(errorHandleController.getErrorType(404)).isEqualTo("Not Found");
        }

        @Test
        void shouldReturnCorrectErrorMessageFor500() {
            assertThat(errorHandleController.getErrorType(500)).isEqualTo("Internal Server Error");
        }

        @Test
        void shouldReturnCorrectErrorMessageFor503() {
            assertThat(errorHandleController.getErrorType(503)).isEqualTo("Service Unavailable");
        }

        @Test
        void shouldReturnCorrectErrorMessageFor999() {
            assertThat(errorHandleController.getErrorType(999)).isEqualTo("Unknown");
        }

    }

}
