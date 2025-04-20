package random_walk.automation.database.chat.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import random_walk.automation.websocket.model.Payload;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "message")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@NoArgsConstructor
public class Message {

    @Id
    private UUID id;

    @Type(type = "jsonb")
    @Column(name = "payload", nullable = false)
    private Payload payload;

    @Column(name = "chat_id", nullable = false)
    private UUID chatId;

    private UUID sender;

    private UUID recipient;

    @Column(name = "marked_as_read")
    private Boolean markedAsRead;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
