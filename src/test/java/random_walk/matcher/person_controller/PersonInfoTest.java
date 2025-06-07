package random_walk.matcher.person_controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.database.matcher.functions.PersonFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersonInfoTest extends MatcherTest {

    @Autowired
    private PersonMatcherApi personMatcherApi;

    @Autowired
    private PersonFunctions personFunctions;

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Test
    @DisplayName("Получение информации о пользователе")
    void getUserInfo() {
        givenStep();

        var userUuid = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var currentUserInfo = personMatcherApi.getInfoAboutUser();

        var userInfoDb = personFunctions.getPersonInfo(userUuid);
        var userClubs = personClubFunctions.getUserClubs(userUuid);

        thenStep();
        assertAll(
                () -> assertThat("Id соответствует ожидаемому", currentUserInfo.getId(), equalTo(userInfoDb.getId())),
                () -> assertThat("Возраст соответствует ожидаемому", currentUserInfo.getAge(), equalTo(userInfoDb.getAge())),
                () -> assertThat("Пол соответствует ожидаемому", currentUserInfo.getGender(), equalTo(userInfoDb.getGender())),
                () -> assertThat(
                        "Имя соответствует ожидаемому",
                        currentUserInfo.getFullName(),
                        equalTo(userInfoDb.getFullName())),
                () -> assertThat(
                        "Клубы пользователя соответствуют ожидаемому",
                        currentUserInfo.getClubs(),
                        containsInAnyOrder(toListClubDto(userClubs).toArray())));
    }

    @Step
    @Title("GIVEN: Получена информация о пользователе для получения данных о нем")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Информация о пользователе успешно получена")
    public void thenStep() {
    }
}
