package random_walk.automation.database.auth.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.auth.entities.Role;
import random_walk.automation.database.auth.repos.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleFunctions {
    private final RoleRepository roleRepository;

    public List<Role> getAllData() {
        return roleRepository.findAllData();
    }
}
