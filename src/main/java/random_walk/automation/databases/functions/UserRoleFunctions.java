package random_walk.automation.databases.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.databases.entities.UserRole;
import random_walk.automation.databases.repos.UserRoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleFunctions {
    private final UserRoleRepository userRoleRepository;

    public List<UserRole> getAll() {
        return userRoleRepository.findAll();
    }
}
