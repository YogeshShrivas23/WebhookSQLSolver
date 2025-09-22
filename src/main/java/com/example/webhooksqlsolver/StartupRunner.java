package com.example.webhooksqlsolver;

import com.example.webhooksqlsolver.service.WebhookService;
import com.example.webhooksqlsolver.service.WebhookService.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private WebhookService webhookService;

    @Override
    public void run(String... args) {
        logger.info("Starting Bajaj Finserv Health Qualifier workflow...");
        try {
            // Step 1: Generate webhook and get credentials
            WebhookResponse response = webhookService.generateWebhook();
            if (response == null) {
                logger.error("Failed to generate webhook. Workflow aborted.");
                return;
            }
            String webhookUrl = response.getWebhookUrl();
            String accessToken = response.getAccessToken();
            String regNo = response.getRegNo();

            // Step 2: Solve SQL problem
            String finalQuery = webhookService.solveSQL(regNo);
            if (finalQuery == null) {
                logger.error("Failed to generate SQL query. Workflow aborted.");
                return;
            }

            // Step 3: Submit solution
            webhookService.submitSolution(webhookUrl, accessToken, finalQuery);
        } catch (Exception e) {
            logger.error("Exception in workflow: {}", e.getMessage(), e);
        }
    }
}
