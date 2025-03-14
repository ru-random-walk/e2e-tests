package random_walk.automation.databases.chat.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.databases.chat.entities.Chat;
import random_walk.automation.databases.chat.repos.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatFunctions {

    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
}
