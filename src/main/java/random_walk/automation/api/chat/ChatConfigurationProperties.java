package random_walk.automation.api.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import random_walk.automation.config.HttpEndpointConfig;

@ConfigurationProperties("api.club-service")
public record ChatConfigurationProperties(HttpEndpointConfig httpEndpoint) {}
