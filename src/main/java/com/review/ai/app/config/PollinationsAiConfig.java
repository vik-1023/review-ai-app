package com.review.ai.app.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai.pollinations")
public class PollinationsAiConfig {
    private String url = "https://gen.pollinations.ai/v1/chat/completions";
    private String model = "openai";
    private double temperature = 0.7;
    private int maxTokens = 200;
}