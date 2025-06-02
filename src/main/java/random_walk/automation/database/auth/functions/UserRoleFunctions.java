package random_walk.automation.database.auth.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import random_walk.automation.database.auth.entities.UserRole;
import random_walk.automation.database.auth.repos.UserRoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleFunctions {
    private final UserRoleRepository userRoleRepository;

    public List<UserRole> getAll() {
        return userRoleRepository.findAll();
    }

    @Transactional(transactionManager = "authTransactionManager")
    public void save(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
