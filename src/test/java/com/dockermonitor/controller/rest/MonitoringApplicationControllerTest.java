package com.dockermonitor.controller.rest;

import com.dockermonitor.dto.MonitoredApplicationDto;
import com.dockermonitor.entity.MonitoredApplication;
import com.dockermonitor.exception.ApplicationNotFoundException;
import com.dockermonitor.service.MonitoredApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MonitoringApplicationController.class)
class MonitoringApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonitoredApplicationService service;

    @Autowired
    private ObjectMapper objectMapper;

    private MonitoredApplication application;
    private MonitoredApplicationDto applicationDto;
    private Long monitoredApplicationId = 1L;

    @BeforeEach
    void setUp() {
        application = new MonitoredApplication();
        application.setId(monitoredApplicationId);
        application.setName("Codepred");

        applicationDto = new MonitoredApplicationDto("Codepred", "https://codepred.pl/");
    }

    @Nested
    class GetApplicationsTest {

        @Test
        void shouldCallService() throws Exception {
            when(service.findAll()).thenReturn(Collections.singletonList(application));

            doRequest().andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value(application.getName()));

            verify(service).findAll();
        }

        @Test
        void shouldReturnApplications() throws Exception {
            when(service.findAll()).thenReturn(Collections.singletonList(application));

            doRequest().andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value(application.getName()));
        }

        @Test
        void shouldReturnEmptyList() throws Exception {
            when(service.findAll()).thenReturn(Collections.emptyList());

            doRequest().andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest().andExpect(status().isOk());
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(get("/api/applications"));
        }

    }

    @Nested
    class GetApplicationTest {

        @Test
        void shouldCallService() throws Exception {
            when(service.getById(anyLong())).thenReturn(application);

            doRequest().andExpect(jsonPath("$.name").value(application.getName()));

            verify(service).getById(monitoredApplicationId);
        }

        @Test
        void shouldReturnApplication() throws Exception {
            when(service.getById(anyLong())).thenReturn(application);

            doRequest().andExpect(jsonPath("$.name").value(application.getName()));
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            when(service.getById(anyLong())).thenThrow(ApplicationNotFoundException.class);

            doRequest().andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest().andExpect(status().isOk());
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(get("/api/applications/{id}", monitoredApplicationId));
        }
    }

    @Nested
    class GetApplicationStatusTest {

        @Test
        void shouldCallService() throws Exception {
            when(service.getStatusById(monitoredApplicationId)).thenReturn(true);

            doRequest().andExpect(status().isOk()).andExpect(jsonPath("$").value(true));

            verify(service).getStatusById(monitoredApplicationId);
        }

        @Test
        void shouldReturnApplicationStatus() throws Exception {
            when(service.getStatusById(monitoredApplicationId)).thenReturn(true);

            doRequest().andExpect(status().isOk()).andExpect(jsonPath("$").value(true));
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            when(service.getStatusById(monitoredApplicationId)).thenThrow(ApplicationNotFoundException.class);

            doRequest().andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest().andExpect(status().isOk());
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(get("/api/applications/{id}/status", monitoredApplicationId));
        }
    }

    @Nested
    class CreateApplicationTest {

        @Test
        void shouldCallService() throws Exception {
            when(service.create(applicationDto)).thenReturn(application);

            doRequest(objectMapper.writeValueAsString(applicationDto)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(application.getName()));

            verify(service).create(applicationDto);
        }

        @Test
        void shouldCreateApplication() throws Exception {
            when(service.create(applicationDto)).thenReturn(application);

            doRequest(objectMapper.writeValueAsString(applicationDto)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(application.getName()));
        }

        @Test
        void shouldReturnBadRequestNoBody() throws Exception {
            doRequest("").andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestNoName() throws Exception {
            final var body = "{ \"url\": \"http://example.com\" }";

            doRequest(body).andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestNoUrl() throws Exception {
            final var body = "{ \"name\": \"MyApp\" }";

            doRequest(body).andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest(objectMapper.writeValueAsString(applicationDto)).andExpect(status().isOk());
        }

        private ResultActions doRequest(final String body) throws Exception {
            return mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(body));
        }

    }

    @Nested
    class UpdateApplicationTest {

        @Test
        void shouldCallService() throws Exception {
            mockMvc.perform(put("/api/applications/{id}", monitoredApplicationId).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(applicationDto))).andExpect(status().isOk());

            verify(service).update(monitoredApplicationId, applicationDto);
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            doThrow(new ApplicationNotFoundException(monitoredApplicationId)).when(service)
                .update(anyLong(), any(MonitoredApplicationDto.class));

            mockMvc.perform(put("/api/applications/{id}", monitoredApplicationId).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(applicationDto))).andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequestNoBody() throws Exception {
            mockMvc.perform(put("/api/applications/{id}", monitoredApplicationId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestNoName() throws Exception {
            final var body = "{ \"url\": \"http://example.com\" }";

            doRequest(body).andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestNoUrl() throws Exception {
            final var body = "{ \"name\": \"MyApp\" }";

            doRequest(body).andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest(objectMapper.writeValueAsString(applicationDto)).andExpect(status().isOk());
        }

        private ResultActions doRequest(final String body) throws Exception {
            return mockMvc.perform(put("/api/applications/{id}", monitoredApplicationId).contentType(MediaType.APPLICATION_JSON)
                                       .content(body));
        }
    }

    @Nested
    class DeleteApplicationTest {

        @Test
        void shouldCallService() throws Exception {
            doRequest();

            verify(service).deleteById(monitoredApplicationId);
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            doThrow(new ApplicationNotFoundException(monitoredApplicationId)).when(service).deleteById(monitoredApplicationId);

            doRequest().andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest().andExpect(status().isOk());
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(delete("/api/applications/{id}", monitoredApplicationId));
        }
    }

    @Nested
    class HandleApplicationNotFoundTest {

        @Test
        void shouldHandleApplicationNotFound() throws Exception {
            doThrow(new ApplicationNotFoundException(monitoredApplicationId)).when(service).getById(monitoredApplicationId);

            doRequest().andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Application with id 1 not found"));
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(get("/api/applications/{id}", monitoredApplicationId));
        }

    }

    @Nested
    class HandleMessageNotReadableExceptionTest {

        @Test
        void shouldHandleHttpMessageNotReadableException() throws Exception {
            final var body = "{ \"name\": \"MyApp\", \"url\": \"http://example.com\" ";

            doRequest(body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                    "JSON parse error: Unexpected end-of-input: expected close marker for Object (start marker at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 1])"));
        }

        private ResultActions doRequest(final String body) throws Exception {
            return mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(body));
        }

    }

    @Nested
    class HandleMethodArgumentNotValidExceptionTest {

        @Test
        void shouldHandleMethodArgumentNotValidException() throws Exception {
            final var invalidDto = "{ \"name\": null, \"url\": null }";

            doRequest(invalidDto).andExpect(status().isBadRequest());
        }

        private ResultActions doRequest(final String body) throws Exception {
            return mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(body));
        }

    }

}