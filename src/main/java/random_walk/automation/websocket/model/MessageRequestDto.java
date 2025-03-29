package random_walk.automation.websocket.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import ru.random_walk.swagger.chat_service.model.TextPayload;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MessageRequestDto(
        TextPayload payload,
        UUID chatId,
        UUID sender,
        UUID recipient,
        @Schema(example = "18:00 22-09-2024") @DateTimeFormat(pattern = "HH:mm dd-MM-yyyy") LocalDateTime createdAt
) {}
