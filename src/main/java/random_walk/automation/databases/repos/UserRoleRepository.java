package random_walk.automation.databases.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import random_walk.automation.databases.entities.UserRole;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    @Query(nativeQuery = true, value = "select * from auth_db.user_role")
    List<UserRole> findAll();
}
