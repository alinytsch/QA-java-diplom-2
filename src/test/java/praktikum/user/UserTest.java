package praktikum.user;

import io.qameta.allure.junit4.AllureJunit4;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import praktikum.client.UserApi;
import praktikum.model.User;
import praktikum.model.UserCredentials;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class UserTest {

    private final UserApi api = new UserApi();
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        testUser = new User(generateEmail(), "123456", "TestUser");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            api.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {
        Response response = api.createUser(testUser);
        response.then().statusCode(200).body("success", is(true));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    public void createExistingUserTest() {
        api.createUser(testUser);
        Response response = api.createUser(testUser);
        response.then().statusCode(403).body("message", containsString("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без одного обязательного поля")
    public void createUserMissingFieldTest() {
        User userWithoutEmail = new User(null, "123456", "TestUser");
        Response response = api.createUser(userWithoutEmail);
        response.then().statusCode(403).body("success", is(false));
    }

    @Test
    @DisplayName("Логин с корректными данными")
    public void loginWithValidCredentialsTest() {
        api.createUser(testUser);
        UserCredentials creds = new UserCredentials(testUser.getEmail(), testUser.getPassword());
        Response response = api.login(creds);
        response.then().statusCode(200).body("accessToken", notNullValue());
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWithWrongPasswordTest() {
        api.createUser(testUser);
        UserCredentials creds = new UserCredentials(testUser.getEmail(), "wrongpass");
        Response response = api.login(creds);
        response.then().statusCode(401).body("message", containsString("email or password are incorrect"));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }
}
