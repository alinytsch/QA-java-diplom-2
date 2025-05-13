package praktikum.user;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.AllureJunit4;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import praktikum.client.UserApi;
import praktikum.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserUpdateTest {

    private final UserApi api = new UserApi();
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        testUser = new User(generateEmail(), "123456", "OriginalName");
        Response createResponse = api.createUser(testUser);
        accessToken = createResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            api.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Изменение имени с авторизацией")
    public void updateNameWithAuthTest() {
        Map<String, String> update = new HashMap<>();
        update.put("name", "UpdatedName");

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(update)
                .patch("https://stellarburgers.nomoreparties.site/api/auth/user")
                .then()
                .statusCode(200)
                .body("user.name", equalTo("UpdatedName"));
    }

    @Test
    @DisplayName("Изменение email с авторизацией")
    public void updateEmailWithAuthTest() {
        String newEmail = generateEmail();
        Map<String, String> update = new HashMap<>();
        update.put("email", newEmail);

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(update)
                .patch("https://stellarburgers.nomoreparties.site/api/auth/user")
                .then()
                .statusCode(200)
                .body("user.email", equalTo(newEmail.toLowerCase()));
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    public void updateWithoutAuthTest() {
        Map<String, String> update = new HashMap<>();
        update.put("name", "UnauthorizedChange");

        given()
                .header("Content-type", "application/json")
                .body(update)
                .patch("https://stellarburgers.nomoreparties.site/api/auth/user")
                .then()
                .statusCode(401)
                .body("message", containsString("You should be authorised"));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }
}
