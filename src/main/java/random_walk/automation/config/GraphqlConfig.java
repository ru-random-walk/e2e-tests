package random_walk.automation.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("api.graphql")
@NoArgsConstructor
public class GraphqlConfig {

    private String baseUri;

    private String requestPath;
}
