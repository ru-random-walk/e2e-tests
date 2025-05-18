package random_walk.automation.database.club.entities;

import club_service.graphql.model.AnswerStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@Table(name = "answer")
@NoArgsConstructor
public class Answer {

    @Id
    private UUID id;

    @Column(name = "approvement_id")
    private UUID approvementId;

    @Column(name = "user_id")
    private UUID userId;

    private String data;

    @Enumerated(EnumType.STRING)
    private AnswerStatus status;
}
