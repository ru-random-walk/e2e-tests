package random_walk.automation.api.matcher.service;

import io.qameta.allure.Step;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.PersonControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.ClubDto;
import ru.random_walk.swagger.matcher_service.model.PersonDto;
import ru.random_walk.swagger.matcher_service.model.UserScheduleDto;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class PersonMatcherApi {

    private final PersonControllerApi api;

    private final String token;

    public PersonMatcherApi(MatcherConfigurationProperties properties, TestTokenConfig testTokenConfig) {
        this.api = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .personController();
        this.token = testTokenConfig.getToken();
    }

    @Step("[MATCHER_SERVICE: /person/schedule] Получение информации о расписании пользователя")
    public UserScheduleDto getInfoAboutSchedule() {
        return api.getUserSchedule()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(r -> r.as(UserScheduleDto.class));
    }

    @Step("[MATCHER_SERVICE: /person/info] Получение информации о пользователе")
    public PersonDto getInfoAboutUser() {
        return api.getPersonInfo().reqSpec(r -> r.addFilter(new BearerAuthToken(token))).execute(r -> r.as(PersonDto.class));
    }

    @Step("[MATCHER_SERVICE: /person/clubs] Получение информации о клубах пользователя")
    public ClubDto[] getInfoAboutUserClubs() {
        return api.getClubs().reqSpec(r -> r.addFilter(new BearerAuthToken(token))).execute(r -> r.as(ClubDto[].class));
    }
}
