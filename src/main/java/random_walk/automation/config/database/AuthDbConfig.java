package random_walk.automation.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("datasource.auth")
public class AuthDbConfig extends DatabaseAuthConfig {}
