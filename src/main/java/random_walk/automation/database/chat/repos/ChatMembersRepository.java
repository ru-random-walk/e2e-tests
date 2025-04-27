package random_walk.automation.database.chat.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import random_walk.automation.database.chat.entities.ChatMembers;

import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;

@Repository
public interface ChatMembersRepository extends JpaRepository<ChatMembers, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM chat_members WHERE chat_id = ?1")
    List<ChatMembers> getAllByChatId(UUID chatId);

    List<ChatMembers> findAllByUserId(UUID userId);

    @Query(nativeQuery = true, value = "SELECT * FROM chat_members WHERE user_id = ?1")
    List<ChatMembers> getClubsByUserId(UUID userId);

    @Transactional
    void deleteByChatId(UUID chatId);
}
