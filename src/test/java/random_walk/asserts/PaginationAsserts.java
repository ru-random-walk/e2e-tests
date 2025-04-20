package random_walk.asserts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.random_walk.swagger.auth_service.model.PageMetadata;

import javax.annotation.Nullable;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationAsserts {

    public static void checkPagination(Integer totalElements,
                                       @Nullable Integer page,
                                       @Nullable Integer size,
                                       PageMetadata metadata) {
        Long expectedPage = page == null ? 0L : page.longValue();
        Long expectedSize = size == null ? 20L : size.longValue();
        step(
                "Возвращенная информация о пагинации соответствует ожидаемой",
                () -> assertAll(
                        () -> assertEquals(expectedPage, metadata.getNumber()),
                        () -> assertEquals(expectedSize, metadata.getSize()),
                        () -> assertEquals(totalElements.longValue(), metadata.getTotalElements()),
                        () -> assertEquals(
                                metadata.getTotalPages(),
                                (metadata.getTotalElements() + metadata.getSize() - 1) / metadata.getSize())));
    }

    public static void checkPagination(Integer totalElements,
                                       @Nullable Integer page,
                                       @Nullable Integer size,
                                       ru.random_walk.swagger.chat_service.model.PageMetadata metadata) {
        Long expectedPage = page == null ? 0L : page.longValue();
        Long expectedSize = size == null ? 20L : size.longValue();
        step(
                "Возвращенная информация о пагинации соответствует ожидаемой",
                () -> assertAll(
                        () -> assertEquals(expectedPage, metadata.getNumber()),
                        () -> assertEquals(expectedSize, metadata.getSize()),
                        () -> assertEquals(totalElements.longValue(), metadata.getTotalElements()),
                        () -> assertEquals(
                                metadata.getTotalPages(),
                                (metadata.getTotalElements() + metadata.getSize() - 1) / metadata.getSize())));
    }
}
