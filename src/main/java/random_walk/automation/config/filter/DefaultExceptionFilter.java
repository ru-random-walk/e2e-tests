package random_walk.automation.config.filter;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import random_walk.automation.exception.GenericException;
import random_walk.automation.exception.model.DefaultErrorResponse;

@Slf4j
@Data
@NoArgsConstructor
public class DefaultExceptionFilter implements Filter {

    @Override
    @SneakyThrows
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        final Response response = ctx.next(requestSpec, responseSpec);
        log.info("Response = {}, status code = {}", response.asString(), response.getStatusCode());
        DefaultErrorResponse errorResponse;
        if (response.getStatusCode() != 200) {
            try {
                errorResponse = new ObjectMapper().readValue(response.asString(), DefaultErrorResponse.class);
                errorResponse.setStatus(response.getStatusCode());
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            throw new GenericException(errorResponse);
        }
        return response;
    }
}
