package random_walk.automation.exception;

import lombok.Getter;
import random_walk.automation.exception.model.DefaultGraphqlErrorResponse;

import java.io.IOException;

@Getter
public class GraphqlException extends IOException {

    private final DefaultGraphqlErrorResponse defaultGraphqlErrorResponse;

    public GraphqlException(DefaultGraphqlErrorResponse defaultGraphqlErrorResponse) {
        super(defaultGraphqlErrorResponse.getExtensions().getClassification() + " : " + defaultGraphqlErrorResponse.getMessage());
        this.defaultGraphqlErrorResponse = defaultGraphqlErrorResponse;
    }
}
