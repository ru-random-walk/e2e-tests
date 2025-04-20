package random_walk.auth;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import random_walk.BaseTest;
import random_walk.automation.api.auth.service.AuthServiceApi;
import random_walk.automation.api.auth.service.GoogleAccessTokenApi;
import random_walk.automation.database.auth.functions.AuthUserFunctions;

@Tag("auth-e2e")
public abstract class AuthTest extends BaseTest {

    @Autowired
    protected AuthUserFunctions authUserFunctions;

    @Autowired
    protected AuthServiceApi authServiceApi;

    @Autowired
    protected GoogleAccessTokenApi googleAccessTokenApi;
}
