package random_walk.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.automation.api.chat.services.ChatApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatTests extends ChatTest {

    @Autowired
    private ChatApi chatApi;

    @Test
    void f() {
        var a = 1;
        assertEquals(1, a);
    }
}
