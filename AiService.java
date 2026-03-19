package com.portfolio.smartexpense.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String askAi(String rawDescription) {
        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl + "?key=" + apiKey;

        String prompt = "Analyze this bank transaction description: '" + rawDescription + "'. " +
                "Reply with exactly two items separated by a comma. " +
                "The first item should be the clean merchant name. " +
                "The second item should be a simple category (like Food, Shopping, Utilities). " +
                "Do not say anything else. Example reply: Starbucks, Food";

        String requestJson = """
                {
                  "contents": [{
                    "parts": [{"text": "%s"}]
                  }]
                }
                """.formatted(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            // 1. Get the raw String response from Google
            String responseBody = restTemplate.postForObject(url, request, String.class);

            // 2. Use ObjectMapper to safely read that String into a JsonNode
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(responseBody);

            // 3. Dig out the AI's text
            return response.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText().trim();

        } catch (Exception e) {
            System.out.println("AI Error: " + e.getMessage());
            return "Unknown Merchant, General";
        }
    }
}