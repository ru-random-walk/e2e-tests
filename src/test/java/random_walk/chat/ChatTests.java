package random_walk.chat;

import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.chat.services.ChatApi;

public class ChatTests extends BaseTest {

    @Autowired
    private ChatApi chatApi;
}
