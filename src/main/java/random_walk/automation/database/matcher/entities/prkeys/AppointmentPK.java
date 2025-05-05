package random_walk.automation.database.matcher.entities.prkeys;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class AppointmentPK implements Serializable {

    private UUID appointmentId;

    private UUID personId;
}
