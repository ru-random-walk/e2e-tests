package random_walk.automation.api.club;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.config.GraphqlConfig;

import static io.restassured.RestAssured.given;

@Service
@RequiredArgsConstructor
public class BaseGraphqlRequest {

    private final GraphqlConfig graphqlConfig;

    public Response getDefaultGraphqlRequest(String token, String requestBody) {
        return given().baseUri(graphqlConfig.getBaseUri())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(graphqlConfig.getRequestPath());
    }
}
