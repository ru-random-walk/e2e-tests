package random_walk.automation.database.auth.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import random_walk.automation.database.auth.entities.AuthUser;
import random_walk.automation.database.auth.repos.AuthUserRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

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

    @Step
    @Title("AND: Получена информация о пользователе из базы данных")
    @Description("Получаем запись из auth_user по id = {id}")
    public AuthUser getById(UUID id) {
        return authUserRepository.findById(id).orElse(null);
    }

    @Transactional(transactionManager = "authTransactionManager")
    public void save(AuthUser authUser) {
        authUserRepository.save(authUser);
    }
}
