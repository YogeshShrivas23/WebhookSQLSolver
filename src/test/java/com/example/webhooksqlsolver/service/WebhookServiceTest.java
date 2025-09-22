package com.example.webhooksqlsolver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
public class WebhookServiceTest {
    @InjectMocks
    private WebhookService webhookService;

    @Mock
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Value("${webhook.api.url}")
    private String webhookApiUrl;
    @Value("${webhook.submit.url}")
    private String webhookSubmitUrl;
    @Value("${webhook.name}")
    private String name;
    @Value("${webhook.regNo}")
    private String regNo;
    @Value("${webhook.email}")
    private String email;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGenerateWebhookSuccess() throws Exception {
        String responseJson = "{\"webhook\":\"http://test/webhook\",\"accessToken\":\"token\"}";
        mockServer.expect(requestTo(webhookApiUrl))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));
        WebhookService.WebhookResponse response = webhookService.generateWebhook();
        assertNotNull(response);
        assertEquals("http://test/webhook", response.getWebhookUrl());
        assertEquals("token", response.getAccessToken());
    }

    @Test
    void testSolveSQLOdd() {
        String query = webhookService.solveSQL("REG12347");
        assertEquals("SELECT * FROM employees WHERE salary > 50000;", query);
    }

    @Test
    void testSolveSQLEven() {
        String query = webhookService.solveSQL("REG12348");
        assertEquals("SELECT department, COUNT(*) FROM employees GROUP BY department;", query);
    }

    @Test
    void testSubmitSolutionSuccess() {
        String webhookUrl = "http://test/webhook";
        String accessToken = "token";
        String finalQuery = "SELECT * FROM employees WHERE salary > 50000;";
        mockServer.expect(requestTo(webhookUrl))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess("{\"result\":\"success\"}", MediaType.APPLICATION_JSON));
        webhookService.submitSolution(webhookUrl, accessToken, finalQuery);
        // No exception means success
    }
}
