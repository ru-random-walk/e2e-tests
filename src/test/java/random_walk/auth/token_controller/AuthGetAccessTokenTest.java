package random_walk.auth.token_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;
import ru.testit.annotations.Step;
import ru.testit.annotations.WorkItemIds;

import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class AuthGetAccessTokenTest extends AuthTest {

    @Test
    @WorkItemIds("56")
    @DisplayName("Получаем access_token по невалидному токену пользователя")
    void getAccessTokenByInvalidUserToken() {

        var googleToken = UUID.randomUUID();

        var accessToken = toDefaultErrorResponse(() -> authServiceApi.getAuthTokens(googleToken.toString()));


                assertEquals(
                        "Google respond with UNAUTHORIZED error code",
                        accessToken.getMessage(),
                        "Сообщение об ошибке совпадает");
    }
}
