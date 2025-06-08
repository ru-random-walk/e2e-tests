package random_walk.auth.user_controller;

import org.junit.jupiter.api.Test;
import random_walk.auth.AuthTest;
import random_walk.automation.database.auth.entities.AuthUser;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.UserRoleEnum;
import ru.random_walk.swagger.auth_service.model.DetailedUserDto;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.ExternalId;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class ChangeUserInfoTest extends AuthTest {

    private User userInfo;

    @Test
    @ExternalId("auth_service.change_user_info")
    @DisplayName("Изменение информации о пользователе")
    void changeInfoAboutUser() {
        givenStep();

        var newName = "Автотест " + new Random().nextInt(1, 100);
        var newDescription = "Тестовое описание " + new Random().nextInt(1, 100);

        var changedUserInfo = authServiceApi.changeInfoAboutUser(testTokenConfig.getAutotestToken(), newName, newDescription);

        var userDb = authUserFunctions.getById(userInfo.getUuid());

        thenStep(changedUserInfo, newName, newDescription, userDb);
    }

    @Step
    @Title("GIVEN: Получена информация о пользователе AUTOTEST_USER")
    public void givenStep() {
        userInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
    }

    @Step
    @Title("THEN: Информация о пользователе успешно изменена")
    public void thenStep(DetailedUserDto changedUserInfo, String newName, String newDescription, AuthUser userDb) {
        assertAll(
                () -> assertThat("Имя успешно отредактировано", changedUserInfo.getFullName(), equalTo(userDb.getFullName())),
                () -> assertThat("Имя изменено на ожидаемое", changedUserInfo.getFullName(), equalTo(newName)),
                () -> assertThat(
                        "Описание пользователя изменено на ожидаемое",
                        userDb.getDescription(),
                        equalTo(newDescription)));
    }
}
