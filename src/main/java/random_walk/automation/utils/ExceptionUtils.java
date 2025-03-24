package random_walk.automation.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.function.Executable;
import random_walk.automation.exception.GenericException;
import random_walk.automation.exception.model.DefaultErrorResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtils {

    @SneakyThrows
    public static DefaultErrorResponse toDefaultErrorResponse(Executable executable) {
        try {
            executable.execute();
        } catch (GenericException ex) {
            return ex.getDefaultErrorResponse();
        }
        return null;
    }
}
