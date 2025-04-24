package random_walk.auth.token_controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;
import ru.testit.annotations.Step;
import ru.testit.annotations.WorkItemIds;

import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;
import static random_walk.automation.util.ParseAccessTokenUtils.getInfoFromAccessToken;

class AuthGetAccessTokenTest extends AuthTest {

    @Test
    @Disabled("Хз что делать, подумаю на досуге")
    @DisplayName("Получаем access_token для пользователя")
    void getAccessTokenForUser() {

        var googleTokens = step(
                "GIVEN: Получены access и refresh токены пользователя через Google OAuth2 API",
                () -> googleAccessTokenApi.exchangeAuthCode(googleAccessTokenApi.getGoogleAuthorizationCode()));

        var accessToken = step(
                "WHEN: Получаем access_token для пользователя нашего приложения",
                () -> authServiceApi.getAuthTokens(googleTokens.getAccessToken()).getAccessToken());

        step("THEN: Access_token успешно получен", () -> {
            var tokenInfo = getInfoFromAccessToken(accessToken);
            assertAll(
                    () -> assertEquals(
                            "http://auth-service.auth-service.svc.cluster.local:8080",
                            tokenInfo.getIss(),
                            "iss токена совпадает"),
                    () -> assertEquals("DEFAULT_USER", tokenInfo.getAuthorities()[0], "authorities токена совпадает"),
                    () -> assertTrue(tokenInfo.getExp() > 0, "Время жизни токена положительно"));
        });
    }

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
