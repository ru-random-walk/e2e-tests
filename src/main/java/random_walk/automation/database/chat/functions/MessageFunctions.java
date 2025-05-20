package random_walk.automation.database.chat.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import random_walk.automation.database.chat.entities.Message;
import random_walk.automation.database.chat.repos.MessageRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageFunctions {

    private final MessageRepository messageRepository;

    public Message getMessage(UUID id) {
        return messageRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<Message> getMessagesByChatId(UUID chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    @Transactional(transactionManager = "chatTransactionManager")
    public void deleteMessagesByChatId(UUID chatId) {
        log.info("Удаляем все сообщения чата {}", chatId);
        messageRepository.deleteByChatId(chatId);
    }
}
