package random_walk.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.auth.services.GoogleAccessTokenApi;

import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static random_walk.automation.utils.ParseAccessTokenUtils.getInfoFromAccessToken;

class AuthGetAccessTokenTest extends BaseTest {

    @Autowired
    private GoogleAccessTokenApi googleAccessTokenApi;

    @Test
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
    @DisplayName("Получаем access_token по невалидному токену пользователя")
    void getAccessTokenByInvalidUserToken() {
        var googleToken = step("GIVEN: Получен случайный токен для пользователя", UUID::randomUUID);

        var accessToken = step(
                "WHEN: Получаем access_token для пользователя нашего приложения с невалидным токеном",
                () -> authServiceApi.getAuthTokens(googleToken.toString()).getAccessToken());

        // добавить обработку исключений
        step("THEN: Попытка получения access_token пользователя по невалидному токену завершилась ошибкой");
    }
}
