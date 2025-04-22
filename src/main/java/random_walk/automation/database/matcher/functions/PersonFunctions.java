package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.Person;
import random_walk.automation.database.matcher.repos.PersonRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonFunctions {

    private final PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person getPersonInfo(UUID id) {
        return personRepository.findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + id));
    }
}
