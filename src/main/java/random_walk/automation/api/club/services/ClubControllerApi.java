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
public class ClubControllerApi {

    private final BaseGraphqlRequest baseGraphqlRequest;

    public Club getClub(UUID clubId, String token) {
        var request = GetClubQueryRequest.builder().setClubId(clubId.toString()).build();
        var responseData = new ClubResponseProjection().id()
                .name()
                .description()
                .approvements(new ApprovementResponseProjection().id())
                .members(new MemberResponseProjection().id().role())
                .approversNumber()
                .photoVersion();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody).as(GetClubQueryResponse.class).getClub();
    }

    @Step
    @Title("Создание клуба {name} с описанием {description}")
    public Club createClub(String name, String description, String token) {
        var request = CreateClubMutationRequest.builder().setName(name).setDescription(description).build();
        var responseData = new ClubResponseProjection().id().name().description();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody).as(CreateClubMutationResponse.class).createClub();
    }

    @Step
    @Title("WHEN: Пользователь создает клуб {name} с тестами на вступление")
    @Description("Тесты - {form}")
    public Club createClubWithFormApprovement(String name, String description, FormInput form, String token) {
        var request = CreateClubWithFormApprovementMutationRequest.builder()
                .setName(name)
                .setDescription(description)
                .setForm(form)
                .build();
        var responseData = new ClubResponseProjection().id().name().description();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(CreateClubWithFormApprovementMutationResponse.class)
                .createClubWithFormApprovement();
    }

    @Step
    @Title("WHEN: Пользователь создает клуб {name} с подтверждением вступления")
    @Description("Необходимое количество подтверждений - {requiredConfirmationNumber}")
    public Club createClubWithMemberConfirm(String name,
                                            String description,
                                            Integer requiredConfirmationNumber,
                                            Integer approversToNotifyCount,
                                            String token) {
        var request = CreateClubWithMembersConfirmApprovementMutationRequest.builder()
                .setName(name)
                .setDescription(description)
                .setMembersConfirm(
                        MembersConfirmInput.builder()
                                .setRequiredConfirmationNumber(requiredConfirmationNumber)
                                .setApproversToNotifyCount(approversToNotifyCount)
                                .build())
                .build();
        var responseData = new ClubResponseProjection().id().name().description().approversNumber();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(CreateClubWithMembersConfirmApprovementMutationResponse.class)
                .createClubWithMembersConfirmApprovement();
    }

    public PhotoUrl uploadPhotoForClub(UUID clubId, PhotoInput input, String token) {
        var request = UploadPhotoForClubMutationRequest.builder().setClubId(clubId.toString()).setPhoto(input).build();
        var responseData = new PhotoUrlResponseProjection().clubId().url().expiresInMinutes();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(UploadPhotoForClubMutationResponse.class)
                .uploadPhotoForClub();
    }

    @Step
    @Title("WHEN: Получена фотография существующего клуба")
    @Description("Клуб - {clubId}")
    public PhotoUrl getClubPhoto(UUID clubId, String token) {
        var request = GetClubPhotoQueryRequest.builder().setClubId(clubId.toString()).build();
        var responseData = new PhotoUrlResponseProjection().clubId().url().expiresInMinutes();
        var requestBody = new GraphQLRequest(request, responseData).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody).as(GetClubPhotoQueryResponse.class).getClubPhoto();
    }

    @Step
    @Title("WHEN: Администратор удаляет существующий клуб")
    @Description("Клуб - {clubId}")
    public String removeClub(UUID clubId, String token) {
        var request = RemoveClubWithAllItsDataMutationRequest.builder().setClubId(clubId.toString()).build();
        var requestBody = new GraphQLRequest(request).toHttpJsonBody();

        return baseGraphqlRequest.getDefaultGraphqlRequest(token, requestBody)
                .as(RemoveClubWithAllItsDataMutationResponse.class)
                .removeClubWithAllItsData();
    }

}
