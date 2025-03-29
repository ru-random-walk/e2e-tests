package random_walk;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import random_walk.automation.Application;
import random_walk.automation.api.auth.services.AuthServiceApi;
import random_walk.automation.databases.auth.functions.AuthUserFunctions;
import random_walk.automation.databases.auth.functions.RefreshTokenFunctions;
import random_walk.automation.databases.auth.functions.RoleFunctions;
import random_walk.automation.databases.auth.functions.UserRoleFunctions;
import random_walk.automation.databases.chat.functions.ChatFunctions;
import random_walk.automation.databases.club.functions.ClubFunctions;
import random_walk.automation.databases.matcher.functions.PersonFunctions;
import random_walk.extensions.RestAssuredExtension;

@ExtendWith(RestAssuredExtension.class)
//@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = Application.class)
public abstract class BaseTest {

    @Autowired
    protected AuthServiceApi authServiceApi;

    @Autowired
    protected RoleFunctions roleFunctions;

    @Autowired
    protected AuthUserFunctions authUserFunctions;

    @Autowired
    protected UserRoleFunctions userRoleFunctions;

    @Autowired
    protected RefreshTokenFunctions refreshTokenFunctions;

    @Autowired
    protected ChatFunctions chatFunctions;

    @Autowired
    protected ClubFunctions clubFunctions;

    @Autowired
    protected PersonFunctions personFunctions;

}
