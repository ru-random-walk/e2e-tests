package random_walk.automation.database.chat.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;
import random_walk.automation.database.chat.entities.ChatMembers;
import random_walk.automation.database.chat.repos.ChatMembersRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.info("Для пользователя {} список доступных чатов - {}", firstUser, firstUserChats);
        log.info("Для пользователя {} список доступных чатов - {}", secondUser, secondUserChats);
        return firstUserChats.stream().filter(secondUserChats::contains).findFirst().orElse(null);
    }

    public UUID deleteChatMembers(UUID firstUser, UUID secondUser) {
        var firstUserChats = chatMembersRepository.getClubsByUserId(firstUser)
                .stream()
                .map(ChatMembers::getChatId)
                .collect(Collectors.toSet());
        var chatId = chatMembersRepository.getClubsByUserId(secondUser)
                .stream()
                .map(ChatMembers::getChatId)
                .filter(firstUserChats::contains)
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException(
                                "Не найден общий чат для пользователей %s и %s".formatted(firstUser, secondUser)));
        log.info("Удаляем чат {} для переданных пользователей", chatId);
        chatMembersRepository.deleteByChatId(chatId);
        return chatId;
    }
}
