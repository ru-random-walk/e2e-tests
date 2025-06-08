package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Approvement;
import random_walk.automation.database.club.repos.ApprovementRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovementFunctions {

    private final ApprovementRepository approvementRepository;

    @Step
    @Title("AND: Получены формы, отправленные пользователями, для определенного клуба")
    @Description("Записи из таблицы approvement по club_id = {clubId}")
    public List<Approvement> getByClubId(UUID clubId) {
        return approvementRepository.findByClubId(clubId);
    }
}
