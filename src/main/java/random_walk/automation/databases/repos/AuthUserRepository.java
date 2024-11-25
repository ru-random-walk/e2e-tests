package random_walk.automation.databases.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import random_walk.automation.databases.entities.AuthUser;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    @Query(nativeQuery = true, value = "select * from auth_db.auth_user")
    List<AuthUser> findAllData();

    @Query(nativeQuery = true, value = "select * from auth_db.auth_user " + "where full_name = ?1")
    AuthUser findByFullName(String fullName);
}
