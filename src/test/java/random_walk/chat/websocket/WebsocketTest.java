package random_walk.chat.websocket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.websocket.WebsocketApi;
import random_walk.chat.ChatTest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class WebsocketTest extends ChatTest {

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Autowired
    private WebsocketApi websocketApi;

    private UUID chatId;

    public static final String FIRST_MESSAGE = "Hello!";

    public static final String SECOND_MESSAGE = "How are you?";

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
    @DisplayName("Проверка корректной отправки сообщений через вебсокет")
    void checkWebsocket() {
        step("Получены данные о пользователях и чате между ними");

        var chatMessages = chatApi.getAutotestChatMessageList(chatId, null, null, null, null, null, null);

        assertAll(
                "Проверяем, что сообщения были отправлены",
                () -> assertThat("Количество сообщений соответствует ожидаемому", chatMessages.getContent().size(), equalTo(2)),
                () -> assertThat(
                        "Текст сообщений соответствует отправленному",
                        chatMessages.getContent().stream().map(a -> a.getPayload().getText()).toList(),
                        containsInAnyOrder(Stream.of(FIRST_MESSAGE, SECOND_MESSAGE).toArray())));
    }

}
