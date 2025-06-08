package random_walk.automation.exception.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultGraphqlErrorResponse {
    private String message;

    private Extensions extensions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Extensions {
        private String classification;
    }
}
