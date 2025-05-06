package random_walk.automation.api.matcher.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.api.matcher.model.AddAvailableTimeRequest;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.AvailableTimeControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.*;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
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

    @Step("[MATCHER_SERVICE: /available-time/add] Добавляем время для прогулок пользователя и запускаем поиск партнера")
    public void addAvailableTime(UUID clubId,
                                 OffsetTime timeFrom,
                                 OffsetTime timeUntil,
                                 LocalDate date,
                                 Double latitude,
                                 Double longitude) {
        given().baseUri("https://random-walk.ru:44424/matcher")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(
                        new AddAvailableTimeRequest().setDate(date.toString())
                                .setTimeFrom(timeFrom.toString())
                                .setTimeUntil(timeUntil.toString())
                                .setClubsInFilter(List.of(clubId))
                                .setLocation(
                                        new LocationDto().city("Нижний Новгород")
                                                .street("Б. Покровская")
                                                .building("100/1")
                                                .latitude(latitude)
                                                .longitude(longitude)))
                .post("/available-time/add")
                .andReturn();
    }

    public void deleteAvailableTime(UUID id) {
        api.deleteAvailableTime().idPath(id).reqSpec(r -> r.addFilter(new BearerAuthToken(token))).execute(Response::andReturn);
    }
}
