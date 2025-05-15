package random_walk.automation.api.club.services;

import club_service.graphql.model.*;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.api.club.BaseGraphqlRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubControllerApi {

    private final BaseGraphqlRequest baseGraphqlRequest;

    public Club getClub(UUID clubId, String token) {
        var request = GetClubQueryRequest.builder().setClubId(clubId.toString()).build();
        var responseData = new ClubResponseProjection().id()
                .name()
                .description()
                .approvements(new ApprovementResponseProjection().id())
                .members(new MemberResponseProjection().id().role())
                .approversNumber();
        var requestBody = new GraphQLRequest(request, responseData);

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody.toHttpJsonBody())
                .as(GetClubQueryResponse.class)
                .getClub();
    }

    public Club createClub(String name, String description, String token) {
        var request = CreateClubMutationRequest.builder().setName(name).setDescription(description).build();
        var responseData = new ClubResponseProjection().id().name().description();
        var requestBody = new GraphQLRequest(request, responseData);

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody.toHttpJsonBody())
                .as(CreateClubMutationResponse.class)
                .createClub();
    }

    public String removeClub(UUID clubId, String token) {
        var request = RemoveClubWithAllItsDataMutationRequest.builder().setClubId(clubId.toString()).build();
        var requestBody = new GraphQLRequest(request);

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody.toHttpJsonBody())
                .as(RemoveClubWithAllItsDataMutationResponse.class)
                .removeClubWithAllItsData();
    }

}
