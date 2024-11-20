package random_walk.automation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import random_walk.automation.api.auth.models.TokenPayload;

import java.util.Base64;

@Component
public class ParseAccessTokenUtils {

    public TokenPayload getInfoFromAccessToken(String accessToken) throws JsonProcessingException {
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        return new ObjectMapper().readValue(payload, TokenPayload.class);

    }

}
