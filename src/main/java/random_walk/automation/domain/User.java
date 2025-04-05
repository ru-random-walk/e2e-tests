package random_walk.automation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import random_walk.automation.domain.enums.UserRoleEnum;

import java.util.UUID;

@Data
@NoArgsConstructor
public class User {

    private String name;

    private UserRoleEnum role;

    private UUID uuid;
}
