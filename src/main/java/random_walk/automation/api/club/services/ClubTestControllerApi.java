package random_walk.automation.api.club.services;

import club_service.graphql.model.ListenRegisteredUserInfoEventMutationRequest;
import club_service.graphql.model.ListenRegisteredUserInfoEventMutationResponse;
import club_service.graphql.model.RegisteredUserInfoEvent;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.api.club.BaseGraphqlRequest;
import random_walk.automation.config.TestTokenConfig;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubTestControllerApi {

    private final BaseGraphqlRequest baseGraphqlRequest;

    private final TestTokenConfig testTokenConfig;

    public void addMemberInClubService(UUID userId, String fullName) {
        var request = ListenRegisteredUserInfoEventMutationRequest.builder()
                .setEvent(RegisteredUserInfoEvent.builder().setId(userId.toString()).setFullName(fullName).build())
                .build();
        var requestBody = new GraphQLRequest(request).toHttpJsonBody();

        baseGraphqlRequest.getDefaultGraphqlRequest(testTokenConfig.getToken(), requestBody)
                .as(ListenRegisteredUserInfoEventMutationResponse.class)
                .listenRegisteredUserInfoEvent();
    }
}
