package random_walk.automation.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("datasource.matcher")
public class MatcherDbConfig extends DatabaseAuthConfig {}
