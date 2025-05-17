package random_walk.auth;

public class Test extends AuthTest {

    @org.junit.jupiter.api.Test
    void f() {
        var token = authServiceApi.getAuthTokens(
                "")
                .getAccessToken();
        authServiceApi.logout(token);
    }
}
