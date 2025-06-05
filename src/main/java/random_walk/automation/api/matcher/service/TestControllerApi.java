package random_walk.automation.api.matcher.service;

import io.restassured.response.Response;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.RegisteredUserInfoEvent;

import java.util.UUID;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class TestControllerApi {

    private final ru.random_walk.swagger.matcher_service.api.TestControllerApi api;

    private final TestTokenConfig testTokenConfig;

    public TestControllerApi(MatcherConfigurationProperties properties, TestTokenConfig testTokenConfig) {
        this.api = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .testController();
        this.testTokenConfig = testTokenConfig;
    }

    public void addUserInMatcher(UUID userId, String fullName) {
        api.addPerson()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(testTokenConfig.getToken())))
                .body(new RegisteredUserInfoEvent().id(userId).fullName(fullName))
                .execute(Response::andReturn);
    }
}
