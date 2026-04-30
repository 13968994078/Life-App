package com.lifeapp.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.lifeapp.vo.HomeContentResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class HitokotoRemoteInspirationClient implements RemoteInspirationClient {

    private static final String REMOTE_URL = "https://v1.hitokoto.cn/?c=d&encode=json";

    private final RestTemplate restTemplate;

    public HitokotoRemoteInspirationClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    @Override
    public HomeContentResponse.QuoteBlock fetchLatestQuote() {
        JsonNode payload = restTemplate.getForObject(REMOTE_URL, JsonNode.class);
        if (payload == null) {
            throw new IllegalStateException("empty response");
        }

        String text = payload.path("hitokoto").asText("").trim();
        if (!StringUtils.hasText(text)) {
            throw new IllegalStateException("quote missing");
        }

        String source = payload.path("from").asText("hitokoto").trim();
        if (!StringUtils.hasText(source)) {
            source = "hitokoto";
        }
        return new HomeContentResponse.QuoteBlock(text, source);
    }
}
