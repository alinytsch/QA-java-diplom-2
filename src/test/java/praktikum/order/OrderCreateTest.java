package praktikum.order;

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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderCreateTest {

    private String accessToken;
    private final UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        User user = new User(generateEmail(), "123456", "OrderTestUser");
        Response response = userApi.createUser(user);
        accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredients() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa70");

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(Collections.singletonMap("ingredients", ingredients))
                .post("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6d");

        given()
                .header("Content-type", "application/json")
                .body(Collections.singletonMap("ingredients", ingredients))
                .post("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(Collections.singletonMap("ingredients", Collections.emptyList()))
                .post("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(400)
                .body("message", containsString("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным ингредиентом")
    public void createOrderWithInvalidIngredient() {
        List<String> ingredients = List.of("invalid_hash");

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(Collections.singletonMap("ingredients", ingredients))
                .post("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }
}
