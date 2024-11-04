package random_walk.automation.api.auth;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.springframework.stereotype.Service;

import static io.restassured.RestAssured.given;

@Service
public class AuthServiceApi {

    @Step("/.well-knowm/openid-configuration")
    public ResponseBody getOpenidConfiguration() {
        return given()
                .contentType("application/json")
                .get("http://random-walk.ru/auth/.well-known/openid-configuration")
                .getBody();
    }
}
