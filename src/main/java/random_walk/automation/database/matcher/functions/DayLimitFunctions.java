package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.DayLimit;
import random_walk.automation.database.matcher.entities.prkeys.DayLimitPK;
import random_walk.automation.database.matcher.repos.DayLimitRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

@Service
@RequiredArgsConstructor
public class DayLimitFunctions {

    private final DayLimitRepository dayLimitRepository;

    @Step
    @Title("AND: Получены лимиты пользователя на количество прогулок в день из базы данных")
    @Description("Получена запись из таблицы day_limit по ключу = {dayLimitPK}")
    public DayLimit getById(DayLimitPK dayLimitPK) {
        return dayLimitRepository.findById(dayLimitPK).orElse(null);
    }

    @Step
    @Title("AND: Изменяем лимиты на количество встреч в день в базе данных")
    @Description("Изменена запись в таблице day_limit с ключом = {dayLimitPK}")
    public void setDayLimitByDateAndPersonId(DayLimitPK dayLimitPK) {
        dayLimitRepository.setDayLimitById(dayLimitPK);
    }
}
