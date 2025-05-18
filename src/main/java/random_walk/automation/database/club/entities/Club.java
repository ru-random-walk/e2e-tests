package random_walk.automation.database.club.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "club")
@NoArgsConstructor
public class Club {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    private String name;

    private String description;

    @Column(name = "photo_version")
    private Integer photoVersion;
}
