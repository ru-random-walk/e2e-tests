package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.Person;
import random_walk.automation.database.matcher.repos.PersonRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonFunctions {

    private final PersonRepository personRepository;

    @Step
    @Title("AND: Получаем информацию по пользователю из базы данных")
    @Description("Запись из таблицы person по id = {id}")
    public Person getPersonInfo(UUID id) {
        return personRepository.findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + id));
    }
}
