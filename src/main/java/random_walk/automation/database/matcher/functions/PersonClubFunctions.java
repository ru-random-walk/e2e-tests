package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.PersonClub;
import random_walk.automation.database.matcher.repos.PersonClubRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonClubFunctions {

    private final PersonClubRepository personClubRepository;

    @Step
    @Title("AND: Получаем список клубов, в которых состоит пользователь, из базы данных")
    @Description("Записи из таблицы person_club по person_id = {personId}")
    public List<UUID> getUserClubs(UUID personId) {
        var personClubs = personClubRepository.findByPersonId(personId);
        log.info("Для пользователя {} найдены клубы {}", personId, personClubs);
        return personClubs.stream().map(PersonClub::getClubId).toList();
    }
}
