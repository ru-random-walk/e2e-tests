package random_walk.automation.database.auth.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;

    private UUID token;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
