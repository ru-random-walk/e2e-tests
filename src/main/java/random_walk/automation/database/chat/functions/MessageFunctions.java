package random_walk.automation.database.chat.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.chat.entities.Message;
import random_walk.automation.database.chat.repos.MessageRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageFunctions {

    private final MessageRepository messageRepository;

    public Message getMessage(UUID id) {
        return messageRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<Message> getMessagesByChatId(UUID chatId) {
        return messageRepository.findAllByChatId(chatId);
    }
}
