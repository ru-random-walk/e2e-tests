package random_walk.automation.config.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("datasource.club")
public class ClubDbConfig extends DatabaseAuthConfig {}
