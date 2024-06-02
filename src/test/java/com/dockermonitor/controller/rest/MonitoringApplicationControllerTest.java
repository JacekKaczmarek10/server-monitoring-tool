package com.dockermonitor.controller.rest;

import com.dockermonitor.dto.MonitoredAppDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private MonitoredAppDto applicationDto;

    @BeforeEach
    void setUp() {
        application = new MonitoredApplication();
        application.setId(1L);
        application.setName("Codepred");

        applicationDto = new MonitoredAppDto("Codepred", "https://codepred.pl/");
    }

    @Nested
    class GetApplicationsTest {

        @Test
        void shouldReturnApplications() throws Exception {
            when(service.findAll()).thenReturn(Collections.singletonList(application));

            mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(application.getName()));
        }

        @Test
        void shouldReturnEmptyList() throws Exception {
            when(service.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        }

    }

    @Nested
    class GetApplicationTest {

        @Test
        void shouldReturnApplication() throws Exception {
            when(service.getById(anyLong())).thenReturn(application);

            mockMvc.perform(get("/api/applications/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(application.getName()));
        }

        @Test
        void shouldReturnEmpty() throws Exception {
            when(service.getById(anyLong())).thenThrow(ApplicationNotFoundException.class);

            mockMvc.perform(get("/api/applications/{id}", 1L))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetApplicationStatusTest {

        @Test
        void shouldReturnApplicationStatus() throws Exception {
            when(service.getStatusById(anyLong())).thenReturn(true);

            mockMvc.perform(get("/api/applications/{id}/status", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        }

        @Test
        void shouldThrowApplicationNotFoundException() throws Exception {
            when(service.getStatusById(anyLong())).thenThrow(ApplicationNotFoundException.class);

            mockMvc.perform(get("/api/applications/{id}/status", 1L))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateApplicationTest {

        @Test
        void shouldCreateApplication() throws Exception {
            when(service.create(any(MonitoredAppDto.class))).thenReturn(application);

            mockMvc.perform(post("/api/applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(applicationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(application.getName()));
        }
    }

    @Nested
    class UpdateApplicationTest {

        @Test
        void shouldUpdateApplication() throws Exception {
            mockMvc.perform(put("/api/applications/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(applicationDto)))
                .andExpect(status().isOk());

            verify(service).update(anyLong(), any(MonitoredAppDto.class));
        }
    }

    @Nested
    class DeleteApplicationTest {

        @Test
        void shouldDeleteApplication() throws Exception {
            mockMvc.perform(delete("/api/applications/{id}", 1L))
                .andExpect(status().isOk());

            verify(service).deleteById(anyLong());
        }
    }

}