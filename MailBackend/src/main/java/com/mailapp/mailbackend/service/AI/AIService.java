package com.mailapp.mailbackend.service.AI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate(); // Used to make external api calls
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String summarizeEmail(String emailBody) {
        if (emailBody == null || emailBody.trim().isEmpty()) {
            return "Email body is empty.";
        }

        try {
            HttpHeaders headers = new HttpHeaders(); // Defines some data involving the API requests
            headers.setContentType(MediaType.APPLICATION_JSON); // Says that we are sending JSON
            headers.setBearerAuth(apiKey); // Required so that the api verifies the used api key
            
            // Build the params
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "Summarize emails in 2-3 sentences. Write the summary directly without any introductory phrases like 'Here is a summary' or 'This email discusses'. Just provide the core summary."),
                    Map.of("role", "user", "content", emailBody)
            ));
            requestBody.put("max_tokens", 150); // Sets max number of characters for the summarizer so that the AI generates around 2-3 sentences
            requestBody.put("temperature", 0.5); // Determines the level of creativity of the AI (range: 0-2) (Using 0.5 to prevent it from being too creative, ie. only use data from the actual email body and not invent new data)

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            System.out.println("Calling Groq API...");
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, entity, Map.class);
            System.out.println("Response status: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return content != null ? content.trim() : "No summary generated.";
                }
            }

            return "Failed to generate summary. Status: " + response.getStatusCode();
        } catch (Exception e) {
            System.err.println("Error calling Groq API: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            return "Error generating summary: " + e.getMessage();
        }
    }
}
