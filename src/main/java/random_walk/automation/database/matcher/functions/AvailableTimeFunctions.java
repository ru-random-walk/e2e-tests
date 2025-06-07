package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.AvailableTime;
import random_walk.automation.database.matcher.repos.AvailableTimeRepository;
import random_walk.automation.util.PointConverterUtils;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailableTimeFunctions {

    private final AvailableTimeRepository availableTimeRepository;

    public AvailableTime getById(UUID id) {
        return availableTimeRepository.findById(id).orElse(null);
    }

    @Step
    @Title("AND: Получаем свободное время пользователя из базы данных")
    @Description("Записи из таблицы available_time по date = {date} и person_id = {personId}")
    public List<AvailableTime> getUserAvailableTimeByDateAndPersonId(LocalDate date, UUID personId) {
        return availableTimeRepository.findByDateAndPersonId(date, personId)
                .stream()
                .map(r -> r.setLocation(PointConverterUtils.convertToPoint(r.getLocation()).toString()))
                .toList();
    }

    public void deleteUserAvailableTime(UUID personId) {
        availableTimeRepository.deleteByPersonId(personId);
    }

    public void deleteById(UUID id) {
        availableTimeRepository.deleteById(id);
    }

    @Step
    @Title("AND: Получаем все записи о свободном времени из базы данных")
    @Description("Все записи из таблицы available_time")
    public List<AvailableTime> getAllAvailableTime() {
        return availableTimeRepository.findAll();
    }
}
