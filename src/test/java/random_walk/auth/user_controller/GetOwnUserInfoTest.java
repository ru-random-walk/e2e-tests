package random_walk.auth.user_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static random_walk.automation.domain.enums.UserRoleEnum.TEST_USER;

class GetOwnUserInfoTest extends AuthTest {

    @Test
    @DisplayName("Получение собственной информации пользователя")
    void getOwnInfoByUser() {
        var userInfoDb = step(
                "GIVEN: Получена информация о пользователе TEST_USER из базы данных",
                () -> authUserFunctions.getById(userConfigService.getUserByRole(TEST_USER).getUuid()));

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
