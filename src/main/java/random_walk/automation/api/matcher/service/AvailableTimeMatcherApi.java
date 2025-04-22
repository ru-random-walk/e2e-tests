package random_walk.automation.api.matcher.service;

import io.qameta.allure.Step;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.AvailableTimeControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;

import java.time.LocalDate;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class AvailableTimeMatcherApi {

    private final AvailableTimeControllerApi api;

    private final String token;

    public AvailableTimeMatcherApi(MatcherConfigurationProperties properties, TestTokenConfig testTokenConfig) {
        this.api = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .availableTimeController();
        this.token = testTokenConfig.getToken();
    }

    @Step("[MATCHER_SERVICE: /available-time/{id}/change] Изменяем время для прогулок пользователя и запускаем поиск партнеров")
    public void changeAvailableTime(String id, LocalDate date) {
        api.changeSchedule().reqSpec(r -> r.addFilter(new BearerAuthToken(token))).idPath(id);
        // .body(new AvailableTimeModifyDto().date(date).)
    }
}
