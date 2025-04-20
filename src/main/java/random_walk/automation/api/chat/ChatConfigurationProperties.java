package random_walk.automation.api.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import random_walk.automation.config.HttpEndpointConfig;

@ConfigurationProperties("api.chat-service")
public record ChatConfigurationProperties(HttpEndpointConfig httpEndpoint) {}
