package com.google.a2a.common.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.a2a.common.types.AgentCard;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 代理卡解析器
 */
public class CardResolver {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public CardResolver() {
        this.webClient = WebClient.create();
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
    
    /**
     * 从URL获取代理卡
     * @param url 代理URL
     * @return 代理卡
     */
    public Mono<AgentCard> getAgentCard(String url) {
        String wellKnownUrl;
        try {
            URI uri = new URI(url);
            String baseUrl = uri.getScheme() + "://" + uri.getAuthority();
            wellKnownUrl = baseUrl + "/.well-known/agent.json";
        } catch (URISyntaxException e) {
            return Mono.error(new IllegalArgumentException("Invalid URL: " + url, e));
        }
        
        return webClient.get()
                .uri(wellKnownUrl)
                .retrieve()
                .bodyToMono(AgentCard.class);
    }
} 