package random_walk.automation.api.auth.services;

import io.qameta.allure.Step;
import org.springframework.stereotype.Service;
import random_walk.automation.api.auth.AuthServiceConfigurationProperties;
import random_walk.automation.config.filters.BasicAuthFilter;
import ru.random_walk.swagger.auth_service.api.OAuth2ControllerApi;
import ru.random_walk.swagger.auth_service.api.TokenControllerApi;
import ru.random_walk.swagger.auth_service.invoker.ApiClient;
import ru.random_walk.swagger.auth_service.model.OAuthConfigurationResponse;
import ru.random_walk.swagger.auth_service.model.TokenResponse;

import java.util.Map;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class AuthServiceApi {

    private final TokenControllerApi tokenControllerApi;

    private final OAuth2ControllerApi oAuth2ControllerApi;

    private final String username;

    private final String password;

    public AuthServiceApi(AuthServiceConfigurationProperties authServiceConfigurationProperties) {
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
        this.username = authServiceConfigurationProperties.username();
        this.password = authServiceConfigurationProperties.password();
    }

    @Step("/.well-knowm/openid-configuration")
    public OAuthConfigurationResponse getOpenidConfiguration() {
        return oAuth2ControllerApi.getConfiguration().execute(r -> r.as(OAuthConfigurationResponse.class));
    }

    @Step("Получаем access и refresh токены для пользователя")
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

    @Step("Обновляем access_token по полученному refresh_token")
    public TokenResponse refreshAuthToken(String refreshToken) {
        var mapOfRequestParams = Map.of("grant_type", "refresh_token", "refresh_token", refreshToken);

        return tokenControllerApi.token().reqSpec(r -> {
            r.addFilter(new BasicAuthFilter(username, password));
            r.setContentType("application/x-www-form-urlencoded");
            r.addFormParams(mapOfRequestParams);
        }).execute(r -> r.as(TokenResponse.class));
    }
}
