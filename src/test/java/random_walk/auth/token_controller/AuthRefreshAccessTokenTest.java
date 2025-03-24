package random_walk.auth.token_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

class AuthRefreshAccessTokenTest extends AuthTest {

    @Test
    @DisplayName("Получение актуального access_token по refresh_token")
    void getActualAccessTokenByRefreshToken() {
        var actualTokens = step(
                "GIVEN: Получены access и refresh токены пользователя через Google OAuth2 API",
                () -> googleAccessTokenApi.exchangeAuthCode(googleAccessTokenApi.getGoogleAuthorizationCode()));

        var randomWalkTokens = step(
                "AND: Получены токены для приложения random_walk",
                () -> authServiceApi.getAuthTokens(actualTokens.getAccessToken()));

        var newAccessToken = step(
                "WHEN: Получаем новый access_token по refresh_token",
                () -> authServiceApi.refreshAuthToken(randomWalkTokens.getRefreshToken()));

        step("THEN: Новый access_token успешно получен", () -> {
            var userId = authUserFunctions.getUserByFullName("Тест").getId();
            var userRefreshToken = refreshTokenFunctions.getRefreshTokenById(userId).getToken().toString();
            assertAll(
                    () -> assertNotEquals(
                            randomWalkTokens.getAccessToken(),
                            newAccessToken.getAccessToken(),
                            "Новый access_token не совпадает со старым"),
                    () -> assertEquals(userRefreshToken, newAccessToken.getRefreshToken(), "refresh_token был обновлен в базе"));
        });
    }

    @Test
    @DisplayName("Пробный тест")
    void f() {
        var a = 1;
        assertEquals(1, a);
    }
}
