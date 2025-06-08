package random_walk.matcher.job;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.matcher.MatcherTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class DeletePastAvailableTimeJobTest extends MatcherTest {

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    @Test
    @DisplayName("Проверка корректной работы джобы по удалению доступного времени для прогулок, которое уже прошло")
    void checkDeletePastAvailableTimeJob() {
        givenStep();

        var availableTimeDates = availableTimeFunctions.getAllAvailableTime().stream().map(AvailableTime::getDate).toList();

        var date = LocalDate.now();

        thenStep();

        assertThat(
                "Все слоты, которые находятся в прошлом, удалены из базы",
                availableTimeDates,
                everyItem(greaterThanOrEqualTo(date)));
    }

    @Step
    @Title("GIVEN: Проверяем джобу по удалению свободного времени, срок которого истекает")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Записи успешно получены")
    public void thenStep() {
    }
}
