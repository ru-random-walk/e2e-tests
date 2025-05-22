package random_walk.automation.api.chat.service;

import io.restassured.response.ResponseOptions;
import org.springframework.stereotype.Service;
import random_walk.automation.api.chat.ChatConfigurationProperties;
import random_walk.automation.api.chat.model.PagedModelMessage;
import random_walk.automation.config.TestTokenConfig;
import random_walk.automation.config.filter.BearerAuthToken;
import ru.random_walk.swagger.chat_service.api.RestChatControllerApi;
import ru.random_walk.swagger.chat_service.api.RestIntegrationTestControllerApi;
import ru.random_walk.swagger.chat_service.api.RestMessageControllerApi;
import ru.random_walk.swagger.chat_service.invoker.ApiClient;
import ru.random_walk.swagger.chat_service.model.CreatePrivateChatEvent;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.Nullable;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class ChatApi {

    private final RestChatControllerApi restChatControllerApi;

    private final RestMessageControllerApi restMessageControllerApi;

    private final RestIntegrationTestControllerApi restIntegrationTestControllerApi;

    private final TestTokenConfig testTokenConfig;

    public ChatApi(ChatConfigurationProperties properties, TestTokenConfig testTokenConfig) {
        this.restChatControllerApi = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .restChatController();
        this.restMessageControllerApi = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .restMessageController();
        this.restIntegrationTestControllerApi = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .restIntegrationTestController();
        this.testTokenConfig = testTokenConfig;
    }

    @Step
    @Title("WHEN: Происходит создание чата между {firstMember} и {secondMember}")
    @Description("CHAT_SERVICE: POST /test/create-private-chat-event")
    public void createPrivateChatEvent(UUID firstMember, UUID secondMember) {
        restIntegrationTestControllerApi.createChat()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(testTokenConfig.getToken())))
                .body(new CreatePrivateChatEvent().chatMember1(firstMember).chatMember2(secondMember))
                .execute(ResponseOptions::andReturn);
    }

    @Step
    @Title("WHEN: Происходит получение списка сообщений из чата {chatId}")
    @Description("CHAT_SERVICE: GET /message/list")
    public PagedModelMessage getChatMessageList(UUID chatId,
                                                @Nullable LocalDateTime from,
                                                @Nullable LocalDateTime to,
                                                @Nullable Integer size,
                                                @Nullable Integer page,
                                                @Nullable String sort,
                                                @Nullable String message) {
        var getHistoryRequest = restMessageControllerApi.getHistory().reqSpec(r -> {
            r.addFilter(new BearerAuthToken(testTokenConfig.getToken()));
            if (size != null)
                r.addQueryParam("size", size);
            if (page != null)
                r.addQueryParam("page", page);
            if (sort != null)
                r.addQueryParam("sort", sort);
        }).chatIdQuery(chatId);
        if (from != null)
            getHistoryRequest.fromQuery(from.toString());
        if (to != null)
            getHistoryRequest.toQuery(to.toString());
        if (message != null)
            getHistoryRequest.messageQuery(message);
        return getHistoryRequest.execute(r -> r.as(PagedModelMessage.class));
    }

    @Step
    @Title("WHEN: Происходит получение списка сообщений из чата {chatId}")
    @Description("CHAT_SERVICE: GET /message/list")
    public PagedModelMessage getAutotestChatMessageList(UUID chatId,
                                                        @Nullable LocalDateTime from,
                                                        @Nullable LocalDateTime to,
                                                        @Nullable Integer size,
                                                        @Nullable Integer page,
                                                        @Nullable String sort,
                                                        @Nullable String message) {
        var getHistoryRequest = restMessageControllerApi.getHistory().reqSpec(r -> {
            r.addFilter(new BearerAuthToken(testTokenConfig.getAutotestToken()));
            if (size != null)
                r.addQueryParam("size", size);
            if (page != null)
                r.addQueryParam("page", page);
            if (sort != null)
                r.addQueryParam("sort", sort);
        }).chatIdQuery(chatId);
        if (from != null)
            getHistoryRequest.fromQuery(from.toString());
        if (to != null)
            getHistoryRequest.toQuery(to.toString());
        if (message != null)
            getHistoryRequest.messageQuery(message);
        return getHistoryRequest.execute(r -> r.as(PagedModelMessage.class));
    }
}
