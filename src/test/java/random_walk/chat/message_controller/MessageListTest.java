package random_walk.chat.message_controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.chat.model.PagedModelMessage;
import random_walk.automation.database.chat.entities.Message;
import random_walk.automation.database.chat.functions.MessageFunctions;
import random_walk.chat.ChatTest;
import ru.testit.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.PaginationAsserts.checkPagination;

class MessageListTest extends ChatTest {

    @Autowired
    private MessageFunctions messageFunctions;

    @Test
    @ExternalId("chat_service.get_message_list")
    @DisplayName("Получение списка всех сообщений из чата")
    void getMessageListFromChatWithoutFilters() {
        givenStep();

        var messageList = chatApi.getChatMessageList(chatId, null, null, 100, null, null, null);

        var messagesWithoutFilters = messageFunctions.getMessagesByChatId(chatId);

        thenStep(messageList, messagesWithoutFilters);
    }

    @ParameterizedTest
    @ExternalId("chat_service.get_message_list_by_message_{message}")
    @ValueSource(strings = { "Hi, Тест!", "Hi, Автотест!", "Привет" })
    @DisplayName("Получение списка сообщений, отфильтрованных по отправленному сообщению = {message}")
    void getMessageListFromChatByMessage(String message) {
        givenStep();

        var messageList = chatApi.getChatMessageList(chatId, null, null, 100, null, null, message);

        var messageListFilterByMessage = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getPayload().getText().equals(message))
                .toList();

        thenStep(messageList, messageListFilterByMessage);
    }

    @Test
    @ExternalId("chat_service.get_message_list_by_date_from")
    @DisplayName("Получение списка сообщений, отфильтрованных по дате, с момента которой были отправлены")
    void getMessageListFromChatByDateFrom() {
        givenStep();

        var dateFrom = LocalDateTime.now().minusMonths(1);
        var messageList = chatApi.getChatMessageList(chatId, dateFrom, null, 100, null, null, null);

        var messageListFilterByDateFrom = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getSentAt().isAfter(dateFrom))
                .toList();

        thenStep(messageList, messageListFilterByDateFrom);
    }

    @Test
    @ExternalId("chat_service.get_message_list_by_date_to")
    @DisplayName("Получение списка сообщений, отфильтрованных по дате, до которой были отправлены сообщения")
    void getMessageListFromChatByDateTo() {
        givenStep();

        var dateTo = LocalDateTime.now().minusDays(1);
        var messageList = chatApi.getChatMessageList(chatId, null, dateTo, 100, null, null, null);

        var messageListFilterByDateFrom = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getSentAt().isBefore(dateTo))
                .toList();

        thenStep(messageList, messageListFilterByDateFrom);
    }

    private static Stream<Arguments> getDates() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().minusMonths(1), LocalDateTime.now(), "с date-1m до date"),
                Arguments.of(LocalDateTime.now(), LocalDateTime.now().minusMonths(1), "с date до date-1m"),
                Arguments.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), "с date-1d по date-1d"));
    }

    @ParameterizedTest
    @WorkItemIds({ "154", "156", "158" })
    @ExternalId("chat_service.get_message_list_by_date_interval")
    @MethodSource("getDates")
    @DisplayName("Получение списка сообщений, отфильтрованных по временному диапазону отправки {desc}")
    void getMessageListFromChatByDateFromAndDateTo(LocalDateTime dateFrom, LocalDateTime dateTo, String desc) {
        givenStep();

        var messageList = chatApi.getChatMessageList(chatId, dateFrom, dateTo, 100, null, null, null);

        var messageListFilterByDateFromAndDateTo = messageFunctions.getMessagesByChatId(chatId)
                .stream()
                .filter(dbMessage -> dbMessage.getSentAt().isBefore(dateTo) && dbMessage.getSentAt().isAfter(dateFrom))
                .toList();

        thenStep(messageList, messageListFilterByDateFromAndDateTo);
    }

    @Step
    @Title("GIVEN: Получены пользователи чата")
    void givenStep() {
    }

    @Step
    @Title("THEN: Сообщения из чата по указанным фильтрам успешно получены")
    void thenStep(PagedModelMessage messageList, List<Message> dbMessages) {
        assertAll(
                () -> checkPagination(messageList.getContent().size(), null, 100, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), dbMessages));
    }
}
