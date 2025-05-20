package praktikum.user;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserApi;
import praktikum.model.User;
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
    @DisplayName("Создание пользователя без email")
    public void createUserMissingEmailTest() {
        User user = new User(null, "123456", "TestUser");
        api.createUser(user).then().statusCode(403).body("success", is(false));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserMissingPasswordTest() {
        User user = new User(testUser.getEmail(), null, "TestUser");
        api.createUser(user).then().statusCode(403).body("success", is(false));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserMissingNameTest() {
        User user = new User(testUser.getEmail(), "123456", null);
        api.createUser(user).then().statusCode(403).body("success", is(false));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }
}
