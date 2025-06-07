package random_walk.automation.api.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import random_walk.automation.api.auth.AuthServiceConfigurationProperties;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BasicAuthFilter;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.auth_service.api.OAuth2ControllerApi;
import ru.random_walk.swagger.auth_service.api.TokenControllerApi;
import ru.random_walk.swagger.auth_service.api.UserControllerApi;
import ru.random_walk.swagger.auth_service.invoker.ApiClient;
import ru.random_walk.swagger.auth_service.model.*;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class AuthServiceApi {

    private final TokenControllerApi tokenControllerApi;

    private final OAuth2ControllerApi oAuth2ControllerApi;

    private final UserControllerApi userControllerApi;

    private final String username;

    private final String password;

    private final String token;

    @Autowired
    public AuthServiceApi(
            AuthServiceConfigurationProperties authServiceConfigurationProperties,
            TestTokenConfig testTokenConfig) {
        this.tokenControllerApi = ApiClient
                .api(
                        ApiClient.Config.apiConfig()
                                .reqSpecSupplier(getSupplierWithUri(authServiceConfigurationProperties.httpEndpoint().host())))
                .tokenController();
        this.oAuth2ControllerApi = ApiClient
                .api(
                        ApiClient.Config.apiConfig()
                                .reqSpecSupplier(getSupplierWithUri(authServiceConfigurationProperties.httpEndpoint().host())))
                .oAuth2Controller();
        this.userControllerApi = ApiClient
                .api(
                        ApiClient.Config.apiConfig()
                                .reqSpecSupplier(getSupplierWithUri(authServiceConfigurationProperties.httpEndpoint().host())))
                .userController();
        this.username = authServiceConfigurationProperties.username();
        this.password = authServiceConfigurationProperties.password();
        this.token = testTokenConfig.getToken();
    }

    @Step
    @Description("[AUTH_SERVICE: POST /token]")
    @Title("WHEN: Получаем access и refresh токены для пользователя")
    public TokenResponse getAuthTokens(String accessToken) {
        var mapOfRequestParams = Map.of(
                "grant_type",
                "urn:ietf:params:oauth:grant-type:token-exchange",
                "subject_token",
                accessToken,
                "subject_token_type",
                "Access Token",
                "subject_token_provider",
                "google");

        return tokenControllerApi.token().reqSpec(r -> {
            r.addFilter(new BasicAuthFilter(username, password));
            r.setContentType("application/x-www-form-urlencoded");
            r.addFormParams(mapOfRequestParams);
        }).execute(r -> r.as(TokenResponse.class));
    }

    // @Step("Получаем access и refresh токены для пользователя")
    public TokenResponse getAuthTokens(String email, Integer oneTimePassword) {
        var mapOfRequestParams = Map.of("grant_type", "email_otp", "otp", oneTimePassword, "email", email);

        return tokenControllerApi.token().reqSpec(r -> {
            r.addFilter(new BasicAuthFilter(username, password));
            r.setContentType("application/x-www-form-urlencoded");
            r.addFormParams(mapOfRequestParams);
        }).execute(r -> r.as(TokenResponse.class));
    }

    @Step
    @Description("[AUTH_SERVICE: POST /token]")
    @Title("WHEN: Обновляем access_token по полученному refresh_token")
    public TokenResponse refreshAuthToken(String refreshToken) {
        var mapOfRequestParams = Map.of("grant_type", "refresh_token", "refresh_token", refreshToken);

        return tokenControllerApi.token().reqSpec(r -> {
            r.addFilter(new BasicAuthFilter(username, password));
            r.setContentType("application/x-www-form-urlencoded");
            r.addFormParams(mapOfRequestParams);
        }).execute(r -> r.as(TokenResponse.class));
    }

    @Step
    @Description("[AUTH_SERVICE: GET /users]")
    @Title("WHEN: Получаем список пользователей по их id")
    public PagedModelUserDto getUsersById(List<UUID> ids, @Nullable Integer size, @Nullable Integer page, @Nullable String sort) {

        var getUsersRequest = userControllerApi.getUsers();

        if (size != null)
            getUsersRequest.sizeQuery(size);

        if (page != null)
            getUsersRequest.pageQuery(page);

        if (sort != null)
            getUsersRequest.sortQuery(sort);

        Object[] currentIds = new Object[ids.size()];
        var index = 0;
        for (UUID id : ids) {
            currentIds[index++] = id;
        }

        return getUsersRequest.reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .idsQuery(currentIds)
                .execute(r -> r.as(PagedModelUserDto.class));
    }

    @Step
    @Description("[AUTH_SERVICE: GET /userinfo/me]")
    @Title("WHEN: Получение собственной информации пользователем")
    public DetailedUserDto getUserSelfInfo(String token) {
        return userControllerApi.getSelfInfo()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .execute(r -> r.as(DetailedUserDto.class));
    }

    @Step
    @Description("AUTH_SERVICE: PUT /userinfo/change")
    @Title("WHEN: Изменение собственной информации о пользователе")
    public DetailedUserDto changeInfoAboutUser(String token, String fullName, String description) {
        return userControllerApi.changeSelfInfo()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .body(new ChangeUserInfoDto().fullName(fullName).aboutMe(description))
                .execute(r -> r.as(DetailedUserDto.class));
    }
}
