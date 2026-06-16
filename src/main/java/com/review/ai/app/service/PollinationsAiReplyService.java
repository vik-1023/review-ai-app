package com.review.ai.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.review.ai.app.config.PollinationsAiConfig;
import com.review.ai.app.entity.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollinationsAiReplyService {

    private final PollinationsAiConfig aiConfig;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    /**
     * Generate an AI reply for a review using Pollinations API
     */
    public String generateReply(Review review) {
        log.info("Generating Pollinations AI reply for review ID: {}, Sentiment: {}", 
                 review.getId(), review.getSentiment());

        String customerName = review.getCustomerName() != null ? 
                              review.getCustomerName() : "Valued Customer";
        
        // Build prompt based on sentiment
        String prompt = buildPrompt(review.getSentiment(), review.getReviewText(), customerName);

        try {
            String response = callPollinationsApi(prompt);
            log.info("AI reply generated successfully for review: {}", review.getId());
            return response;
        } catch (Exception e) {
            log.error("Pollinations AI API call failed: {}", e.getMessage());
            return getFallbackReply(review.getSentiment(), customerName);
        }
    }

    /**
     * Build a prompt based on review sentiment
     */
    private String buildPrompt(String sentiment, String reviewText, String customerName) {
        String basePrompt = String.format("""
            Generate a professional customer service reply for this review.
            
            Customer Name: %s
            Review Text: "%s"
            Sentiment: %s
            
            Requirements:
            1. Be professional and empathetic
            2. Keep it under 100 words
            3. Thank the customer
            4. Address their specific concerns
            
            Reply:""", customerName, reviewText, sentiment);

        // Add sentiment-specific guidance
        if ("POSITIVE".equals(sentiment)) {
            basePrompt += "\n\nMake it warm and appreciative. Mention specific positive points.";
        } else if ("NEGATIVE".equals(sentiment)) {
            basePrompt += "\n\nBe apologetic and solution-oriented. Promise to investigate.";
        } else {
            basePrompt += "\n\nBe polite and ask how we can improve.";
        }

        return basePrompt;
    }

    /**
     * Call Pollinations AI API
     */
    private String callPollinationsApi(String prompt) throws Exception {
        // Build request payload (OpenAI-compatible format)
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", aiConfig.getModel());
        payload.put("temperature", aiConfig.getTemperature());
        payload.put("max_tokens", aiConfig.getMaxTokens());
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        payload.put("messages", List.of(userMessage));

        // Convert payload to JSON
        String jsonPayload = objectMapper.writeValueAsString(payload);

        log.debug("Calling Pollinations API with prompt: {}", prompt.substring(0, Math.min(50, prompt.length())) + "...");

        // Make the API call
        String response = restClient.post()
                .uri(aiConfig.getUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPayload)
                .retrieve()
                .body(String.class);

        // Parse response
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).path("message");
            if (message != null && !message.isMissingNode()) {
                String content = message.path("content").asText();
                if (content != null && !content.isEmpty()) {
                    return content.trim();
                }
            }
        }

        log.warn("Unexpected API response format: {}", response);
        return getFallbackReply("NEUTRAL", "Valued Customer");
    }

    /**
     * Fallback replies when API fails
     */
    private String getFallbackReply(String sentiment, String customerName) {
        String[] positiveReplies = {
            "Dear %s, Thank you so much for your wonderful review! 🌟 We're thrilled you had a great experience. Your feedback means the world to us!",
            "Hi %s, We truly appreciate your kind words! Thanks for choosing us. We hope to serve you again soon!",
            "Dear %s, Your 5-star review made our day! 🎉 We're committed to maintaining this quality for you."
        };

        String[] negativeReplies = {
            "Dear %s, We sincerely apologize for your disappointing experience. 😔 Please contact our support team and we'll make it right.",
            "Hi %s, We're sorry to hear this. Our customer service manager will reach out to you personally.",
            "Dear %s, Thank you for your honest feedback. We've flagged this for immediate attention."
        };

        String[] neutralReplies = {
            "Dear %s, Thank you for your feedback. We value your input and will use it to improve!",
            "Hi %s, Thanks for sharing your experience. We hope to earn your 5-star rating next time!"
        };

        String[] replies;
        if ("POSITIVE".equals(sentiment)) {
            replies = positiveReplies;
        } else if ("NEGATIVE".equals(sentiment)) {
            replies = negativeReplies;
        } else {
            replies = neutralReplies;
        }

        String reply = String.format(replies[random.nextInt(replies.length)], customerName);
        reply += "\n\n📦 Order Ref: ORD" + String.format("%08d", System.currentTimeMillis() % 100000000);
        
        return reply;
    }
}