package random_walk.automation.config;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.UserRoleEnum;

import java.util.EnumMap;
import java.util.List;

@Data
@SpringBootConfiguration
@ConfigurationProperties
public class UsersConfig {

    private List<User> users;

    private EnumMap<UserRoleEnum, String> userTokens = new EnumMap<>(UserRoleEnum.class);
}
