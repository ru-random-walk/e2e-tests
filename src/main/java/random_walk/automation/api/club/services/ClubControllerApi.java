package random_walk.automation.api.club.services;

import club_service.graphql.model.Club;
import club_service.graphql.model.ClubResponseProjection;
import club_service.graphql.model.GetClubQueryRequest;
import club_service.graphql.model.GetClubQueryResponse;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.restassured.RestAssured.given;

@Service
@RequiredArgsConstructor
public class ClubControllerApi {

    public static final String BASE_URI = "https://random-walk.ru:44424";

    public Club getClub(String clubId, String token) {
        var request = new GetClubQueryRequest();
        request.setClubId(clubId);
        var responseData = new ClubResponseProjection().id().description();
        var requestBody = new GraphQLRequest(request, responseData);
        return given().baseUri(BASE_URI)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody.toHttpJsonBody())
                .post("/graphql")
                .as(GetClubQueryResponse.class)
                .getClub();
    }
}
