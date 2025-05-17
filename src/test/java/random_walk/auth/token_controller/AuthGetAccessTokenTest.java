package random_walk.auth.token_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;

import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class AuthGetAccessTokenTest extends AuthTest {

    @Test
    @DisplayName("Получаем access_token по невалидному токену пользователя")
    void getAccessTokenByInvalidUserToken() {
        var googleToken = step("GIVEN: Получен случайный токен для пользователя", UUID::randomUUID);

        var accessToken = step(
                "WHEN: Получаем access_token для пользователя нашего приложения с невалидным токеном",
                () -> toDefaultErrorResponse(() -> authServiceApi.getAuthTokens(googleToken.toString())));

        step(
                "THEN: Попытка получения access_token пользователя по невалидному токену завершилась ошибкой",
                () -> assertEquals(
                        "Yandex respond with UNAUTHORIZED error code",
                        accessToken.getMessage(),
                        "Сообщение об ошибке совпадает"));
    }
}
