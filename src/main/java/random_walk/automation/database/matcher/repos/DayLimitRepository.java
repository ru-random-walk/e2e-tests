package random_walk.automation.database.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import random_walk.automation.database.matcher.entities.DayLimit;
import random_walk.automation.database.matcher.entities.prkeys.DayLimitPK;

public interface DayLimitRepository extends JpaRepository<DayLimit, DayLimitPK> {}
