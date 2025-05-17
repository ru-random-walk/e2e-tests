package random_walk.auth;

public class Test extends AuthTest {

    @org.junit.jupiter.api.Test
    void f() {
        var token = authServiceApi.getAuthTokens(
                "ya29.a0AW4XtxihghKIDLcQe5bYjC5x4Sllxsb1360Fv0AqSOdrmC17FilladlcGyH3oBArtPbfgYAN7rOBaUFwKOITuVpiMQGt-aYOMihZ11pHUkHZSFKV7zb1_w1qcGMxUn5FNPMy4a2Fv12vj9MXwMG79DnczQhgqXkNIBSzwngwaCgYKASMSARASFQHGX2Mis_V4_AzH6T44Xf0eRKw1XQ0175")
                .getAccessToken();
        authServiceApi.logout(token);
    }
}
