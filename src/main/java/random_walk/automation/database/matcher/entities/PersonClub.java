package random_walk.automation.database.matcher.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import random_walk.automation.database.matcher.entities.prkeys.PersonClubPK;

import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@Table(name = "person_club")
@NoArgsConstructor
@IdClass(PersonClubPK.class)
public class PersonClub {

    @Id
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Id
    @Column(name = "club_id", nullable = false)
    private UUID clubId;
}
