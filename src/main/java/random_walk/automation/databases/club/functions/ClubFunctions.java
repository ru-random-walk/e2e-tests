package random_walk.automation.databases.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.databases.club.entities.Club;
import random_walk.automation.databases.club.repos.ClubRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubFunctions {

    private final ClubRepository clubRepository;

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }
}
