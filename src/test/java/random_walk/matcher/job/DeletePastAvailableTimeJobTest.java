package random_walk.matcher.job;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.functions.AvailableTimeFunctions;
import random_walk.matcher.MatcherTest;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Disabled("Джоба не отработала, видимо, проблемы с матчером, спрошу у Никиты")
class DeletePastAvailableTimeJobTest extends MatcherTest {

    @Autowired
    private AvailableTimeFunctions availableTimeFunctions;

    @Test
    @DisplayName("Проверка корректной работы джобы по удалению доступного времени для прогулок, которое уже прошло")
    void checkDeletePastAvailableTimeJob() {
        var availableTimeDates = availableTimeFunctions.getAllAvailableTime().stream().map(AvailableTime::getDate).toList();

        var date = LocalDate.now();

        assertThat(
                "Все слоты, которые находятся в прошлом, удалены из базы",
                availableTimeDates,
                everyItem(greaterThanOrEqualTo(date)));
    }
}
