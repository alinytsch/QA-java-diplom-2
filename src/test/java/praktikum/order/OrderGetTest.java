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

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderGetTest {

    private String accessToken;
    private final UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        User user = new User(generateEmail(), "123456", "OrderViewer");
        Response response = userApi.createUser(user);
        accessToken = response.jsonPath().getString("accessToken");

        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6d");
        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(new OrderGetTest.OrderRequest(ingredients))
                .post("https://stellarburgers.nomoreparties.site/api/orders");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersWithAuth() {
        given()
                .header("Authorization", accessToken)
                .get("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", not(empty()));
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getOrdersWithoutAuth() {
        given()
                .get("https://stellarburgers.nomoreparties.site/api/orders")
                .then()
                .statusCode(401)
                .body("message", containsString("You should be authorised"));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }

    private static class OrderRequest {
        public List<String> ingredients;

        public OrderRequest(List<String> ingredients) {
            this.ingredients = ingredients;
        }
    }
}
