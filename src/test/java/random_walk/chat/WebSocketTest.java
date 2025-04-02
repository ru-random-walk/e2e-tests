package random_walk.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.Constants;
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
import random_walk.automation.websocket.model.Payload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLContext;

@Slf4j
public class WebSocketTest extends BaseTest {
    private static final String WS_URL = "wss://random-walk.ru:44424/chat/ws";
    private static final String CHAT_TOPIC = "/topic/chat/";
    private static final String SEND_ENDPOINT = "/app/sendMessage";
    private static final String BEARER_TOKEN = "eyJ0eXAiOiJKV1QiLCJraWQiOiJyd19rZXkiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vYXV0aC1zZXJ2aWNlLmF1dGgtc2VydmljZS5zdmMuY2x1c3Rlci5sb2NhbDo4MDgwIiwiY2xpZW50X2lkIjoiVEVTVF9DTElFTlQiLCJhdXRob3JpdGllcyI6WyJERUZBVUxUX1VTRVIiLCJURVNURVIiLCJJTkZJTklURV9BQ0NPVU5UIl0sImV4cCI6MTg5OTQ3Mjg4Niwic3ViIjoiNThlOTUzZWYtMDE1My00OTE4LTlhMjYtMTdiY2IyMjEzYzEyIn0.OkEZ3SF2lophwxNnNdEymTHLjH0fy64bSK2plrd2B8DP0877GfUvtxEd-XlN7_akIxdvTYByMvoYga6Q0lQV-eFruQxPaNjGcMgGm6SVBz8f3Y4mvgBVtZhEeJFv60_fMzs62Cebz2U8YzS4QnDooGklesYupQgCovzgK5czqtv4G8sDgmXkZ_WxKDgg3UmHe_FCMEIokNGPLrumG1VLzho87ultub_lo1ODCM4LHZTSZetoWGIQjULSgvJPVt-0WhXVh3-Uv--1g8mEDJmCw5Ujgi0PSs_EIqHEbcqfLRTp-N4iUEHG69L1N4BdH7uoiErKXEL_pHGUQIWZpQI4lQ";
    public static final String SU_BEARER = "eyJ0eXAiOiJKV1QiLCJraWQiOiJyd19rZXkiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vYXV0aC1zZXJ2aWNlLmF1dGgtc2VydmljZS5zdmMuY2x1c3Rlci5sb2NhbDo4MDgwIiwiY2xpZW50X2lkIjoiVEVTVF9DTElFTlQiLCJhdXRob3JpdGllcyI6WyJERUZBVUxUX1VTRVIiXSwiZXhwIjoxNzQzNjI3MDczLCJzdWIiOiIzZDE1NGVhNi03ZDY3LTQxZjEtODIwZi01ZjZiNDAzY2FlYzYifQ.DeFGe5vMhaQ94Lk45ExuyK6vs0N4FvlT7g3hBIXGGpd49gdN5sLDH7hjOrEpn5JGY9XhmABZqxDkXZPPoWqpvkzmCdgHno96H6dhe2IxADqWjTO4BzLAXwDMQJYh4Y-S3310C9foDsNSovmargt5k_j-cl3tE99IFlv0pZDUKbOLx-BTuaKrLVF3zXiEy_wXPWPsbAV4cfMxe6ZkRR75W4PlVN6lNgXo9pBSY3OkIsD5FbVzhkw9pfl0UU2zxQBmY8sbGLkxsCXmQNBhRKCOJCygjwMJI71JoOgNogEx0rZRfRxAZjg0NsJV4-MIKdb9ofPporkTACfJjQeb_xF-nA";

    public static void main(String[] args) throws Exception {
        SslUtil.disableHttpCertificateVerification();
        String chatId = "40b53ea6-864e-4e72-a808-605132b5e2c7";
        StompSession session = connect(chatId, BEARER_TOKEN);
        StompSession secondSession = connect(chatId, SU_BEARER);
        sendMessage(
                "Hi, Dmitry!",
                session,
                UUID.fromString(chatId),
                UUID.fromString("58e953ef-0153-4918-9a26-17bcb2213c12"),
                UUID.fromString("3d154ea6-7d67-41f1-820f-5f6b403caec6"));
        Thread.sleep(20000);
        // session.disconnect();
    }

    private static void sendMessage(String text, StompSession session, UUID chatId, UUID sender, UUID recipient) {
        StompHeaders sendHeaders = getStompHeaders();
        Payload payload = new Payload().setType("text").setText(text);
        MessageRequestDto message = new MessageRequestDto(
                payload,
                chatId,
                sender,
                recipient,
                LocalDateTime.parse("06:00 02-04-2025", DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));
        log.info("Отправляем {}", message);
        session.send(sendHeaders, message);
        log.info("Сообщение успешно отправлено");
    }

    private static StompSession connect(String chatId, final String token) throws Exception {
        WebSocketStompClient stompClient = getWebSocketStompClient();
        StompSession session = connect(stompClient, token);
        log.info("session = {}, sclient = {}", session, stompClient);
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
        webSocketClient.setUserProperties(Map.of(Constants.SSL_CONTEXT_PROPERTY, sslContext));
        return webSocketClient;
    }
}
