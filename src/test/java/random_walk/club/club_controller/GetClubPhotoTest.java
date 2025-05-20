package random_walk.club.club_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.club.ClubTest;

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
        var photo = clubControllerApi.getClubPhoto(createdClubId, testTokenConfig.getToken());

        assertAll(
                "Проверяем корректность возвращаемых данных",
                () -> assertThat(photo.getClubId(), equalTo(createdClubId.toString())),
                () -> assertThat(photo.getExpiresInMinutes(), equalTo(1)),
                () -> assertThat(photo.getUrl(), notNullValue()));
    }
}
