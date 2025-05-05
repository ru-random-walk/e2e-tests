package random_walk.automation.database.matcher.entities.prkeys;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class DayLimitPK implements Serializable {

    private UUID personId;

    private LocalDate date;
}
