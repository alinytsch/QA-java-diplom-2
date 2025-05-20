package praktikum.user;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserApi;
import praktikum.model.User;
import praktikum.model.UserCredentials;
import java.util.UUID;
import static org.hamcrest.Matchers.*;

public class UserLoginTest {
    private final UserApi api = new UserApi();
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        testUser = new User(generateEmail(), "123456", "TestUser");
        api.createUser(testUser);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            api.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин с корректными данными")
    public void loginWithValidCredentialsTest() {
        UserCredentials creds = new UserCredentials(testUser.getEmail(), testUser.getPassword());
        Response response = api.login(creds);
        response.then().statusCode(200).body("accessToken", notNullValue());
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWithWrongPasswordTest() {
        UserCredentials creds = new UserCredentials(testUser.getEmail(), "wrongpass");
        api.login(creds)
                .then()
                .statusCode(401)
                .body("message", containsString("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверной почтой")
    public void loginWithWrongEmailTest() {
        UserCredentials creds = new UserCredentials("wrong@email.com", testUser.getPassword());
        api.login(creds)
                .then()
                .statusCode(401)
                .body("message", containsString("email or password are incorrect"));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }
}
