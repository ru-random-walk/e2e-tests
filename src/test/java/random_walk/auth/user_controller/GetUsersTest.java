package random_walk.auth.user_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import random_walk.auth.AuthTest;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

class GetUsersTest extends AuthTest {

    @Test
    @DisplayName("Получение списка пользователей по их идентификаторам")
    void getUsersByIds() {
        var firstUserName = "Тест";
        var secondUserName = "Дмитрий";
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с именем \"Тест\"",
                () -> authUserFunctions.getUserByFullName(firstUserName));

        var secondTestUserDb = step(
                "AND: Получен пользователь с именем \"Дмитрий\"",
                () -> authUserFunctions.getUserByFullName(secondUserName));

        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях по их id",
                () -> authServiceApi.getUsersById(List.of(firstTestUserDb.getId(), secondTestUserDb.getId())));

        step("THEN: Информация о пользователях успешно получена", () -> {
            var firstUser = usersInfo.getContent()
                    .stream()
                    .filter(user -> user.getFullName().equals(firstUserName))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Не найден пользователь с именем " + firstUserName));
            var secondUser = usersInfo.getContent()
                    .stream()
                    .filter(user -> user.getFullName().equals(secondUserName))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Не найден пользователь с именем " + secondUserName));
            assertAll(
                    () -> assertEquals(2, usersInfo.getContent().size(), "Количество возвращенных пользователей соответствует"),
                    // проверяем первого юзера
                    () -> step(
                            "Проверяем пользователя с именем " + firstUserName,
                            () -> assertAll(
                                    () -> assertEquals(firstTestUserDb.getId(), firstUser.getId(), "Id соответствует ожидаемому"),
                                    () -> assertEquals(
                                            firstTestUserDb.getFullName(),
                                            firstUser.getFullName(),
                                            "Имя соответсвует ожидаемому"),
                                    () -> assertEquals(
                                            firstTestUserDb.getAvatar(),
                                            firstUser.getAvatar(),
                                            "Аватар соответствует ожидаемому"))),
                    () -> step(
                            "Проверяем пользователя с именем " + secondUserName,
                            () -> assertAll(
                                    () -> assertEquals(
                                            secondTestUserDb.getId(),
                                            secondUser.getId(),
                                            "Id соответствует ожидаемому"),
                                    () -> assertEquals(
                                            secondTestUserDb.getFullName(),
                                            secondUser.getFullName(),
                                            "Имя соответствует ожидаемому"),
                                    () -> assertEquals(
                                            secondTestUserDb.getAvatar(),
                                            secondUser.getAvatar(),
                                            "Аватар соответствует ожидаемому"))));
        });
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    public void getNonExistingUser() {
        var userUuid = step("GIVEN: Получен несуществующий пользователь", UUID::randomUUID);

        var clientsInfo = step(
                "WHEN: Получаем информацию о несуществующем пользователе",
                () -> authServiceApi.getUsersById(List.of(userUuid)));

        step(
                "THEN: Информация не была возвращена",
                () -> assertAll(
                        () -> assertTrue(clientsInfo.getContent().isEmpty(), "Список с информацией о пользователях пуст")));
    }
}
