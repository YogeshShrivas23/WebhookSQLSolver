package com.example.webhooksqlsolver.runner;

import com.example.webhooksqlsolver.service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);
    private final WebhookService webhookService;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting workflow...");
        // Step 1: Generate webhook and get credentials
        WebhookService.WebhookResponse response = webhookService.generateWebhook();
        if (response == null) {
            logger.error("Failed to generate webhook. Workflow aborted.");
            return;
        }

        // Step 2: Solve SQL problem
        String finalQuery = webhookService.solveSQL(response.getRegNo());
        if (finalQuery == null) {
            logger.error("Failed to generate SQL query. Workflow aborted.");
            return;
        }

        // Step 3: Submit solution
        webhookService.submitSolution(response.getWebhookUrl(), response.getAccessToken(), finalQuery);
    }
}
