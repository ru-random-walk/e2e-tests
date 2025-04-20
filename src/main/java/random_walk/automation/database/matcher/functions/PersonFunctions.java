package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.Person;
import random_walk.automation.database.matcher.repos.PersonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonFunctions {

    private final PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
}
