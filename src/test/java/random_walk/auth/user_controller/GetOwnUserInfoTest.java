package random_walk.auth.user_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.auth.AuthTest;
import random_walk.automation.api.auth.services.AuthServiceApi;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetOwnUserInfoTest extends AuthTest {

    @Autowired
    private AuthServiceApi authServiceApi;

    @Test
    @DisplayName("Получение собственной информации пользователя")
    void getOwnInfoByUser() {
        var userInfoDb = step(
                "GIVEN: Получена информация о пользователе \"Тест\" из базы данных",
                () -> authUserFunctions.getUserByFullName("Тест"));

        var userInfo = step("WHEN: Пользователь получает собственную информацию", () -> authServiceApi.getUserSelfInfo());

        step(
                "THEN: Личная информация успешно получена пользователем",
                () -> assertAll(
                        () -> assertEquals(userInfoDb.getId(), userInfo.getId(), "Id соответствует ожидаемому"),
                        () -> assertEquals(userInfoDb.getFullName(), userInfo.getFullName(), "Имя соответствует ожидаемому"),
                        () -> assertEquals(userInfoDb.getAvatar(), userInfo.getAvatar(), "Avatar соответствует ожидаемому"),
                        () -> assertEquals(userInfoDb.getEmail(), userInfo.getEmail(), "Email соответствует ожидаемому")));
    }
}
