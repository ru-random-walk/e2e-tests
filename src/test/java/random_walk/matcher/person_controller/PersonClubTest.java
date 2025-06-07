package random_walk.matcher.person_controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class PersonClubTest extends MatcherTest {

    @Autowired
    private PersonClubFunctions personClubFunctions;

    @Autowired
    private PersonMatcherApi personMatcherApi;

    @Test
    @DisplayName("Получение информации о клубах пользователя")
    void getInfoAboutUserClubs() {
        givenStep();

        var userUuid = userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid();

        var userClubs = Arrays.stream(personMatcherApi.getInfoAboutUserClubs()).toList();

        var userClubsDb = personClubFunctions.getUserClubs(userUuid);
        thenStep();

        assertThat("Список клубов соответствует ожидаемому", userClubs, containsInAnyOrder(toListClubDto(userClubsDb).toArray()));
    }

    @Step
    @Title("GIVEN: Получена информация о пользователе для получения данных о нем")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Клубы пользователя успешно получены")
    public void thenStep() {
    }
}
