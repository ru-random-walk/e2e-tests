package random_walk.automation.exception.model;

import lombok.Data;

@Data
public class DefaultErrorResponse {
    private String message;

    private Integer status;
}
