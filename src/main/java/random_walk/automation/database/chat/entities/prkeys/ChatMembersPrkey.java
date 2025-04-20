package random_walk.automation.database.chat.entities.prkeys;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ChatMembersPrkey implements Serializable {

    private UUID chatId;

    private UUID userId;
}
