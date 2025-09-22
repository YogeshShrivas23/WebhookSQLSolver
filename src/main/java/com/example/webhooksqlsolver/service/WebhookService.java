package com.example.webhooksqlsolver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Inject properties from application.properties
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

    public static class WebhookResponse {
        private String webhookUrl;
        private String accessToken;
        private String regNo;
        public String getWebhookUrl() { return webhookUrl; }
        public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRegNo() { return regNo; }
        public void setRegNo(String regNo) { this.regNo = regNo; }
    }

    /**
     * Sends POST request to generate webhook and returns response details.
     */
    /**
     * Sends POST request to generate webhook and returns response details.
     * Retries up to 3 times on failure.
     */
    public WebhookResponse generateWebhook() {
        String requestBody = String.format("{\"name\": \"%s\",\"regNo\": \"%s\",\"email\": \"%s\"}", name, regNo, email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        int attempts = 0;
        while (attempts < 3) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(webhookApiUrl, entity, String.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    JsonNode json = objectMapper.readTree(response.getBody());
                    WebhookResponse webhookResponse = new WebhookResponse();
                    webhookResponse.setWebhookUrl(json.get("webhook").asText());
                    webhookResponse.setAccessToken(json.get("accessToken").asText());
                    webhookResponse.setRegNo(regNo);
                    logger.info("Webhook generated: {}", webhookResponse.getWebhookUrl());
                    logger.info("Access token received.");
                    logger.debug("Webhook response JSON: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
                    return webhookResponse;
                } else {
                    logger.error("Failed to generate webhook. Status: {}", response.getStatusCode());
                }
            } catch (Exception e) {
                logger.error("Exception during webhook generation (attempt {}): {}", attempts + 1, e.getMessage(), e);
            }
            attempts++;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        return null;
    }

    /**
     * Selects SQL query based on regNo.
     */
    /**
     * Generates SQL query based on regNo.
     * Odd last two digits: salary query. Even: department count query.
     */
    public String solveSQL(String regNo) {
        String lastTwo = regNo.substring(regNo.length() - 2);
        int lastTwoDigits;
        try {
            lastTwoDigits = Integer.parseInt(lastTwo);
        } catch (NumberFormatException e) {
            logger.error("Invalid regNo format: {}", regNo, e);
            return null;
        }
        String finalQuery;
        if (lastTwoDigits % 2 == 1) {
            finalQuery = "SELECT * FROM employees WHERE salary > 50000;";
        } else {
            finalQuery = "SELECT department, COUNT(*) FROM employees GROUP BY department;";
        }
        logger.info("SQL query generated for regNo {}: {}", regNo, finalQuery);
        return finalQuery;
    }

    /**
     * Submits SQL solution to webhook URL with Bearer token.
     */
    /**
     * Submits SQL solution to webhook URL with Bearer token.
     * Retries up to 3 times on failure.
     */
    public void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        String body = String.format("{\"finalQuery\": \"%s\"}", finalQuery);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        int attempts = 0;
        while (attempts < 3) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("✅ Solution submitted successfully! Response: {}", response.getBody());
                    if (response.getBody() != null) {
                        JsonNode json = objectMapper.readTree(response.getBody());
                        logger.debug("Submission response JSON: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
                    }
                    return;
                } else {
                    logger.error("❌ Submission failed. Status: {}, Response: {}", response.getStatusCode(), response.getBody());
                }
            } catch (Exception e) {
                logger.error("Exception during solution submission (attempt {}): {}", attempts + 1, e.getMessage(), e);
            }
            attempts++;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }
}
