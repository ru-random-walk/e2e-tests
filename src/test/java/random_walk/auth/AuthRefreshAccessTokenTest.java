package random_walk.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.auth.services.GoogleAccessTokenApi;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

class AuthRefreshAccessTokenTest extends BaseTest {

    @Autowired
    private GoogleAccessTokenApi googleAccessTokenApi;

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
            var userRefreshToken = refreshTokenFunctions.getRefreshTokenById(authUserFunctions.getUserByFullName("Тест").getId())
                    .getToken()
                    .toString();
            assertAll(
                    () -> assertNotEquals(
                            randomWalkTokens.getAccessToken(),
                            newAccessToken.getAccessToken(),
                            "Новый access_token не совпадает со старым"),
                    () -> assertEquals(userRefreshToken, newAccessToken.getRefreshToken(), "refresh_token был обновлен в базе"));
        });
    }
}
