package random_walk.automation.database.club.entities;

import club_service.graphql.model.ApprovementType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@Table(name = "approvement")
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Approvement {

    @Id
    private UUID id;

    @Column(name = "club_id")
    private UUID clubId;

    @Enumerated(EnumType.STRING)
    private ApprovementType type;

    @Type(type = "jsonb")
    private String data;

}
