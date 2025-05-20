package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Club;
import random_walk.automation.database.club.repos.ClubRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubFunctions {

    private final ClubRepository clubRepository;

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club getById(UUID id) {
        return clubRepository.findById(id).orElse(null);
    }
}
