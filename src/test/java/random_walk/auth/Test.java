package random_walk.auth;

import org.junit.jupiter.api.Disabled;

public class Test extends AuthTest {

    @org.junit.jupiter.api.Test
    @Disabled
    void f() {
        var token = authServiceApi.getAuthTokens("").getAccessToken();
        authServiceApi.logout(token);
    }
}
