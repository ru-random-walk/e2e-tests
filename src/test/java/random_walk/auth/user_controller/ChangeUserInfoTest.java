package random_walk.auth.user_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.auth.AuthTest;
import random_walk.automation.database.club.functions.UserFunctions;
import random_walk.automation.database.matcher.functions.PersonFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class ChangeUserInfoTest extends AuthTest {

    @Autowired
    private UserFunctions userFunctions;

    @Autowired
    private PersonFunctions personFunctions;

    @Test
    @DisplayName("Изменение информации о пользователе")
    void changeInfoAboutUser() {
        var userInfo = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);

        var newName = "Автотест " + new Random().nextInt(1, 100);
        var newDescription = "Тестовое описание " + new Random().nextInt(1, 100);

        var changedUserInfo = authServiceApi.changeInfoAboutUser(testTokenConfig.getAutotestToken(), newName, newDescription);

        var userDb = authUserFunctions.getById(userInfo.getUuid());

        assertAll(
                "Проверяем отредактированную информацию",
                () -> assertThat("Имя успешно отредактировано", changedUserInfo.getFullName(), equalTo(userDb.getFullName())),
                () -> assertThat("Имя изменено на ожидаемое", changedUserInfo.getFullName(), equalTo(newName)),
                () -> assertThat("Описание пользователя изменено на ожидаемое", userDb.getDescription(), equalTo(newDescription)),
                () -> assertThat(
                        "Имя пользователя в matcher изменено на ожидаемое",
                        personFunctions.getPersonInfo(userInfo.getUuid()).getFullName(),
                        equalTo(newName)),
                () -> assertThat(
                        "Имя пользователя в club изменено на ожидаемое",
                        userFunctions.getById(userInfo.getUuid()).getFullName(),
                        equalTo(newName)));
    }
}
