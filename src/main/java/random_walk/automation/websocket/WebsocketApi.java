package random_walk.automation.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.Constants;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import random_walk.automation.config.WebsocketConfig;
import random_walk.automation.websocket.model.MessageRequestDto;
import random_walk.automation.websocket.model.Payload;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebsocketApi {

    private final WebsocketConfig websocketConfig;

    public void sendMessage(String text,
                            StompSession session,
                            UUID chatId,
                            UUID sender,
                            UUID recipient,
                            LocalDateTime localDateTime) {
        StompHeaders sendHeaders = getStompHeaders();
        Payload payload = new Payload().setType("text").setText(text);
        MessageRequestDto message = new MessageRequestDto(payload, chatId, sender, recipient, localDateTime);
        log.info("Отправляем {}", message);
        session.send(sendHeaders, message);
        log.info("Сообщение успешно отправлено");
    }

    public StompSession connect(UUID chatId, final String token) {
        WebSocketStompClient stompClient;
        StompSession session;
        try {
            stompClient = getWebSocketStompClient();
            session = connect(stompClient, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("session = {}, sclient = {}", session, stompClient);
        session.subscribe(websocketConfig.getChatEndpoint() + chatId, new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("Received message: {}", payload.toString());
            }
        });
        return session;
    }

    private StompHeaders getStompHeaders() {
        StompHeaders sendHeaders = new StompHeaders();
        sendHeaders.setDestination(websocketConfig.getMessageEndpoint());
        sendHeaders.setContentType(MimeType.valueOf("application/json"));
        return sendHeaders;
    }

    private StompSession connect(WebSocketStompClient stompClient, final String token)
            throws InterruptedException,
            ExecutionException {
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token); // Для SockJS HTTP-запросов

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token); // Для STOMP
        log.info("Connecting to {} with token: Bearer {}", websocketConfig.getUrl(), token);

        var future = stompClient
                .connect(websocketConfig.getUrl(), httpHeaders, connectHeaders, new StompSessionHandlerAdapter() {});

        try {
            return future.get();
        } catch (ExecutionException e) {
            log.error("Failed to connect: {}", e.getCause().getMessage(), e.getCause());
            throw e;
        }
    }

    private WebSocketStompClient getWebSocketStompClient() throws Exception {
        StandardWebSocketClient webSocketClient = getWebSocketClientWithDisabledCertificateVerification();
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(webSocketClient))));
        MappingJackson2MessageConverter messageConverter = getMappingJackson2MessageConverter();
        stompClient.setMessageConverter(messageConverter);
        return stompClient;
    }

    private MappingJackson2MessageConverter getMappingJackson2MessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрируем модуль для Java 8 Date/Time
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    private StandardWebSocketClient getWebSocketClientWithDisabledCertificateVerification() throws Exception {
        SSLContext sslContext = SslUtil.disableCertificateVerification();
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        webSocketClient.setUserProperties(Map.of(Constants.SSL_CONTEXT_PROPERTY, sslContext));
        return webSocketClient;
    }
}
