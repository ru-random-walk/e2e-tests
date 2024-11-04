package random_walk.automation.databases.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import random_walk.automation.databases.entities.Role;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query(nativeQuery = true, value = "select * from auth_db.role")
    List<Role> findAllData();
}
