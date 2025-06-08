package random_walk.automation.api.club.services;

import club_service.graphql.model.*;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.api.club.BaseGraphqlRequest;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberControllerApi {

    private final BaseGraphqlRequest baseGraphqlRequest;

    @Step
    @Title("WHEN: Администратор добавляет нового пользователя в клуб")
    @Description("В клуб {clubId} добавлен пользователь {memberId}")
    public Member addMemberInClub(UUID clubId, UUID memberId, String token) {
        var request = AddMemberInClubMutationRequest.builder()
                .setClubId(clubId.toString())
                .setMemberId(memberId.toString())
                .build();
        var responseData = new MemberResponseProjection().id().role();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(AddMemberInClubMutationResponse.class)
                .addMemberInClub();
    }

    @Step
    @Title("WHEN: Администратор удаляет пользователя из группы")
    @Description("Пользователь {memberId} удален из клуба {clubId}")
    public String removeMemberFromClub(UUID clubId, UUID memberId, String token) {
        var request = RemoveMemberFromClubMutationRequest.builder()
                .setClubId(clubId.toString())
                .setMemberId(memberId.toString())
                .build();
        var requestBody = new GraphQLRequest(request).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(RemoveMemberFromClubMutationResponse.class)
                .removeMemberFromClub();
    }

    @Step
    @Title("WHEN: Администратор изменяет роль пользователя в клубе")
    @Description("Роль пользователя {memberId} в клубе {clubId} изменена на {memberRole}")
    public Member changeMemberRole(UUID clubId, UUID memberId, MemberRole memberRole, String token) {
        var request = ChangeMemberRoleMutationRequest.builder()
                .setClubId(clubId.toString())
                .setMemberId(memberId.toString())
                .setRole(memberRole)
                .build();
        var responseData = new MemberResponseProjection().id().role();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(ChangeMemberRoleMutationResponse.class)
                .changeMemberRole();
    }
}
