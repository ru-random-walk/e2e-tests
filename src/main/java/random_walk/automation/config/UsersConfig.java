package random_walk.automation.config;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import random_walk.automation.domain.User;

import java.util.List;

@Data
@SpringBootConfiguration
@ConfigurationProperties
public class UsersConfig {

    private List<User> users;
}
