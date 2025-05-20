package praktikum.order;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrderApi;
import praktikum.client.UserApi;
import praktikum.model.User;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class OrderGetTest {
    private String accessToken;
    private final UserApi userApi = new UserApi();
    private final OrderApi orderApi = new OrderApi();

    @Before
    public void setUp() {
        User user = new User(generateEmail(), "123456", "OrderViewer");
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
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersWithAuth() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6d");
        orderApi.createOrderWithAuth(accessToken, ingredients);

        orderApi.getOrders(accessToken)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", not(empty()));
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getOrdersWithoutAuth() {
        orderApi.getOrdersWithoutAuth()
                .then()
                .statusCode(401)
                .body("message", containsString("You should be authorised"));
    }

    private String generateEmail() {
        return "user_" + UUID.randomUUID() + "@yandex.ru";
    }
}