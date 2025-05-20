package random_walk.automation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import random_walk.automation.domain.enums.ClubRole;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserClub {

    private UUID id;

    private ClubRole role;
}
