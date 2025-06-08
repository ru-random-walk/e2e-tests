package random_walk.automation.api.matcher.service;

import io.restassured.response.Response;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.AppointmentControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.UUID;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class AppointmentMatcherApi {

    private final AppointmentControllerApi api;

    public AppointmentMatcherApi(MatcherConfigurationProperties properties) {
        this.api = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .appointmentController();
    }

    @Step
    @Description("MATCHER_SERVICE: GET /appointment/{appointmentId}")
    @Title("WHEN: Пользователь получает детали встречи по id = {appointmentId}")
    public AppointmentDetailsDto getInfoByAppointment(UUID appointmentId, String token) {
        return api.getAppointment()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .appointmentIdPath(appointmentId)
                .execute(r -> r.as(AppointmentDetailsDto.class));
    }

    @Step
    @Title("WHEN: Пользователь отменяет назначенную встречу {appointmentId}")
    @Description("MATCHER_SERVICE: DELETE /appointment/{appointmentId}/cancel")
    public void cancelAppointment(UUID appointmentId, String token) {
        api.cancelAppointment()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .appointmentIdPath(appointmentId)
                .execute(Response::andReturn);
    }

    @Step
    @Title("WHEN: Пользователь подтверждает запрос на встречу {appointmentId}")
    @Description("MATCHER_SERVICE: PUT /appointment/{appointmentId}/approve")
    public void approveAppointment(UUID appointmentId, String token) {
        api.approveAppointment()
                .appointmentIdPath(appointmentId)
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(Response::andReturn);
    }

    @Step
    @Title("WHEN: Пользователь откланяет запрос на встречу {appointmentId}")
    @Description("MATCHER_SERVICE: PUT /appointment/{appointmentId}/reject")
    public void rejectAppointment(UUID appointmentId, String token) {
        api.rejectAppointment()
                .appointmentIdPath(appointmentId)
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(Response::andReturn);
    }
}
