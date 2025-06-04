package random_walk.club.club_controller;

import club_service.graphql.model.PhotoInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.database.club.functions.ClubFunctions;
import random_walk.club.ClubTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.domain.enums.UserRoleEnum.FOURTH_TEST_USER;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class UploadClubPhotoTest extends ClubTest {

    @Autowired
    private ClubControllerApi clubControllerApi;

    @Autowired
    private ClubFunctions clubFunctions;

    @Test
    @DisplayName("Обновление фотографии несуществующего клуба")
    void uploadNonExistingClubPhoto() {
        var uploadedClubPhoto = toGraphqlErrorResponse(
                () -> clubControllerApi.uploadPhotoForClub(
                        UUID.randomUUID(),
                        PhotoInput.builder().setBase64(NOT_PHOTO_URL).build(),
                        userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken()));

        var errorCode = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(uploadedClubPhoto, errorCode, errorMessage);
    }

    @Test
    @DisplayName("Обновление фотографии клуба")
    void uploadClubPhoto() throws IOException {
        var base64Photo = Base64.getEncoder()
                .encodeToString(Files.readAllBytes(new File("src/main/resources/photo/test_photo.png").toPath()));
        var uploadedClubPhoto = clubControllerApi.uploadPhotoForClub(
                createdClubId,
                PhotoInput.builder().setBase64(base64Photo).build(),
                userConfigService.getUserByRole(FOURTH_TEST_USER).getAccessToken());

        var clubDb = clubFunctions.getById(createdClubId);

        assertAll(
                "Проверяем обновление фото",
                () -> assertThat(clubDb.getPhotoVersion(), equalTo(1)),
                () -> assertThat(uploadedClubPhoto.getUrl(), notNullValue()),
                () -> assertThat(uploadedClubPhoto.getClubId(), equalTo(createdClubId.toString())));
    }
}
