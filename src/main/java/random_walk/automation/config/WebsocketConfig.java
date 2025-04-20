package random_walk.automation.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("api.websocket")
@NoArgsConstructor
public class WebsocketConfig {

    private String url;

    private String messageEndpoint;

    private String chatEndpoint;
}
