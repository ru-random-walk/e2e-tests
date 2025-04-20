package random_walk.automation.api.chat.service;

import io.qameta.allure.Step;
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

import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.Nullable;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class ChatApi {

    private final RestChatControllerApi restChatControllerApi;

    private final RestMessageControllerApi restMessageControllerApi;

    private final RestIntegrationTestControllerApi restIntegrationTestControllerApi;

    private final String token;

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
        this.token = testTokenConfig.getToken();
    }

    @Step("[CHAT_SERVICE: /test/create-private-chat-event] Создаем чат между {firstMember} и {secondMember}")
    public void createPrivateChatEvent(UUID firstMember, UUID secondMember) {
        restIntegrationTestControllerApi.createChat()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(token)))
                .body(new CreatePrivateChatEvent().chatMember1(firstMember).chatMember2(secondMember))
                .execute(ResponseOptions::andReturn);
    }

    @Step("[CHAT_SERVICE: /message/list] Получаем список сообщений из чата {chatId}")
    public PagedModelMessage getChatMessageList(UUID chatId,
                                                @Nullable LocalDateTime from,
                                                @Nullable LocalDateTime to,
                                                @Nullable Integer size,
                                                @Nullable Integer page,
                                                @Nullable String sort,
                                                @Nullable String message) {
        var getHistoryRequest = restMessageControllerApi.getHistory().reqSpec(r -> {
            r.addFilter(new BearerAuthToken(token));
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
