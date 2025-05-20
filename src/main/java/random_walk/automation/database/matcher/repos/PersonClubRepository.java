package random_walk.automation.database.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import random_walk.automation.database.matcher.entities.PersonClub;
import random_walk.automation.database.matcher.entities.prkeys.PersonClubPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonClubRepository extends JpaRepository<PersonClub, PersonClubPK> {

    List<PersonClub> findByPersonId(UUID personId);
}
