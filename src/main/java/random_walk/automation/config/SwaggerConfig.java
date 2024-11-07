package random_walk.automation.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

import java.util.function.Supplier;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static ru.random_walk.swagger.auth_service.invoker.GsonObjectMapper.gson;

public class SwaggerConfig {
    public static Supplier<RequestSpecBuilder> getSupplierWithUri(String baseUri) {
        RestAssuredConfig config = RestAssuredConfig.config()
                .objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson()));
        return () -> new RequestSpecBuilder().setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(config);
    }
}
