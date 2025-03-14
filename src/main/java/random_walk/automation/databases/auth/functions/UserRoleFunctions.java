package random_walk.automation.databases.auth.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.databases.auth.entities.UserRole;
import random_walk.automation.databases.auth.repos.UserRoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleFunctions {
    private final UserRoleRepository userRoleRepository;

    public List<UserRole> getAll() {
        return userRoleRepository.findAll();
    }
}
