package random_walk.automation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.chat.functions.ChatFunctions;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.database.chat.functions.MessageFunctions;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMembersFunctions chatMembersFunctions;

    private final ChatFunctions chatFunctions;

    private final MessageFunctions messageFunctions;

    public void deleteChatBetweenUsers(UUID firstUser, UUID secondUser) {
        var chatId = chatMembersFunctions.deleteChatMembers(firstUser, secondUser);
        messageFunctions.deleteMessagesByChatId(chatId);
        chatFunctions.deleteByChatId(chatId);
    }
}
