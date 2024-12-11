package random_walk.automation.api.auth.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import random_walk.automation.api.auth.models.ExchangeAuthCodeResponse;
import random_walk.automation.api.auth.models.RefreshAccessTokenResponse;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Service
public class GoogleAccessTokenApi {

    @Value("${api.google-oauth2.email}")
    private String googleUserEmail;

    @Value("${api.google-oauth2.password}")
    private String googleUserPassword;

    @Step("Получаем актуальный access_token по refresh_token в google OAuth2 API")
    public String getActualGoogleAccessToken(String refreshToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("token_uri", "https://oauth2.googleapis.com/token");
        params.put("refresh_token", refreshToken);

        var responseBody = given().header("Content-Type", "application/json")
                .body(params)
                .post("https://developers.google.com/oauthplayground/refreshAccessToken")
                .as(RefreshAccessTokenResponse.class);
        return responseBody.getAccessToken();
    }

    @Step("Меняем auth_code на access и refresh токены")
    public ExchangeAuthCodeResponse exchangeAuthCode(String authCode) {
        var params = Map.of("token_uri", "https://oauth2.googleapis.com/token", "code", authCode);

        return given().header("Content-Type", "application/json")
                .body(params)
                .post("https://developers.google.com/oauthplayground/exchangeAuthCode")
                .as(ExchangeAuthCodeResponse.class);
    }

    @Step("Получаем актуальный authorization_code пользователя в google")
    public String getGoogleAuthorizationCode() {
        System.setProperty("webdriver.chrome.driver", "/Users/dmi.a.petrov/Downloads/chromedriver-mac-arm64/chromedriver");

        var option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        var driver = new ChromeDriver(option);

        var wait = new WebDriverWait(driver, Duration.of(10L, ChronoUnit.SECONDS));

        var authUri = getAuthorizationUrl();
        driver.get(authUri);
        driver.findElement(By.id("identifierId")).sendKeys(googleUserEmail);
        driver.findElement(By.id("identifierNext")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Passwd")));

        driver.findElement(By.name("Passwd")).sendKeys(googleUserPassword);
        driver.findElement(By.id("passwordNext")).click();
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(., 'Продолжить')]"))).click();
        } catch (Exception e) {
            System.out.println("Кнопка продолжить не появилась, продолжаем выполнение");
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exchangeCode")));
        var authCode = driver.findElement(By.id("auth_code")).getAttribute("value");
        driver.quit();
        return authCode;
    }

    @Step("Получаем url для авторизации в google")
    private String getAuthorizationUrl() {
        final String CLIENT_ID = "407408718192.apps.googleusercontent.com";
        final String REDIRECT_URI = "https://developers.google.com/oauthplayground";
        final List<String> SCOPES = List
                .of("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile");

        return new GoogleAuthorizationCodeRequestUrl(CLIENT_ID, REDIRECT_URI, SCOPES).build();
    }
}
