package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    private static final String ORDER_ENDPOINT = "/orders";

    public Response createOrderWithAuth(String token, List<String> ingredients) {
        return given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(Collections.singletonMap("ingredients", ingredients))
                .post(BASE_URL + ORDER_ENDPOINT);
    }

    public Response createOrderWithoutAuth(List<String> ingredients) {
        return given()
                .header("Content-type", "application/json")
                .body(Collections.singletonMap("ingredients", ingredients))
                .post(BASE_URL + ORDER_ENDPOINT);
    }

    public Response getOrders(String token) {
        return given()
                .header("Authorization", token)
                .get(BASE_URL + ORDER_ENDPOINT);
    }

    public Response getOrdersWithoutAuth() {
        return given()
                .get(BASE_URL + ORDER_ENDPOINT);
    }
}