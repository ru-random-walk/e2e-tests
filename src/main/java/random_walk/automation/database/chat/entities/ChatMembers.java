package random_walk.automation.database.chat.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import random_walk.automation.database.chat.entities.prkeys.ChatMembersPrkey;

import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@Table(name = "chat_members")
@NoArgsConstructor
@IdClass(ChatMembersPrkey.class)
public class ChatMembers {

    @Id
    @Column(name = "chat_id", nullable = false)
    private UUID chatId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
