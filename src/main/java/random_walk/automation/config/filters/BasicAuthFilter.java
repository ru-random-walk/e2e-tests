package random_walk.automation.config.filters;

import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.spi.AuthFilter;
import lombok.AllArgsConstructor;

import java.util.Base64;

@AllArgsConstructor
public class BasicAuthFilter implements AuthFilter {
    private final String username;
    private final String password;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        String accessToken = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        requestSpec.replaceHeader("AUTHORIZATION", "Basic " + accessToken);
        return ctx.next(requestSpec, responseSpec);
    }
}
