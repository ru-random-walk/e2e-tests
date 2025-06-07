package random_walk.automation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.config.ClubsConfig;
import random_walk.automation.domain.UserClub;
import random_walk.automation.domain.enums.ClubRole;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClubConfigService {

    private final ClubsConfig config;

    public UserClub getClubByRole(ClubRole role) {
        return config.getClubs()
                .stream()
                .filter(club -> Objects.equals(club.getRole(), role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Клуб с ролью " + role + " не найден"));
    }

    public List<UserClub> getClubs() {
        return config.getClubs();
    }
}
