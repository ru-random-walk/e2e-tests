package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Club;
import random_walk.automation.database.club.repos.ClubRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubFunctions {

    private final ClubRepository clubRepository;

    @Step
    @Title("AND: Получена информация о клубе из базы данных")
    @Description("Запись из таблицы club по id = {id}")
    public Club getById(UUID id) {
        return clubRepository.findById(id).orElse(null);
    }
}
