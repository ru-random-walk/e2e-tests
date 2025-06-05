package random_walk.automation.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum AuthType {
    PASSWORD("password"),
    GOOGLE("google"),
    YANDEX("yandex");

    private final String name;

    public static AuthType getByName(String name) {
        return Arrays.stream(AuthType.values()).filter(type -> type.name.equals(name)).findFirst().orElse(null);
    }
}
