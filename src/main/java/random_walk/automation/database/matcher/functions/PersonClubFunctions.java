package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.PersonClub;
import random_walk.automation.database.matcher.repos.PersonClubRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonClubFunctions {

    private final PersonClubRepository personClubRepository;

    public List<UUID> getUserClubs(UUID personId) {
        return personClubRepository.findByPersonId(personId).stream().map(PersonClub::getClubId).toList();
    }
}
