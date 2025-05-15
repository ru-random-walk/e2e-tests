package random_walk.automation.database.club.entities.prkeys;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class MemberPK implements Serializable {

    private UUID id;

    private UUID clubId;
}
