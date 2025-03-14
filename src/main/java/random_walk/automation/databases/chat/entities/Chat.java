package random_walk.automation.databases.chat.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "chat")
@NoArgsConstructor
public class Chat {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "type", nullable = false)
    private String type;
}
