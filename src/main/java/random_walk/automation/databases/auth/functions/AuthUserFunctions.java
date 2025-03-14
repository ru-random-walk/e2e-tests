package random_walk.automation.databases.auth.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.databases.auth.entities.AuthUser;
import random_walk.automation.databases.auth.repos.AuthUserRepository;

import java.util.List;

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
}
