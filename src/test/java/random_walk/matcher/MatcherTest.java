package random_walk.matcher;

import org.junit.jupiter.api.Tag;
import random_walk.BaseTest;
import ru.random_walk.swagger.matcher_service.model.ClubDto;

import java.util.List;
import java.util.UUID;

@Tag("matcher-e2e")
public class MatcherTest extends BaseTest {

    protected static List<ClubDto> toListClubDto(List<UUID> personClubs) {
        if (personClubs == null)
            return List.of();
        return personClubs.stream().map(club -> new ClubDto().id(club)).toList();
    }
}
