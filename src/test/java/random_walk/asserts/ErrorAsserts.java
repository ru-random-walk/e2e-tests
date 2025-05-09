package random_walk.asserts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import random_walk.automation.exception.model.DefaultErrorResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorAsserts {

    public static void checkError(DefaultErrorResponse errorResponse, Integer errorCode, String errorMessage) {
        assertAll(
                () -> assertThat("Статус ответа соответствует ожидаемому", errorResponse.getStatus(), equalTo(errorCode)),
                () -> assertThat(
                        "Сообщение об ошибке соответствует ожидаемому",
                        errorResponse.getMessage(),
                        equalTo(errorMessage)));
    }
}
