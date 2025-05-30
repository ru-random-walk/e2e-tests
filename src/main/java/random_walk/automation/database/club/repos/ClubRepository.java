package random_walk.automation.database.club.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import random_walk.automation.database.club.entities.Club;

import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {}
