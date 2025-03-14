package random_walk.automation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import random_walk.automation.api.auth.models.TokenPayload;

import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParseAccessTokenUtils {

    public static TokenPayload getInfoFromAccessToken(String accessToken) throws JsonProcessingException {
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        return new ObjectMapper().readValue(payload, TokenPayload.class);

    }

}
