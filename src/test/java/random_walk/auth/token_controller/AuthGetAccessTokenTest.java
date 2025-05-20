package random_walk.auth.token_controller;

import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.ExternalId;
import ru.testit.annotations.WorkItemIds;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class AuthGetAccessTokenTest extends AuthTest {

    @Test
    @WorkItemIds("56")
    @ExternalId("auth_service.get_access_token_by_invalid_user_token")
    @DisplayName("Получаем access_token по невалидному токену пользователя")
    void getAccessTokenByInvalidUserToken() {

        var googleToken = UUID.randomUUID();

        var accessToken = toDefaultErrorResponse(() -> authServiceApi.getAuthTokens(googleToken.toString()));

        assertEquals("Google respond with UNAUTHORIZED error code", accessToken.getMessage(), "Сообщение об ошибке совпадает");
    }
}
