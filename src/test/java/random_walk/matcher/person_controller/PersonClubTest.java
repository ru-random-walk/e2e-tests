package random_walk.matcher.person_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.matcher.service.PersonMatcherApi;
import random_walk.automation.database.matcher.functions.PersonClubFunctions;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.matcher.MatcherTest;

import java.util.Arrays;

import static io.qameta.allure.Allure.step;
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
        var userUuid = step(
                "GIVEN: Получена информация о пользователе с ролью TEST_USER",
                () -> userConfigService.getUserByRole(UserRoleEnum.TEST_USER).getUuid());

        var userClubs = step(
                "WHEN: Получены клубы, в которых состоит пользователь",
                () -> Arrays.stream(personMatcherApi.getInfoAboutUserClubs()).toList());

        step("THEN: Информация о полученных клубах успешно получена", () -> {
            var userClubsDb = personClubFunctions.getUserClubs(userUuid);
            assertThat(
                    "Список клубов соответствует ожидаемому",
                    userClubs,
                    containsInAnyOrder(toListClubDto(userClubsDb).toArray()));
        });
    }
}
