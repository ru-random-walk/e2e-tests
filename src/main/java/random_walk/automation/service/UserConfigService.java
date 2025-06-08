package random_walk.automation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.config.UsersConfig;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.UserRoleEnum;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final UsersConfig usersConfig;

    @Step
    @Title("Получаем пользователя из конфига")
    @Description("Пользователь с ролью {role}")
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
}
