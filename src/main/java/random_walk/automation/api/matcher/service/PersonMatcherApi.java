package random_walk.automation.api.matcher.service;

import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.api.matcher.model.UserSchedule;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.PersonControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.ClubDto;
import ru.random_walk.swagger.matcher_service.model.PersonDto;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

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

    @Step
    @Title("WHEN: Пользователь получает информацию о своем расписании")
    @Description("MATCHER_SERVICE: GET /person/schedule")
    public UserSchedule[] getInfoAboutSchedule(String token) {
        return api.getUserSchedule()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(r -> r.as(UserSchedule[].class));
    }

    @Step
    @Title("WHEN: Пользователь получает собственную информацию")
    @Description("MATCHER_SERVICE: GET /person/info")
    public PersonDto getInfoAboutUser() {
        return api.getPersonInfo().reqSpec(r -> r.addFilter(new BearerAuthToken(token))).execute(r -> r.as(PersonDto.class));
    }

    @Step
    @Title("WHEN: Пользователь получает информацию о клубах, в которых состоит")
    @Description("MATCHER_SERVICE: GET /person/clubs")
    public ClubDto[] getInfoAboutUserClubs() {
        return api.getClubs().reqSpec(r -> r.addFilter(new BearerAuthToken(token))).execute(r -> r.as(ClubDto[].class));
    }
}
