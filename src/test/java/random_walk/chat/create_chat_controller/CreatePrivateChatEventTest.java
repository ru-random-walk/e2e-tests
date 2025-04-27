package random_walk.chat.create_chat_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.database.chat.functions.ChatMembersFunctions;
import random_walk.chat.ChatTest;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static random_walk.automation.util.ExceptionUtils.toDefaultErrorResponse;

class CreatePrivateChatEventTest extends ChatTest {

    @Autowired
    private ChatMembersFunctions chatMembersFunctions;

    @Test
    @DisplayName("Создание чата для пользователей")
    void createChatForUsers() {
        try {
            chatService.deleteChatBetweenUsers(firstUser.getUuid(), thirdUser.getUuid());
        } catch (NotFoundException ignored) {
        }

        step("GIVEN: Получены пользователи для создания чата между ними");

        var chatId = chatMembersFunctions.getUsersChat(firstUser.getUuid(), thirdUser.getUuid());

        chatApi.createPrivateChatEvent(firstUser.getUuid(), thirdUser.getUuid());

        var newChatId = chatMembersFunctions.getUsersChat(firstUser.getUuid(), thirdUser.getUuid());

        assertAll(
                "Чат между пользователями корректно создан",
                () -> assertThat("Между пользователями не существовал чат до вызова запроса", chatId, nullValue()),
                () -> assertThat("После создания чат появляется в базе", newChatId, notNullValue()));
    }

    @Test
    @DisplayName("Проверка невозможности создания второго чата между пользователями")
    void checkAttemptToCreateSecondChatBetweenUsers() {
        try {
            chatApi.createPrivateChatEvent(firstUser.getUuid(), thirdUser.getUuid());
        } catch (Exception ignored) {
        }

        step("GIVEN: Получены пользователи для создания чата между ними");

        var chatId = toDefaultErrorResponse(() -> chatApi.createPrivateChatEvent(firstUser.getUuid(), thirdUser.getUuid()));

        assertThat(
                "Невозможно создать второй чат между пользователями",
                chatId.getMessage(),
                equalTo("Chat with members: [%s, %s] already exist!".formatted(firstUser.getUuid(), thirdUser.getUuid())));
    }
}
