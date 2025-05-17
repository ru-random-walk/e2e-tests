package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Approvement;
import random_walk.automation.database.club.repos.ApprovementRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovementFunctions {

    private final ApprovementRepository approvementRepository;

    public List<Approvement> getByClubId(UUID clubId) {
        return approvementRepository.findByClubId(clubId);
    }
}
