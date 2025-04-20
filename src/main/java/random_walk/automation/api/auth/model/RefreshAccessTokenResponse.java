package random_walk.automation.api.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshAccessTokenResponse {

    @JsonProperty("success")
    private Boolean isSuccess;

    @JsonProperty("access_token")
    private String accessToken;

}
