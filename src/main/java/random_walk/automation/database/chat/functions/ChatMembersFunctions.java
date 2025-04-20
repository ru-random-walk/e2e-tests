package random_walk.automation.database.chat.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.chat.entities.ChatMembers;
import random_walk.automation.database.chat.repos.ChatMembersRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMembersFunctions {

    private final ChatMembersRepository chatMembersRepository;

    public List<ChatMembers> getMembersByChatId(UUID id) {
        return chatMembersRepository.getAllByChatId(id);
    }

    public List<ChatMembers> getChatsByUserId(UUID userId) {
        return chatMembersRepository.findAllByUserId(userId);
    }

    public UUID getUsersChat(UUID firstUser, UUID secondUser) {
        var firstUserChats = getChatsByUserId(firstUser).stream().map(ChatMembers::getChatId).toList();
        var secondUserChats = getChatsByUserId(secondUser).stream().map(ChatMembers::getChatId).toList();
        return firstUserChats.stream().filter(secondUserChats::contains).findFirst().orElse(null);
    }
}
