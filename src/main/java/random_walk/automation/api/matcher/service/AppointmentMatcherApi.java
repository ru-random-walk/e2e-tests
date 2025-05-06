package random_walk.automation.api.matcher.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;
import random_walk.automation.api.matcher.MatcherConfigurationProperties;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.matcher_service.api.AppointmentControllerApi;
import ru.random_walk.swagger.matcher_service.invoker.ApiClient;
import ru.random_walk.swagger.matcher_service.model.AppointmentDetailsDto;

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

    @Step("[MATCHER_SERVICE: /appointment/{appointmentId}] Получение деталей встречи по id")
    public AppointmentDetailsDto getInfoByAppointment(String appointmentId, String token) {
        return api.getAppointment()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .appointmentIdPath(appointmentId)
                .execute(r -> r.as(AppointmentDetailsDto.class));
    }

    @Step("[MATCHER_SERVICE: /appointment/{appointmentId}/cancel] Отмена назначенной встречи")
    public void cancelAppointmentById(String appointmentId, String token) {
        api.cancelAppointment().reqSpec(r -> r.addFilter(new BearerAuthToken(token))).appointmentIdPath(appointmentId);
    }

    public void approveAppointment(UUID appointmentId, String token) {
        api.approveAppointment()
                .appointmentIdPath(appointmentId)
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(Response::andReturn);
    }

    public void rejectAppointment(UUID appointmentId, String token) {
        api.rejectAppointment()
                .appointmentIdPath(appointmentId)
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(Response::andReturn);
    }
}
