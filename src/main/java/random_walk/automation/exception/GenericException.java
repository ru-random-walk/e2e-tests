package random_walk.automation.exception;

import lombok.Getter;
import random_walk.automation.exception.model.DefaultErrorResponse;

import java.io.IOException;

@Getter
public class GenericException extends IOException {

    private final DefaultErrorResponse defaultErrorResponse;

    public GenericException(DefaultErrorResponse defaultErrorResponse) {
        super(defaultErrorResponse.getStatus() + " : " + defaultErrorResponse.getMessage());
        this.defaultErrorResponse = defaultErrorResponse;
    }
}
