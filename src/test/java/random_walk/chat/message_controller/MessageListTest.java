package random_walk.chat.message_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.chat.functions.MessageFunctions;
import random_walk.chat.ChatTest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.PaginationAsserts.checkPagination;

class MessageListTest extends ChatTest {

    @Autowired
    private MessageFunctions messageFunctions;

    @Test
    @DisplayName("Получение списка всех сообщений из чата")
    void getMessageListFromChatWithoutFilters() {
        step("GIVEN: Получены пользователи чата");

        var messageList = step(
                "WHEN: Получаем список сообщений для чата " + chatId,
                () -> chatApi.getChatMessageList(chatId, null, null, 100, null, null, null));

        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageList.getContent().size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), messageFunctions.getMessagesByChatId(chatId)));
    }

    @ParameterizedTest(name = "= {0}")
    @ValueSource(strings = { "Hi, Тест!", "Hi, Автотест!", "Привет" })
    @DisplayName("Получение списка сообщений, отфильтрованных по отправленному сообщению")
    void getMessageListFromChatByMessage(String message) {
        step("GIVEN: Получены пользователи чата");

        var messageList = step(
                "WHEN: Получаем список сообщений для чата %s по сообщению %s".formatted(chatId, message),
                () -> chatApi.getChatMessageList(chatId, null, null, 100, null, null, message));

        var messageListFilterByMessage = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getPayload().getText().equals(message))
                .toList();
        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageListFilterByMessage.size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), messageListFilterByMessage));
    }

    @Test
    @DisplayName("Получение списка сообщений, отфильтрованных по дате, с момента которой были отправлены")
    void getMessageListFromChatByDateFrom() {
        step("GIVEN: Получены пользователи чата");

        var dateFrom = LocalDateTime.now().minusMonths(1);
        var messageList = step(
                "WHEN: Получаем список сообщений для чата %s с даты %s".formatted(chatId, dateFrom),
                () -> chatApi.getChatMessageList(chatId, dateFrom, null, 100, null, null, null));

        var messageListFilterByDateFrom = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getSentAt().isAfter(dateFrom))
                .toList();
        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageList.getContent().size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), messageListFilterByDateFrom));
    }

    @Test
    @DisplayName("Получение списка сообщений, отфильтрованных по дате, до которой были отправлены сообщения")
    void getMessageListFromChatByDateTo() {
        step("GIVEN: Получены пользователи чата");

        var dateTo = LocalDateTime.now().minusDays(1);
        var messageList = step(
                "WHEN: Получаем список сообщений для чата %s до даты %s".formatted(chatId, dateTo),
                () -> chatApi.getChatMessageList(chatId, null, dateTo, 100, null, null, null));

        var messageListFilterByDateFrom = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getSentAt().isBefore(dateTo))
                .toList();
        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageList.getContent().size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), messageListFilterByDateFrom));
    }

    private static Stream<Arguments> getDates() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().minusMonths(1), LocalDateTime.now()),
                Arguments.of(LocalDateTime.now(), LocalDateTime.now().minusMonths(1)),
                Arguments.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1)));
    }

    @ParameterizedTest(name = "с {0} по {1}")
    @MethodSource("getDates")
    @DisplayName("Получение списка сообщений, отфильтрованных по временному диапазону отправки")
    void getMessageListFromChatByDateFromAndDateTo(LocalDateTime dateFrom, LocalDateTime dateTo) {
        step("GIVEN: Получены пользователи чата");

        var messageList = step(
                "WHEN: Получаем список сообщений для чата %s в промежуток с %s по %s".formatted(chatId, dateFrom, dateTo),
                () -> chatApi.getChatMessageList(chatId, dateFrom, dateTo, 100, null, null, null));

        var messageListFilterByDateFromAndDateTo = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getSentAt().isBefore(dateTo) && dbMessage.getSentAt().isAfter(dateFrom))
                .toList();
        assertAll(
                "THEN: Проверяем содержимое ответа",
                () -> checkPagination(messageList.getContent().size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), messageListFilterByDateFromAndDateTo));
    }
}
