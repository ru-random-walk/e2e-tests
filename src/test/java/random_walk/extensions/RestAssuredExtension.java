package random_walk.extensions;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import random_walk.automation.config.filter.DefaultExceptionFilter;

import java.util.Arrays;

public class RestAssuredExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        Filter filter = new DefaultExceptionFilter();
        RestAssured.reset();
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        RestAssured
                .filters(Arrays.asList(new AllureRestAssured(), new RequestLoggingFilter(), new ResponseLoggingFilter(), filter));
    }
}
