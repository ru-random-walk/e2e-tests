package random_walk.automation.database.matcher.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "appointment_details")
public class AppointmentDetails {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "starts_at")
    private OffsetDateTime startsAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private AppointmentDetailsDto.StatusEnum status;

    @Column(name = "approximate_location")
    private String approximateLocation;

    @Column(name = "requester_id")
    private UUID requesterId;

}
