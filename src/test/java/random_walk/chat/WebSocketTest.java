package random_walk.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.MimeType;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import random_walk.BaseTest;
import random_walk.automation.websocket.SslUtil;
import random_walk.automation.websocket.model.MessageRequestDto;
import ru.random_walk.swagger.chat_service.model.TextPayload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLContext;

@Slf4j
public class WebSocketTest extends BaseTest {
    private static final String WS_URL = "wss://random-walk.ru:44424/chat/ws";
    private static final String CHAT_TOPIC = "/topic/chat/";
    private static final String SEND_ENDPOINT = "/app/sendMessage";
    private static final String BEARER_TOKEN = "eyJ0eXAiOiJKV1QiLCJraWQiOiJyd19rZXkiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vYXV0aC1zZXJ2aWNlLmF1dGgtc2VydmljZS5zdmMuY2x1c3Rlci5sb2NhbDo4MDgwIiwiY2xpZW50X2lkIjoiVEVTVF9DTElFTlQiLCJhdXRob3JpdGllcyI6WyJERUZBVUxUX1VTRVIiXSwiZXhwIjoxNzQwNzc1MTc3LCJzdWIiOiIyZjRkMTYzYS1lMDI0LTQ1NzQtOWI4NC01MDIxYWUzNGNjY2UifQ.XeIp7zBlt7JyWx-hX4Sg4lUBBv9LzlxUgl1C7tj2b10TgpPNf4O4rsh_enSXpHrp1aX8NtzvjAU_fjym8_UjcN0a33vqEQusdFTCIN3eECwno_DuqTapbJYEbo_iBUw1aRNL3beeMuFoC6RTd5TD5fE_XByYCFvUclVr7U03C7gUQlM2bmms6M0TeCVsTBxNmFn_39If8t6IiQCD1-lkUqMdaR-hkM1p7u6xqLAmR-V49Fpa7xLw_lb6as5LvluNZgZzq4ubm0et4l08-B5uk1Tzn6ejLW8aITOv7KqDLjx8B-paSLmCtXFHaeCv1nNbiBHD_Bjf-xyTdxcrVS9-PQ";

    public static void main(String[] args) throws Exception {
        SslUtil.disableHttpCertificateVerification();
        String chatId = "9942e49e-13f7-4ca2-bc8e-e527c5ddac9d";
        StompSession session = connect(chatId, BEARER_TOKEN);
        sendMessage(
                session,
                UUID.fromString(chatId),
                UUID.fromString("2f4d163a-e024-4574-9b84-5021ae34ccce"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
        Thread.sleep(10_000);
        session.disconnect();
    }

    private static void sendMessage(StompSession session, UUID chatId, UUID sender, UUID recipient) {
        StompHeaders sendHeaders = getStompHeaders();
        TextPayload payload = new TextPayload().text("Hello, world!");
        MessageRequestDto message = new MessageRequestDto(
                payload,
                chatId,
                sender,
                recipient,
                LocalDateTime.parse("18:00 22-09-2024", DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));
        session.send(sendHeaders, message);
    }

    private static StompSession connect(String chatId, final String token) throws Exception {
        WebSocketStompClient stompClient = getWebSocketStompClient();
        StompSession session = connect(stompClient, token);
        session.subscribe(CHAT_TOPIC + chatId, new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("Received message: {}", payload.toString());
            }
        });
        return session;
    }

    private static StompHeaders getStompHeaders() {
        StompHeaders sendHeaders = new StompHeaders();
        sendHeaders.setDestination(SEND_ENDPOINT);
        sendHeaders.setContentType(MimeType.valueOf("application/json"));
        return sendHeaders;
    }

    private static StompSession connect(WebSocketStompClient stompClient, final String token)
            throws InterruptedException,
            ExecutionException {
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token); // Для SockJS HTTP-запросов

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token); // Для STOMP
        log.info("Connecting to {} with token: Bearer {}", WS_URL, token);

        var future = stompClient.connect(WS_URL, httpHeaders, connectHeaders, new StompSessionHandlerAdapter() {});

        try {
            return future.get();
        } catch (ExecutionException e) {
            log.error("Failed to connect: {}", e.getCause().getMessage(), e.getCause());
            throw e;
        }
    }

    private static WebSocketStompClient getWebSocketStompClient() throws Exception {
        StandardWebSocketClient webSocketClient = getWebSocketClientWithDisabledCertificateVerification();
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(webSocketClient))));
        MappingJackson2MessageConverter messageConverter = getMappingJackson2MessageConverter();
        stompClient.setMessageConverter(messageConverter);
        return stompClient;
    }

    private static MappingJackson2MessageConverter getMappingJackson2MessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрируем модуль для Java 8 Date/Time
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    private static StandardWebSocketClient getWebSocketClientWithDisabledCertificateVerification() throws Exception {
        SSLContext sslContext = SslUtil.disableCertificateVerification();
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        webSocketClient.setSslContext(sslContext);
        return webSocketClient;
    }
}
