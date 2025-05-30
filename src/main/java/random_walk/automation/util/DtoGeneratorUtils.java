package random_walk.automation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import random_walk.automation.database.auth.entities.AuthUser;
import ru.random_walk.swagger.auth_service.model.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DtoGeneratorUtils {

    public static UserDto generateUserDto(AuthUser user) {
        return new UserDto().id(user.getId())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .description(user.getDescription());
    }
}
