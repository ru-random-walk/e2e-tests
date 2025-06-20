package random_walk.asserts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import random_walk.automation.exception.model.DefaultErrorResponse;
import random_walk.automation.exception.model.DefaultGraphqlErrorResponse;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorAsserts {

    @Step
    @Title("THEN: Проверяем параметры полученной ошибки")
    @Description("Ошибка {errorCode} - {errorMessage}")
    public static void checkError(DefaultErrorResponse errorResponse, Integer errorCode, String errorMessage) {
        assertAll(
                () -> assertThat("Статус ответа соответствует ожидаемому", errorResponse.getStatus(), equalTo(errorCode)),
                () -> assertThat(
                        "Сообщение об ошибке соответствует ожидаемому",
                        errorResponse.getMessage(),
                        equalTo(errorMessage)));
    }

    @Step
    @Title("THEN: Проверяем параметры полученной ошибки")
    @Description("Ошибка {classification} - {errorMessage}")
    public static void checkGraphqlError(DefaultGraphqlErrorResponse defaultGraphqlErrorResponse,
                                         String classification,
                                         String errorMessage) {
        assertAll(
                () -> assertThat(
                        "Тип ошибки соответствует ожидаемому",
                        defaultGraphqlErrorResponse.getExtensions().getClassification(),
                        equalTo(classification)),
                () -> assertThat(
                        "Сообщение об ошибке соответствует ожидаемому",
                        defaultGraphqlErrorResponse.getMessage(),
                        equalTo(errorMessage)));
    }
}
