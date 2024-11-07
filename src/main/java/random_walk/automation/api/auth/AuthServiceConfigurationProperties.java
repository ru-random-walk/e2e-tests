package random_walk.automation.api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import random_walk.automation.config.HttpEndpointConfig;

@ConfigurationProperties("api.auth-service")
public record AuthServiceConfigurationProperties(HttpEndpointConfig httpEndpoint) {}
