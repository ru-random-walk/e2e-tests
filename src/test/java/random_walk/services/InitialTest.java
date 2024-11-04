package random_walk.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import random_walk.automation.api.auth.AuthServiceApi;
import random_walk.extensions.RestAssuredExtension;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(RestAssuredExtension.class)
@SpringBootTest(classes = { AuthServiceApi.class })
public class InitialTest {

    @Autowired
    private AuthServiceApi api;
    @Test
    public void test() {
        var a = api.getOpenidConfiguration();
        step("GIVEN: First step");
        step("WHEN: Second step");
        step("THEN: Third step", () ->
                assertAll(
                        () -> assertThat(1, equalTo(1)),
                        () -> assertThat(2, equalTo(2))
                ));
    }
}
