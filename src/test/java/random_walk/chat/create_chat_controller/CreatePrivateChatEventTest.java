package random_walk.chat.create_chat_controller;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.automation.exception.model.DefaultErrorResponse;
import random_walk.chat.ChatTest;
import ru.testit.annotations.*;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class CreatePrivateChatEventTest extends ChatTest {

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Test
    @ExternalId("chat_service.create_chat")
    @WorkItemIds("124")
    @DisplayName("Создание чата для пользователей")
    void createChatForUsers() {
        try {
            chatService.deleteChatBetweenUsers(firstUser.getUuid(), thirdUser.getUuid());
        } catch (NotFoundException ignored) {
        }

        givenStep();

        var chatId = chatMembersFunctions.getUsersChat(firstUser.getUuid(), thirdUser.getUuid());

        chatApi.createPrivateChatEvent(firstUser.getUuid(), thirdUser.getUuid());

        var newChatId = chatMembersFunctions.getUsersChat(firstUser.getUuid(), thirdUser.getUuid());

        thenStepForCreatingChat(chatId, newChatId);
    }

    @Step
    @Title("THEN: Чат между пользователями корректно создан")
    void thenStepForCreatingChat(UUID chatId, UUID newChatId) {
        assertAll(
                () -> assertThat("Между пользователями не существовал чат до вызова запроса", chatId, nullValue()),
                () -> assertThat("После создания чат появляется в базе", newChatId, notNullValue()));
    }

    @Test
    @ExternalId("chat_service.create_second_chat")
    @WorkItemIds("126")
    @DisplayName("Получение ошибки создания второго чата между пользователями")
    void checkAttemptToCreateSecondChatBetweenUsers() {
        try {
            chatApi.createPrivateChatEvent(firstUser.getUuid(), thirdUser.getUuid());
        } catch (Exception ignored) {
        }

        givenStep();

        var chatId = toDefaultErrorResponse(() -> chatApi.createPrivateChatEvent(firstUser.getUuid(), thirdUser.getUuid()));

        thenStepForSecondTest(chatId, firstUser.getUuid(), thirdUser.getUuid());
    }

    @Step
    @Title("THEN: Попытка создания второго чата между пользователями {firstUserId} и {thirdUserId} завершилась ошибкой")
    void thenStepForSecondTest(DefaultErrorResponse chatId, UUID firstUserId, UUID thirdUserId) {
        assertThat(
                "Невозможно создать второй чат между пользователями",
                chatId.getMessage(),
                equalTo("Chat with members: [%s, %s] already exist!".formatted(firstUserId, thirdUserId)));
    }

    @Step
    @Title("GIVEN: Получены пользователи для создания чата между ними")
    void givenStep() {
    }
}
