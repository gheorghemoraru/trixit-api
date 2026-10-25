package com.trixit.infrastructure.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.trixit.application.CheckHealthUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    // Controllers are component-scanned by the slice; satisfy HealthController's dependency.
    @MockitoBean
    CheckHealthUseCase checkHealth;

    @Test
    void unauthenticatedRequestGets401WithoutRedirect() throws Exception {
        mockMvc.perform(get("/conversations"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    @Test
    @WithMockUser
    void postWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post("/conversations"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void postWithCsrfTokenIsNotRejectedBySecurity() throws Exception {
        // No such endpoint yet: reaching the dispatcher (404) proves security let it through.
        mockMvc.perform(post("/conversations").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void authenticatedGetIsNotRejectedBySecurity() throws Exception {
        mockMvc.perform(get("/conversations"))
                .andExpect(status().isNotFound());
    }

    @Test
    void corsPreflightIsNotAnswered() throws Exception {
        mockMvc.perform(options("/health")
                        .header(HttpHeaders.ORIGIN, "http://evil.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
