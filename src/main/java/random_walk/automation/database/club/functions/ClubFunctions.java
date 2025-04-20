package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Club;
import random_walk.automation.database.club.repos.ClubRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubFunctions {

    private final ClubRepository clubRepository;

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }
}
