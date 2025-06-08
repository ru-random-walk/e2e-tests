package random_walk.auth.user_controller;

import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;
import random_walk.automation.database.auth.entities.AuthUser;
import ru.random_walk.swagger.auth_service.model.DetailedUserDto;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.ExternalId;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static random_walk.automation.domain.enums.UserRoleEnum.FIFTH_TEST_USER;

class GetOwnUserInfoTest extends AuthTest {

    private String userToken;

    @Test
    @ExternalId("auth_service.get_own_user_info")
    @DisplayName("Получение собственной информации пользователя")
    void getOwnInfoByUser() {
        givenStep();

        var userInfo = authServiceApi.getUserSelfInfo(userToken);

        var userInfoDb = authUserFunctions.getById(userConfigService.getUserByRole(FIFTH_TEST_USER).getUuid());

        thenStep(userInfo, userInfoDb);
    }

    @Step
    @Title("GIVEN: Получена токен тестового пользователя")
    public void givenStep() {
        userToken = userConfigService.getUserByRole(FIFTH_TEST_USER).getAccessToken();
    }

    @Step
    @Title("THEN: Личная информация о пользователе успешно получена")
    public void thenStep(DetailedUserDto userInfo, AuthUser userInfoDb) {
        assertAll(
                () -> assertEquals(userInfoDb.getId(), userInfo.getId(), "Id соответствует ожидаемому"),
                () -> assertEquals(userInfoDb.getFullName(), userInfo.getFullName(), "Имя соответствует ожидаемому"),
                () -> assertEquals(userInfoDb.getAvatar(), userInfo.getAvatar(), "Avatar соответствует ожидаемому"),
                () -> assertEquals(userInfoDb.getEmail(), userInfo.getEmail(), "Email соответствует ожидаемому"));
    }
}
