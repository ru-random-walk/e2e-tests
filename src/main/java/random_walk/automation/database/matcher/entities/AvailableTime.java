package random_walk.automation.database.matcher.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "available_time")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class AvailableTime {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "person_id")
    private UUID personId;

    @Column(name = "time_from", nullable = false)
    private OffsetTime timeFrom;

    @Column(name = "time_until", nullable = false)
    private OffsetTime timeUntil;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    private String location;

    @Column(name = "search_area_meters")
    private Integer searchAreaMeters;

    @Type(type = "jsonb")
    @Column(name = "clubs_in_filter")
    private List<UUID> clubsInFilter;

    private String city;

    private String street;

    private String building;

}
