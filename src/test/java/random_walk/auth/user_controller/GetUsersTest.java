package random_walk.auth.user_controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.NotFoundException;
import random_walk.auth.AuthTest;
import ru.random_walk.swagger.auth_service.model.UserDto;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static random_walk.asserts.PaginationAsserts.checkPagination;
import static random_walk.automation.domain.enums.UserRoleEnum.AUTOTEST_USER;
import static random_walk.automation.domain.enums.UserRoleEnum.TEST_USER;
import static random_walk.automation.util.DtoGeneratorUtils.generateUserDto;

class GetUsersTest extends AuthTest {

    @Test
    @DisplayName("Получение списка пользователей по их идентификаторам")
    void getUsersByIds() {
        var firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        var secondUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с id " + firstUserInfo.getUuid(),
                () -> authUserFunctions.getById(firstUserInfo.getUuid()));

        var secondTestUserDb = step(
                "AND: Получен пользователь с id " + secondUserInfo.getUuid(),
                () -> authUserFunctions.getById(secondUserInfo.getUuid()));

        Integer page = null;
        Integer size = null;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях по их id",
                () -> authServiceApi.getUsersById(usersList, size, page, null));

        step("THEN: Информация о пользователях успешно получена", () -> {
            var firstUser = usersInfo.getContent()
                    .stream()
                    .filter(user -> user.getFullName().equals(firstUserInfo.getName()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Не найден пользователь с именем " + firstUserInfo.getName()));
            var secondUser = usersInfo.getContent()
                    .stream()
                    .filter(user -> user.getFullName().equals(secondUserInfo.getName()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Не найден пользователь с именем " + secondUserInfo.getName()));
            assertAll(
                    () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                    () -> checkUserInfo(firstUserInfo.getName(), firstUser, generateUserDto(firstTestUserDb)),
                    () -> checkUserInfo(secondUserInfo.getName(), secondUser, generateUserDto(secondTestUserDb)));
        });
    }

    @Test
    @DisplayName("Получение списка пользователей с учетом ограничения size")
    void getUsersWithSizeLimit() {
        var firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        var secondUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с id " + firstUserInfo.getUuid(),
                () -> authUserFunctions.getById(firstUserInfo.getUuid()));

        var secondTestUserDb = step(
                "AND: Получен пользователь с именем " + secondUserInfo.getUuid(),
                () -> authUserFunctions.getById(secondUserInfo.getUuid()));

        Integer size = 1;
        Integer page = null;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях по их id с учетом параметра size",
                () -> authServiceApi.getUsersById(usersList, size, page, null));

        step("THEN: Получена информация только об одном пользователе", () -> {

            var dbUser = Objects.equals(usersInfo.getContent().get(0).getFullName(), firstUserInfo.getName())
                    ? firstTestUserDb
                    : secondTestUserDb;
            assertAll(
                    () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                    () -> checkUserInfo(dbUser.getFullName(), usersInfo.getContent().get(0), generateUserDto(dbUser)));
        });
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = { 0, 1 })
    @DisplayName("Получение списка пользователей с учетом значения page =")
    void getUsersWithPageLimit(Integer page) {
        var firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        var secondUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с id " + firstUserInfo.getUuid(),
                () -> authUserFunctions.getById(firstUserInfo.getUuid()));

        var secondTestUserDb = step(
                "AND: Получен пользователь с id " + secondUserInfo.getUuid(),
                () -> authUserFunctions.getById(secondUserInfo.getUuid()));

        Integer size = 1;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях по их id с учетом параметра page",
                () -> authServiceApi.getUsersById(usersList, size, page, null));

        step("THEN: Полученная о клиентах информация совпадает с ожидаемой", () -> {

            var dbUser = Objects.equals(usersInfo.getContent().get(0).getFullName(), firstUserInfo.getName())
                    ? firstTestUserDb
                    : secondTestUserDb;
            assertAll(
                    () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                    () -> checkUserInfo(dbUser.getFullName(), usersInfo.getContent().get(0), generateUserDto(dbUser)));
        });
    }

    @ParameterizedTest(name = "порядке {0}")
    @ValueSource(strings = { "asc", "desc" })
    @DisplayName("Получение списка пользователей с учетом сортировки по fullName")
    void getUsersWithFullNameSortParam(String sortOrder) {
        var firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        var secondUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с id " + firstUserInfo.getUuid(),
                () -> authUserFunctions.getById(firstUserInfo.getUuid()));

        var secondTestUserDb = step(
                "AND: Получен пользователь с id " + secondUserInfo.getUuid(),
                () -> authUserFunctions.getById(secondUserInfo.getUuid()));

        Integer size = null;
        Integer page = 0;
        String sort = "fullName," + sortOrder;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях по их id с учетом сортировки по fullName",
                () -> authServiceApi.getUsersById(usersList, size, page, sort));

        step("THEN: Порядок сортировки совпадает с ожидаемым", () -> {
            var sortedUsers = List.of(generateUserDto(firstTestUserDb), generateUserDto(secondTestUserDb));
            if (sortOrder.equals("asc")) {
                sortedUsers = sortedUsers.stream()
                        .sorted(Comparator.comparing(UserDto::getFullName, Comparator.naturalOrder()))
                        .toList();
            } else {
                sortedUsers = sortedUsers.stream()
                        .sorted(Comparator.comparing(UserDto::getFullName, Comparator.reverseOrder()))
                        .toList();
            }
            List<UserDto> finalSortedUsers = sortedUsers;
            assertAll(
                    () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                    () -> assertEquals(finalSortedUsers, usersInfo.getContent()));
        });
    }

    @ParameterizedTest(name = "порядке {0}")
    @ValueSource(strings = { "asc", "desc" })
    @DisplayName("Получение списка пользователей с учетом сортировки по id")
    void getUsersWithIdSortParam(String sortOrder) {
        var firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        var secondUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с id " + firstUserInfo.getUuid(),
                () -> authUserFunctions.getById(firstUserInfo.getUuid()));

        var secondTestUserDb = step(
                "AND: Получен пользователь с id " + secondUserInfo.getUuid(),
                () -> authUserFunctions.getById(secondUserInfo.getUuid()));

        Integer size = null;
        Integer page = 0;
        String sort = "id," + sortOrder;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях по их id с учетом сортировки по id",
                () -> authServiceApi.getUsersById(usersList, size, page, sort));

        step("THEN: Порядок сортировки совпадает с ожидаемым", () -> {
            var sortedUsers = List.of(generateUserDto(firstTestUserDb), generateUserDto(secondTestUserDb));
            if (sortOrder.equals("asc")) {
                sortedUsers = sortedUsers.stream()
                        .sorted(Comparator.comparing(UserDto::getId, Comparator.naturalOrder()))
                        .toList();
            } else {
                sortedUsers = sortedUsers.stream()
                        .sorted(Comparator.comparing(UserDto::getId, Comparator.reverseOrder()))
                        .toList();
            }
            List<UserDto> finalSortedUsers = sortedUsers;
            assertAll(
                    () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                    () -> assertEquals(finalSortedUsers, usersInfo.getContent()));
        });
    }

    @Test
    @DisplayName("Получение пустой страницы с пользователями")
    void getUsersInEmptyPage() {
        var firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        var firstTestUserDb = step(
                "GIVEN: Получен пользователь с id " + firstUserInfo.getUuid(),
                () -> authUserFunctions.getById(firstUserInfo.getUuid()));

        Integer size = null;
        Integer page = 1;
        var usersList = List.of(firstTestUserDb.getId());
        var usersInfo = step(
                "WHEN: Получаем информацию о пользователях на пустой странице",
                () -> authServiceApi.getUsersById(usersList, size, page, null));

        step(
                "THEN: При получении пустой страницы пользователей итоговый список пуст",
                () -> assertAll(
                        () -> assertTrue(usersInfo.getContent().isEmpty()),
                        () -> checkPagination(usersList.size(), page, size, usersInfo.getPage())));
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    void getNonExistingUser() {
        var userUuid = step("GIVEN: Получен несуществующий пользователь", UUID::randomUUID);

        var usersInfo = step(
                "WHEN: Получаем информацию о несуществующем пользователе",
                () -> authServiceApi.getUsersById(List.of(userUuid), null, null, null));

        step(
                "THEN: Информация не была возвращена",
                () -> assertAll(() -> assertTrue(usersInfo.getContent().isEmpty(), "Список с информацией о пользователях пуст")));
    }

    private void checkUserInfo(String username, UserDto givenUserInfo, UserDto expectedUserInfo) {
        step(
                "Полученные данные о пользователе " + username + " соответствуют ожидаемым",
                () -> assertEquals(expectedUserInfo, givenUserInfo));
    }
}
