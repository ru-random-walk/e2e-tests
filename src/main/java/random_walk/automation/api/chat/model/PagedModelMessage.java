package random_walk.automation.api.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.random_walk.swagger.chat_service.model.MessageDtoPayload;
import ru.random_walk.swagger.chat_service.model.PageMetadata;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PagedModelMessage {

    private List<MessageDto> content;

    private PageMetadata page;

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class MessageDto {
        private UUID id;

        private MessageDtoPayload payload;

        private UUID chatId;

        private Boolean markedAsRead;

        private String createdAt;

        private UUID sender;
    }
}
