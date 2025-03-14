package random_walk.automation.api.chat.services;

import io.restassured.response.ResponseOptions;
import org.springframework.stereotype.Service;
import random_walk.automation.api.chat.ChatConfigurationProperties;
import random_walk.automation.config.filters.BearerAuthToken;
import ru.random_walk.swagger.chat_service.api.RestChatControllerApi;
import ru.random_walk.swagger.chat_service.api.RestIntegrationTestControllerApi;
import ru.random_walk.swagger.chat_service.api.RestMessageControllerApi;
import ru.random_walk.swagger.chat_service.invoker.ApiClient;
import ru.random_walk.swagger.chat_service.model.CreatePrivateChatEvent;

import java.util.UUID;

import static random_walk.automation.config.SwaggerConfig.getSupplierWithUri;

@Service
public class ChatApi {

    private final RestChatControllerApi restChatControllerApi;

    private final RestMessageControllerApi restMessageControllerApi;

    private final RestIntegrationTestControllerApi restIntegrationTestControllerApi;

    public ChatApi(ChatConfigurationProperties properties) {
        this.restChatControllerApi = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .restChatController();
        this.restMessageControllerApi = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .restMessageController();
        this.restIntegrationTestControllerApi = ApiClient
                .api(ApiClient.Config.apiConfig().reqSpecSupplier(getSupplierWithUri(properties.httpEndpoint().host())))
                .restIntegrationTestController();
    }

    public void createPrivateChatEvent(UUID firstMember, UUID secondMember, String accessToken) {
        restIntegrationTestControllerApi.createChat()
                .reqSpec(r -> r.addFilter(new BearerAuthToken(accessToken)))
                .body(new CreatePrivateChatEvent().chatMember1(firstMember).chatMember2(secondMember))
                .execute(ResponseOptions::andReturn);
    }
}
