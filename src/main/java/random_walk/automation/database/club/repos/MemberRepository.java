package random_walk.automation.database.club.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import random_walk.automation.database.club.entities.Member;
import random_walk.automation.database.club.entities.prkeys.MemberPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, MemberPK> {

    List<Member> findByClubId(UUID clubId);
}
