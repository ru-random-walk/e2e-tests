package random_walk;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import random_walk.automation.Application;
import random_walk.automation.api.auth.AuthServiceApi;
import random_walk.automation.databases.functions.RoleFunctions;
import random_walk.extensions.RestAssuredExtension;

@ExtendWith(RestAssuredExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = Application.class)
public abstract class BaseTest {

    @Autowired
    protected AuthServiceApi api;

    @Autowired
    protected RoleFunctions roleFunctions;

}
