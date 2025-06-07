package random_walk.chat.websocket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import random_walk.automation.api.chat.model.PagedModelMessage;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.websocket.WebsocketApi;
import random_walk.chat.ChatTest;
import ru.testit.annotations.*;
import ru.testit.services.Adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebsocketTest extends ChatTest {

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Autowired
    private WebsocketApi websocketApi;

    private UUID chatId;

    public static final String FIRST_MESSAGE = "Hello!";

    public static final String SECOND_MESSAGE = "How are you?";

    @Step
    @Title("Очистка старого и создание нового пустого чата")
    @Description("Пользователи - 490689d5-4e63-4724-8ab5-4fb32750b263 и 3d154ea6-7d67-41f1-820f-5f6b403caec6")
    @BeforeAll
    public void createChatWithoutMessages() {
        if (chatMembersFunctions.getUsersChat(secondUser.getUuid(), thirdUser.getUuid()) != null) {
            chatService.deleteChatBetweenUsers(secondUser.getUuid(), thirdUser.getUuid());
        }

        chatApi.createPrivateChatEvent(secondUser.getUuid(), thirdUser.getUuid());

        chatId = chatMembersFunctions.getUsersChat(secondUser.getUuid(), thirdUser.getUuid());

        StompSession session = websocketApi.connect(chatId, testTokenConfig.getAutotestToken());

        websocketApi.sendMessage(
                FIRST_MESSAGE,
                session,
                testTokenConfig.getAutotestToken(),
                chatId,
                secondUser.getUuid(),
                thirdUser.getUuid(),
                LocalDateTime.now());

        websocketApi.sendMessage(
                SECOND_MESSAGE,
                session,
                testTokenConfig.getAutotestToken(),
                chatId,
                secondUser.getUuid(),
                thirdUser.getUuid(),
                LocalDateTime.now().plusSeconds(30));
    }

    @Test
    @ExternalId("chat_service.websocket")
    @WorkItemIds("122")
    @DisplayName("Проверка корректной отправки сообщений через вебсокет")
    void checkWebsocket() {
        Adapter.addMessage("Проверяем, что сообщения были отправлены");
        givenStep();

        var chatMessages = chatApi.getAutotestChatMessageList(chatId, null, null, null, null, null, null);

        thenStep(chatMessages);
    }

    @Step
    @Title("GIVEN: Получены данные пользователей и чат между ними")
    void givenStep() {
    }

    @Step
    @Title("THEN: Сообщения успешно получены")
    void thenStep(PagedModelMessage chatMessages) {
        assertEquals(2, chatMessages.getContent().size(), "Количество сообщений соответствует ожидаемому");
        assertEquals(
                List.of(FIRST_MESSAGE, SECOND_MESSAGE).stream().sorted().toList(),
                chatMessages.getContent().stream().map(a -> a.getPayload().getText()).sorted().toList(),
                "Текст сообщений соответствует отправленному");
    }
}
