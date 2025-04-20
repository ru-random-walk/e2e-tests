package random_walk.chat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import random_walk.BaseTest;
import random_walk.automation.api.chat.model.PagedModelMessage;
import random_walk.automation.api.chat.service.ChatApi;
import random_walk.automation.database.chat.entities.Message;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.UserRoleEnum;
import random_walk.automation.websocket.WebsocketApi;
import ru.random_walk.swagger.chat_service.model.MessageDtoPayload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("chat-e2e")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ChatTest extends BaseTest {

    @Autowired
    protected ChatApi chatApi;

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Autowired
    private WebsocketApi websocketApi;

    protected UUID chatId;

    protected User firstUser;

    protected User secondUser;

    private Boolean isCalled = false;

    @BeforeAll
    public void createChatAndMessages() {
        if (!isCalled) {
            firstUser = userConfigService.getUserByRole(UserRoleEnum.TEST_USER);
            secondUser = userConfigService.getUserByRole(UserRoleEnum.AUTOTEST_USER);
            chatId = chatMembersFunctions.getUsersChat(firstUser.getUuid(), secondUser.getUuid());

            if (chatId == null) {
                chatApi.createPrivateChatEvent(firstUser.getUuid(), secondUser.getUuid());
                chatId = chatMembersFunctions.getUsersChat(firstUser.getUuid(), secondUser.getUuid());
            }

            StompSession firstUserSession = websocketApi.connect(chatId, testTokenConfig.getToken());
            StompSession secondUserSession = websocketApi.connect(chatId, testTokenConfig.getAutotestToken());

            websocketApi.sendMessage(
                    "Hi, %s!".formatted(secondUser.getName()),
                    firstUserSession,
                    chatId,
                    firstUser.getUuid(),
                    secondUser.getUuid(),
                    LocalDateTime.now().minusMonths(1));
            websocketApi.sendMessage(
                    "Hi, %s!".formatted(firstUser.getName()),
                    secondUserSession,
                    chatId,
                    secondUser.getUuid(),
                    firstUser.getUuid(),
                    LocalDateTime.now().minusDays(1));
            websocketApi.sendMessage(
                    "Hi, %s!".formatted(firstUser.getName()),
                    secondUserSession,
                    chatId,
                    secondUser.getUuid(),
                    firstUser.getUuid(),
                    LocalDateTime.now());
            isCalled = true;
        }
    }

    protected static void checkMessageList(List<PagedModelMessage.MessageDto> givenList, List<Message> databaseList) {
        List<PagedModelMessage.MessageDto> expectedList = new ArrayList<>();
        databaseList.forEach(
                databaseMessage -> expectedList.add(
                        new PagedModelMessage.MessageDto().setChatId(databaseMessage.getChatId())
                                .setId(databaseMessage.getId())
                                .setPayload(
                                        new MessageDtoPayload().text(databaseMessage.getPayload().getText())
                                                .type(databaseMessage.getPayload().getType()))
                                .setMarkedAsRead(databaseMessage.getMarkedAsRead())
                                .setSender(databaseMessage.getSender())
                                .setCreatedAt(
                                        databaseMessage.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")))));

        assertTrue(givenList.containsAll(expectedList), "Список собщений соответствует списку из базы");
    }
}
