package random_walk.automation.databases.matcher.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import random_walk.automation.databases.matcher.entities.Person;

import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {}
