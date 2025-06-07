package random_walk.chat.message_controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.chat.model.PagedModelMessage;
import random_walk.automation.database.chat.entities.Message;
import random_walk.automation.database.chat.functions.MessageFunctions;
import random_walk.chat.ChatTest;
import ru.testit.annotations.DisplayName;
import ru.testit.annotations.ExternalId;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.asserts.PaginationAsserts.checkPagination;

class MessageListPaginationTest extends ChatTest {

    @Autowired
    private MessageFunctions messageFunctions;

    @Test
    @ExternalId("chat_service.get_message_list_with_size")
    @DisplayName("Получение списка сообщений из чата с учетом ограничения size")
    void getMessageListBySize() {
        givenStep();

        var size = 2;
        var messageList = chatApi.getChatMessageList(chatId, null, null, size, null, null, null);

        var messageListDatabase = messageFunctions.getMessagesByChatId(chatId);

        thenStep(messageList, size, null, messageListDatabase, messageListDatabase.stream().limit(size).toList());

    }

    @ParameterizedTest(name = "{0}")
    @ExternalId("chat_service.get_message_list_in_page_{page}")
    @ValueSource(ints = { 0, 1 })
    @DisplayName("Получение списка сообщений из чата с учетом ограничения page =")
    void getMessageListByPage(Integer page) {
        givenStep();

        var size = 1;
        var messageList = chatApi.getChatMessageList(chatId, null, null, size, page, null, null);

        var messageListDatabase = messageFunctions.getMessagesByChatId(chatId);

        thenStep(messageList, size, page, messageListDatabase, List.of(messageListDatabase.get(page)));
    }

    @ParameterizedTest(name = "в порядке {0}")
    @ExternalId("chat_service.get_message_by_{sort}_sort")
    @ValueSource(strings = { "asc", "desc" })
    @DisplayName("Получение списка сообщений из чата с учетом сортировки по тексту сообщения")
    void getMessageListByMessageSort(String sort) {
        givenStep();

        var sortParam = "payload," + sort;
        var messageList = chatApi.getChatMessageList(chatId, null, null, 100, null, sortParam, null);

        var messageListDatabase = messageFunctions.getMessagesByChatId(chatId);

        var filteredMessages = sort.equals("asc")
                ? messageListDatabase.stream()
                        .sorted(Comparator.comparing(a -> a.getPayload().getText(), Comparator.naturalOrder()))
                        .toList()
                : messageListDatabase.stream()
                        .sorted(Comparator.comparing(a -> a.getPayload().getText(), Comparator.reverseOrder()))
                        .toList();

        thenStep(messageList, 100, null, messageListDatabase, filteredMessages);
    }

    @Step
    @Title("GIVEN: Получены пользователи чата")
    public void givenStep() {
    }

    @Step
    @Title("THEN: Сообщения из чата по указанным фильтрам успешно получены")
    public void thenStep(PagedModelMessage messageList,
                         Integer size,
                         Integer page,
                         List<Message> messageListDatabase,
                         List<Message> filteredMessages) {
        assertAll(
                () -> checkPagination(messageListDatabase.size(), page, size, messageList.getPage()),
                () -> checkMessageList(messageList.getContent(), filteredMessages));
    }
}
