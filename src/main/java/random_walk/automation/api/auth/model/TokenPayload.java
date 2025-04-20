package random_walk.automation.api.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenPayload {
    private String iss;

    @JsonProperty("client_id")
    private String clientId;

    private String[] authorities;

    private Long exp;

    private UUID sub;
}
