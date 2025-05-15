package random_walk.automation.api.club.services;

import club_service.graphql.model.*;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.api.club.BaseGraphqlRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberControllerApi {

    private final BaseGraphqlRequest baseGraphqlRequest;

    public Member addMemberInClub(UUID clubId, UUID memberId, String token) {
        var request = AddMemberInClubMutationRequest.builder()
                .setClubId(clubId.toString())
                .setMemberId(memberId.toString())
                .build();
        var responseData = new MemberResponseProjection().id().role();
        var requestBody = new GraphQLRequest(request, responseData);

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody.toHttpJsonBody())
                .as(AddMemberInClubMutationResponse.class)
                .addMemberInClub();
    }

    public String removeMemberFromClub(UUID clubId, UUID memberId, String token) {
        var request = RemoveMemberFromClubMutationRequest.builder()
                .setClubId(clubId.toString())
                .setMemberId(memberId.toString())
                .build();
        var requestBody = new GraphQLRequest(request);

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody.toHttpJsonBody())
                .as(RemoveMemberFromClubMutationResponse.class)
                .removeMemberFromClub();
    }

    public Member changeMemberRole(UUID clubId, UUID memberId, MemberRole memberRole, String token) {
        var request = ChangeMemberRoleMutationRequest.builder()
                .setClubId(clubId.toString())
                .setMemberId(memberId.toString())
                .setRole(memberRole)
                .build();
        var responseData = new MemberResponseProjection().id().role();
        var requestBody = new GraphQLRequest(request, responseData);

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody.toHttpJsonBody())
                .as(ChangeMemberRoleMutationResponse.class)
                .changeMemberRole();
    }
}
