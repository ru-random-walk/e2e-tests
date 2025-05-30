package random_walk.automation.database.auth.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.auth.entities.AuthUser;
import random_walk.automation.database.auth.repos.AuthUserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUserFunctions {

    private final AuthUserRepository authUserRepository;

    public List<AuthUser> getAllUsers() {
        return authUserRepository.findAllData();
    }

    public AuthUser getUserByFullName(String fullName) {
        return authUserRepository.findByFullName(fullName);
    }

    public AuthUser getById(UUID id) {
        return authUserRepository.findById(id).orElse(null);
    }
}
