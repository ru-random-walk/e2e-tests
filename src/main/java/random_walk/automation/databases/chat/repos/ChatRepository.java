package random_walk.automation.databases.chat.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import random_walk.automation.databases.chat.entities.Chat;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {}
