package random_walk.automation.database.club.entities;

import club_service.graphql.model.MemberRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import random_walk.automation.database.club.entities.prkeys.MemberPK;

import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@Table(name = "member")
@IdClass(MemberPK.class)
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Id
    @Column(name = "club_id", nullable = false)
    private UUID clubId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

}
