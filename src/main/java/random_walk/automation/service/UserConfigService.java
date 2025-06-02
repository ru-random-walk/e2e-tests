package random_walk.automation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.api.auth.service.AuthServiceApi;
import random_walk.automation.config.UsersConfig;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.UserRoleEnum;

import java.util.EnumMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final UsersConfig usersConfig;

    private final AuthServiceApi api;

    public User getUserByRole(UserRoleEnum role) {
        System.out.println(usersConfig.getUsers());
        return usersConfig.getUsers()
                .stream()
                .filter(client -> client.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Клиент с ролью " + role + " не найден"));
    }

    public List<User> getUsers() {
        return usersConfig.getUsers();
    }

    public String getAccessToken(UserRoleEnum userRoleEnum) {
        return usersConfig.getUserTokens().get(userRoleEnum);
    }

    public EnumMap<UserRoleEnum, String> getUsersTokens() {
        return usersConfig.getUserTokens();
    }
}
