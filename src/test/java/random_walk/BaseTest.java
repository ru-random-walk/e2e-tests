package random_walk;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import random_walk.automation.Application;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.service.ClubConfigService;
import random_walk.automation.service.UserConfigService;
import random_walk.extensions.RestAssuredExtension;

@ExtendWith(RestAssuredExtension.class)
@SpringBootTest(classes = Application.class)
public abstract class BaseTest {

    @Autowired
    protected UserConfigService userConfigService;

    @Autowired
    protected TestTokenConfig testTokenConfig;

    @Autowired
    protected ClubConfigService clubConfigService;
}
