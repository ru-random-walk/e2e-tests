package random_walk.automation.api.club.services;

import club_service.graphql.model.*;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@Service
@RequiredArgsConstructor
public class MemberControllerApi {

    public static final String BASE_URI = "https://random-walk.ru:44424";

    public Member addMemberInClub(UUID clubId, UUID memberId, String token) {
        var request = new AddMemberInClubMutationRequest();
        request.setClubId(clubId.toString());
        request.setMemberId(memberId.toString());
        var responseData = new MemberResponseProjection().id().role();
        var requestBody = new GraphQLRequest(request, responseData);

        return given().baseUri(BASE_URI)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody.toHttpJsonBody())
                .post("/graphql")
                .as(AddMemberInClubMutationResponse.class)
                .addMemberInClub();
    }

    public String removeMemberFromClub(UUID clubId, UUID memberId, String token) {
        var request = new RemoveMemberFromClubMutationRequest();
        request.setClubId(clubId.toString());
        request.setMemberId(memberId.toString());
        var requestBody = new GraphQLRequest(request);

        return given().baseUri(BASE_URI)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody.toHttpJsonBody())
                .post("/graphql")
                .as(RemoveMemberFromClubMutationResponse.class)
                .removeMemberFromClub();
    }
}
