package random_walk;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import random_walk.automation.Application;
import random_walk.automation.api.auth.service.AuthServiceApi;
import random_walk.automation.api.club.services.ClubTestControllerApi;
import random_walk.automation.api.matcher.service.TestControllerApi;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.database.auth.entities.AuthUser;
import random_walk.automation.database.auth.entities.RefreshToken;
import random_walk.automation.database.auth.entities.UserRole;
import random_walk.automation.database.auth.functions.AuthUserFunctions;
import random_walk.automation.database.auth.functions.RefreshTokenFunctions;
import random_walk.automation.database.auth.functions.UserRoleFunctions;
import random_walk.automation.domain.User;
import random_walk.automation.domain.enums.AuthType;
import random_walk.automation.service.ClubConfigService;
import random_walk.automation.service.UserConfigService;
import random_walk.extensions.RestAssuredExtension;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@ExtendWith(RestAssuredExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    @Autowired
    protected UserConfigService userConfigService;

    @Autowired
    protected TestTokenConfig testTokenConfig;

    @Autowired
    protected ClubConfigService clubConfigService;

    @Autowired
    private AuthUserFunctions authUserFunctions;

    @Autowired
    private RefreshTokenFunctions refreshTokenFunctions;

    @Autowired
    private UserRoleFunctions userRoleFunctions;

    @Autowired
    private TestControllerApi testControllerApi;

    @Autowired
    private ClubTestControllerApi clubTestControllerApi;

    @Autowired
    private AuthServiceApi api;

    @BeforeAll
    public void generateUsers() {
        var users = userConfigService.getUsers();
        for (User user : users) {
            if (user.getUuid() == null) {
                UUID userId = UUID.randomUUID();
                String userEmail = "autotestuser%s@rv.com".formatted(userId);
                AuthUser authUser = new AuthUser().setAuthType(AuthType.GOOGLE)
                        .setId(userId)
                        .setEmail(userEmail)
                        .setUsername(userEmail)
                        .setEnabled(true);
                UUID userRefreshToken = UUID.randomUUID();
                RefreshToken refreshToken = new RefreshToken().setUserId(userId)
                        .setToken(userRefreshToken)
                        .setExpiresAt(LocalDateTime.now().plusDays(7));
                UserRole userRole = new UserRole().setUserId(userId).setRoleId(1);

                authUserFunctions.save(authUser);
                userRoleFunctions.save(userRole);
                refreshTokenFunctions.save(refreshToken);
                user.setUuid(userId);
                user.setRefresh(userRefreshToken);
                var map = userConfigService.getUsersTokens();
                map.put(user.getRole(), api.refreshAuthToken(userRefreshToken.toString()).getAccessToken());

                var fullName = "Autotest" + new Random().nextInt(1, 1000);
                testControllerApi.addUserInMatcher(userId, fullName);
                clubTestControllerApi.addMemberInClubService(userId, fullName);
            }
        }
    }
}
