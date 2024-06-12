package com.dockermonitor.controller.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dockermonitor.service.ImportMonitoredApplicationService;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ImportMonitoringApplicationController.class)
class ImportMonitoringApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportMonitoredApplicationService service;

    @Nested
    class ImportApplicationsTest {

        private final MultipartFile multipartFile = new MockMultipartFile("file",
                                                                          "test.csv",
                                                                          MediaType.TEXT_PLAIN_VALUE,
                                                                          "name,url\nFacebook,https://facebook.com".getBytes());

        @Test
        void shouldCallService() throws Exception {
            doRequest();

            verify(service).importApplicationsFromCSV(any());
        }

        @Test
        void shouldReturnOk() throws Exception {
            doRequest().andExpect(status().isOk());
        }

        @Test
        void shouldReturnClientError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/applications/import")).andExpect(status().is4xxClientError());
        }

        @Test
        void shouldHandleIOException() throws Exception {
            final var file = new MockMultipartFile("file",
                                                   "test.csv",
                                                   MediaType.TEXT_PLAIN_VALUE,
                                                   "name,url\nFacebook,https://facebook.com".getBytes());
            doThrow(new IOException("File not found")).when(service).importApplicationsFromCSV(file);

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/applications/import").file(file))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Failed to import CSV file: " + file.getName()));
        }

        @Test
        void shouldHandleDataIntegrityViolationException() throws Exception {
            final var file = new MockMultipartFile("file",
                                                   "test.csv",
                                                   MediaType.TEXT_PLAIN_VALUE,
                                                   "name,url\nFacebook,https://facebook.com".getBytes());
            doThrow(new DataIntegrityViolationException("Duplicate entry for key 'URL'")).when(service).importApplicationsFromCSV(file);

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/applications/import").file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("DataIntegrityViolationException occurred"));
        }

        private ResultActions doRequest() throws Exception {
            return mockMvc.perform(MockMvcRequestBuilders.multipart("/api/applications/import")
                                       .file("file", multipartFile.getBytes())
                                       .contentType(MediaType.MULTIPART_FORM_DATA));
        }

    }

}