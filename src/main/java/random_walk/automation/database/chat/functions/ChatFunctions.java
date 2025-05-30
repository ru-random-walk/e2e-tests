package random_walk.automation.database.chat.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import random_walk.automation.database.chat.entities.Chat;
import random_walk.automation.database.chat.repos.ChatRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatFunctions {

    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    @Transactional(transactionManager = "chatTransactionManager")
    public void deleteByChatId(UUID chatId) {
        chatRepository.deleteById(chatId);
    }
}
