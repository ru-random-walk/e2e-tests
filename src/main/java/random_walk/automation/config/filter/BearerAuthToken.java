package random_walk.automation.config.filter;

import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.spi.AuthFilter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BearerAuthToken implements AuthFilter {

    private final String token;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        requestSpec.replaceHeader("AUTHORIZATION", "Bearer " + token);
        return ctx.next(requestSpec, responseSpec);
    }
}
