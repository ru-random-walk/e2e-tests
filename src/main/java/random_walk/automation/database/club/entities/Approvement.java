package random_walk.automation.database.club.entities;

import club_service.graphql.model.ApprovementType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@Table(name = "approvement")
@NoArgsConstructor
public class Approvement {

    @Id
    private UUID id;

    @Column(name = "club_id")
    private UUID clubId;

    @Enumerated(EnumType.STRING)
    private ApprovementType type;

    private String data;

}
