package random_walk.auth.user_controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.NotFoundException;
import random_walk.auth.AuthTest;
import random_walk.automation.domain.User;
import ru.random_walk.swagger.auth_service.model.UserDto;
import ru.testit.annotations.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static random_walk.asserts.PaginationAsserts.checkPagination;
import static random_walk.automation.domain.enums.UserRoleEnum.AUTOTEST_USER;
import static random_walk.automation.domain.enums.UserRoleEnum.TEST_USER;
import static random_walk.automation.util.DtoGeneratorUtils.generateUserDto;

class GetUsersTest extends AuthTest {

    private User firstUserInfo;

    private User secondUserInfo;

    @Test
    @ExternalId("auth_service.get_users_by_id")
    @DisplayName("Получение списка пользователей по их идентификаторам")
    void getUsersByIds() {
        givenStep();

        var firstTestUserDb = authUserFunctions.getById(firstUserInfo.getUuid());

        var secondTestUserDb = authUserFunctions.getById(secondUserInfo.getUuid());

        Integer page = null;
        Integer size = null;
        var usersList = List.of(firstUserInfo.getUuid(), secondUserInfo.getUuid());
        var usersInfo = authServiceApi.getUsersById(usersList, size, page, null);

        var firstUser = usersInfo.getContent()
                .stream()
                .filter(user -> Objects.equals(user.getId(), firstUserInfo.getUuid()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с именем " + firstUserInfo.getName()));
        var secondUser = usersInfo.getContent()
                .stream()
                .filter(user -> Objects.equals(user.getId(), secondUserInfo.getUuid()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с именем " + secondUserInfo.getName()));
        thenStep();

        assertAll(
                () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                () -> checkUserInfo(firstUser, generateUserDto(firstTestUserDb)),
                () -> checkUserInfo(secondUser, generateUserDto(secondTestUserDb)));

    }

    @Test
    @DisplayName("Получение списка пользователей с учетом ограничения size")
    void getUsersWithSizeLimit() {
        givenStep();

        var firstTestUserDb = authUserFunctions.getById(firstUserInfo.getUuid());

        var secondTestUserDb = authUserFunctions.getById(secondUserInfo.getUuid());

        Integer size = 1;
        Integer page = null;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = authServiceApi.getUsersById(usersList, size, page, null);

        var dbUser = Objects.equals(usersInfo.getContent().get(0).getFullName(), firstUserInfo.getName())
                ? firstTestUserDb
                : secondTestUserDb;

        thenStep();

        assertAll(
                () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                () -> checkUserInfo(usersInfo.getContent().get(0), generateUserDto(dbUser)));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = { 0, 1 })
    @DisplayName("Получение списка пользователей с учетом значения page =")
    void getUsersWithPageLimit(Integer page) {
        givenStep();

        var firstTestUserDb = authUserFunctions.getById(firstUserInfo.getUuid());

        var secondTestUserDb = authUserFunctions.getById(secondUserInfo.getUuid());

        Integer size = 1;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = authServiceApi.getUsersById(usersList, size, page, null);

        var dbUser = Objects.equals(usersInfo.getContent().get(0).getFullName(), firstUserInfo.getName())
                ? firstTestUserDb
                : secondTestUserDb;

        thenStep();

        assertAll(
                () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                () -> checkUserInfo(usersInfo.getContent().get(0), generateUserDto(dbUser)));
    }

    @ParameterizedTest(name = "порядке {0}")
    @ValueSource(strings = { "asc", "desc" })
    @DisplayName("Получение списка пользователей с учетом сортировки по fullName")
    void getUsersWithFullNameSortParam(String sortOrder) {
        givenStep();

        var firstTestUserDb = authUserFunctions.getById(firstUserInfo.getUuid());

        var secondTestUserDb = authUserFunctions.getById(secondUserInfo.getUuid());

        Integer size = null;
        Integer page = 0;
        String sort = "fullName," + sortOrder;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = authServiceApi.getUsersById(usersList, size, page, sort);

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

        thenStep();

        assertAll(
                () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                () -> assertEquals(finalSortedUsers, usersInfo.getContent()));
    }

    @ParameterizedTest(name = "порядке {0}")
    @ValueSource(strings = { "asc", "desc" })
    @DisplayName("Получение списка пользователей с учетом сортировки по id")
    void getUsersWithIdSortParam(String sortOrder) {
        givenStep();

        var firstTestUserDb = authUserFunctions.getById(firstUserInfo.getUuid());

        var secondTestUserDb = authUserFunctions.getById(secondUserInfo.getUuid());

        Integer size = null;
        Integer page = 0;
        String sort = "id," + sortOrder;
        var usersList = List.of(firstTestUserDb.getId(), secondTestUserDb.getId());
        var usersInfo = authServiceApi.getUsersById(usersList, size, page, sort);

        var sortedUsers = List.of(generateUserDto(firstTestUserDb), generateUserDto(secondTestUserDb));
        if (sortOrder.equals("asc")) {
            sortedUsers = sortedUsers.stream().sorted(Comparator.comparing(UserDto::getId, Comparator.naturalOrder())).toList();
        } else {
            sortedUsers = sortedUsers.stream().sorted(Comparator.comparing(UserDto::getId, Comparator.reverseOrder())).toList();
        }
        List<UserDto> finalSortedUsers = sortedUsers;

        thenStep();

        assertAll(
                () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()),
                () -> assertEquals(finalSortedUsers, usersInfo.getContent()));
    }

    @Test
    @DisplayName("Получение пустой страницы с пользователями")
    void getUsersInEmptyPage() {
        givenStep();

        var firstTestUserDb = authUserFunctions.getById(firstUserInfo.getUuid());

        Integer size = null;
        Integer page = 1;
        var usersList = List.of(firstTestUserDb.getId());
        var usersInfo = authServiceApi.getUsersById(usersList, size, page, null);

        thenStep();

        assertAll(
                () -> assertTrue(usersInfo.getContent().isEmpty()),
                () -> checkPagination(usersList.size(), page, size, usersInfo.getPage()));
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    void getNonExistingUser() {
        var userUuid = UUID.randomUUID();

        var usersInfo = authServiceApi.getUsersById(List.of(userUuid), null, null, null);

        assertAll(() -> assertTrue(usersInfo.getContent().isEmpty(), "Список с информацией о пользователях пуст"));
    }

    private void checkUserInfo(UserDto givenUserInfo, UserDto expectedUserInfo) {
        assertEquals(expectedUserInfo, givenUserInfo);
    }

    @Step
    @Title("GIVEN: Получена информация о тестовых пользователях")
    @Description("Пользователи - 58e953ef-0153-4918-9a26-17bcb2213c12 и 490689d5-4e63-4724-8ab5-4fb32750b263")
    public void givenStep() {
        firstUserInfo = userConfigService.getUserByRole(TEST_USER);
        secondUserInfo = userConfigService.getUserByRole(AUTOTEST_USER);
    }

    @Step
    @Title("THEN: Информация о пользователях успешно получена")
    public void thenStep() {
    }
}
