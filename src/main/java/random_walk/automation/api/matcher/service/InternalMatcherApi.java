package random_walk.automation.api.matcher.service;

import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.InternalControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;
import ru.random_walk.swagger.matcher_service.model.RequestForAppointmentDto;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.OffsetDateTime;
import java.util.UUID;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class InternalMatcherApi {

    private final InternalControllerApi api;

    private final String token;

    public InternalMatcherApi(MatcherConfigurationProperties properties, TestTokenConfig testTokenConfig) {
        this.api = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .internalController();
        this.token = testTokenConfig.getAutotestToken();
    }

    @Step
    @Description("MATCHER_SERVICE: POST /internal/appointment/request")
    @Title("WHEN: Пользователь {requesterId} создает запрос на прогулку для пользователя {partnerId}")
    public AppointmentDetailsDto getAppointmentRequest(UUID requesterId,
                                                       UUID partnerId,
                                                       OffsetDateTime startTime,
                                                       Double lon,
                                                       Double lat) {
        return api.requestForAppointment()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .body(
                        new RequestForAppointmentDto().requesterId(requesterId)
                                .partnerId(partnerId)
                                .startTime(startTime)
                                .latitude(lat)
                                .longitude(lon))
                .execute(r -> r.as(AppointmentDetailsDto.class));
    }
}
