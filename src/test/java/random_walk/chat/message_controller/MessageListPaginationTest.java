package random_walk.chat.message_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.chat.functions.MessageFunctions;
import random_walk.chat.ChatTest;

import java.util.Comparator;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.PaginationAsserts.checkPagination;

class MessageListPaginationTest extends ChatTest {

    @Autowired
    private MessageFunctions messageFunctions;

    @Test
    @DisplayName("Получение списка сообщений из чата с учетом ограничения size")
    void getMessageListBySize() {
        step("GIVEN: Получены пользователи чата");

        var size = 2;
        var messageList = step(
                "WHEN: Получаем первые %s сообщений для чата %s".formatted(size, chatId),
                () -> chatApi.getChatMessageList(chatId, null, null, size, null, null, null));

        var messageListDatabase = messageFunctions.getMessagesByChatId(chatId);

        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageListDatabase.size(), null, size, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), messageListDatabase.stream().limit(size).toList()));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = { 0, 1 })
    @DisplayName("Получение списка сообщений из чата с учетом ограничения page =")
    void getMessageListByPage(Integer page) {
        step("GIVEN: Получены пользователи чата");

        var size = 1;
        var messageList = step(
                "WHEN: Получаем %s страницу для чата %s".formatted(page, chatId),
                () -> chatApi.getChatMessageList(chatId, null, null, size, page, null, null));

        var messageListDatabase = messageFunctions.getMessagesByChatId(chatId);

        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageListDatabase.size(), page, size, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), List.of(messageListDatabase.get(page))));
    }

    @ParameterizedTest(name = "в порядке {0}")
    @ValueSource(strings = { "asc", "desc" })
    @DisplayName("Получение списка сообщений из чата с учетом сортировки по тексту сообщения")
    void getMessageListByMessageSort(String sort) {
        step("GIVEN: Получены пользователи чата");

        var sortParam = "payload," + sort;
        var messageList = step(
                "WHEN: Получаем список сообщений с учетом сортировки по тексту сообщения для чата %s".formatted(chatId),
                () -> chatApi.getChatMessageList(chatId, null, null, 100, null, sortParam, null));

        var messageListDatabase = messageFunctions.getMessagesByChatId(chatId);

        var filteredMessages = sort.equals("asc")
                ? messageListDatabase.stream()
                        .sorted(Comparator.comparing(a -> a.getPayload().getText(), Comparator.naturalOrder()))
                        .toList()
                : messageListDatabase.stream()
                        .sorted(Comparator.comparing(a -> a.getPayload().getText(), Comparator.reverseOrder()))
                        .toList();

        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageListDatabase.size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), filteredMessages));
    }

}
