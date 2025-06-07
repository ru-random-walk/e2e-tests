package random_walk.club.club_controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.club.ClubTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GetClubPhotoTest extends ClubTest {

    @Autowired
    private ClubControllerApi clubControllerApi;

    @Test
    @DisplayName("Получение фотографии клуба")
    void getClubPhoto() {
        givenStep();

        var photo = clubControllerApi.getClubPhoto(createdClubId, testTokenConfig.getAutotestToken());

        thenStep();
        assertAll(
                "Проверяем корректность возвращаемых данных",
                () -> assertThat(photo.getClubId(), equalTo(createdClubId.toString())),
                () -> assertThat(photo.getExpiresInMinutes(), equalTo(1)),
                () -> assertThat(photo.getUrl(), notNullValue()));
    }

    @Step
    @Title("GIVEN: Создан клуб для получения его фотографии")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Фотография клуба успешно получена")
    public void thenStep() {
    }
}
