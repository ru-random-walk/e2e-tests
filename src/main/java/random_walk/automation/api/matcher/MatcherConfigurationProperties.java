package random_walk.automation.api.matcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import random_walk.automation.config.HttpEndpointConfig;

@ConfigurationProperties("api.matcher-service")
public record MatcherConfigurationProperties(HttpEndpointConfig httpEndpoint) {}
