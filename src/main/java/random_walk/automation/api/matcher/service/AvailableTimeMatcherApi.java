package random_walk.automation.api.matcher.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.AvailableTimeControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

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
    public void addAvailableTime(UUID clubId ) {
        var timeFrom = OffsetTime.now().minusHours(3);
        var timeUntil = OffsetTime.now();
        api.addAvailableTime().reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .body(new AvailableTimeModifyDto().date(LocalDate.now()).clubsInFilter(List.of(clubId))
                        .timeFrom(new AvailableTimeModifyDtoTimeFrom().hour(timeFrom.getHour()).minute(timeFrom.getMinute()).second(timeFrom.getSecond())
                                .nano(timeFrom.getNano()).offset(new AvailableTimeModifyDtoTimeFromOffset().id(timeFrom.getOffset().getId()))).timeUntil(
                                        new AvailableTimeModifyDtoTimeFrom().hour(timeUntil.getHour()).minute(timeUntil.getMinute()).second(timeUntil.getSecond()).nano(timeUntil.getNano()).offset(new AvailableTimeModifyDtoTimeFromOffset().id(timeUntil.getOffset().getId()))
                        ).location(new LocationDto().city("Нижний Новгород").street("Б. Покровская").building("100/1").latitude(34.234234).longitude(47.341253)))
                .execute(Response::andReturn);
    }
}
