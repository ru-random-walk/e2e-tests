package random_walk.club.club_controller;

import club_service.graphql.model.AnswerType;
import club_service.graphql.model.FormInput;
import club_service.graphql.model.QuestionInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.club.services.ClubControllerApi;
import random_walk.automation.database.club.functions.AnswerFunctions;
import random_walk.automation.database.club.functions.ApprovementFunctions;
import random_walk.automation.database.club.functions.ClubFunctions;
import random_walk.automation.database.club.functions.MemberFunctions;
import random_walk.club.ClubTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.ErrorAsserts.checkGraphqlError;
import static random_walk.automation.util.ExceptionUtils.toGraphqlErrorResponse;

public class RemoveClubTest extends ClubTest {

    @Autowired
    private ClubControllerApi clubControllerApi;

    @Autowired
    private ClubFunctions clubFunctions;

    @Autowired
    private MemberFunctions memberFunctions;

    @Autowired
    private ApprovementFunctions approvementFunctions;

    @Autowired
    private AnswerFunctions answerFunctions;

    private UUID clubId;

    @BeforeAll
    @Step
    @Title("Создан клуб с тестами для его удаления")
    void createClubWithApprovement() {
        var club = clubControllerApi.createClubWithFormApprovement(
                "Клуб с тестом",
                "Описание клуба с тестами",
                FormInput.builder()
                        .setQuestions(
                                List.of(
                                        QuestionInput.builder()
                                                .setText("Вопрос с одним ответом")
                                                .setAnswerType(AnswerType.SINGLE)
                                                .setAnswerOptions(List.of("1", "2", "3", "4"))
                                                .setCorrectOptionNumbers(List.of(0))
                                                .build(),
                                        QuestionInput.builder()
                                                .setText("Вопрос с несколькими ответами")
                                                .setAnswerType(AnswerType.MULTIPLE)
                                                .setAnswerOptions(List.of("1", "2", "3", "4"))
                                                .setCorrectOptionNumbers(List.of(0))
                                                .build()))
                        .build(),
                testTokenConfig.getAutotestToken());
        clubId = UUID.fromString(club.getId());
    }

    @Test
    @DisplayName("Проверка удаления клуба")
    void removeClubWithAllInfo() {
        givenStep();

        var removedClub = clubControllerApi.removeClub(clubId, testTokenConfig.getAutotestToken());

        var removedClubId = UUID.fromString(removedClub);

        var approvements = approvementFunctions.getByClubId(clubId);

        var answers = approvements.stream().map(approvement -> answerFunctions.getByApprovementId(approvement.getId())).toList();

        thenStep();
        assertAll(
                "Все данные о клубе удалены",
                () -> assertThat(clubFunctions.getById(removedClubId), nullValue()),
                () -> assertThat(memberFunctions.getByClubId(removedClubId).size(), equalTo(0)),
                () -> assertThat(approvements.size(), equalTo(0)),
                () -> assertThat(answers.size(), equalTo(0)));
    }

    @Test
    @DisplayName("Проверка удаления несуществующего клуба")
    void removeNonExistingClub() {
        givenStep();

        var error = toGraphqlErrorResponse(() -> clubControllerApi.removeClub(UUID.randomUUID(), testTokenConfig.getToken()));

        var errorCode = "UNAUTHORIZED";
        var errorMessage = "You are not become member of given club!";

        checkGraphqlError(error, errorCode, errorMessage);
    }

    @Step
    @Title("GIVEN: Получены данные администратора удаляемого клуба")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Вся информация о клубе успешно удалена")
    public void thenStep() {
    }
}
